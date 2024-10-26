// DOM Elements
const roomForm = document.getElementById('roomForm');
const roomCodeInput = document.getElementById('roomCode');
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');
const chatInput = document.getElementById('chatInput');
const sendButton = document.getElementById('sendButton');
const chatMessages = document.getElementById('chatMessages');

let roomCode = null;
let currentUsername = '';
fetchUsername().then(username => {
    currentUsername = username;
    console.log("Current Username:", currentUsername); // This will log the string value
}).catch(error => {
    console.error("Error setting currentUsername:", error);
});
let localStream;
let peerConnection;
let stompClient = null;

document.getElementById('startMediaButton').addEventListener('click', setupMedia);
document.getElementById('initiateCallButton').addEventListener('click', setupPeerConnection);
document.getElementById('leaveRoomButton').addEventListener('click', leaveRoom);
document.getElementById('sendOffer').addEventListener('click', sendOffer);

// WebRTC Configuration
const configuration = {
    iceServers: [
        { urls: 'stun:stun.l.google.com:19302' },  // Google STUN server
        {
            urls: 'turn:global.turn.twilio.com:3478?transport=udp',
            username: 'f9dca00024fa8f8e11a22b40a3259d1946657ce52f6399fdb84bb0b53f82bf3c',       // From Twilio
            credential: '1a5a81dc75e34a9c4a81bc46fe963faa'    // From Twilio
        }
    ]
};

// Initialize Room Connection
roomForm.addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form submission
    const inputRoomCode = roomCodeInput.value.trim();


    if (inputRoomCode) {
        roomCode = inputRoomCode; // Store the room code
        document.getElementById("connectWebSocketButton").disabled = false;
        document.getElementById("leaveRoomButton").disabled = false; // Enable the WebSocket button
        console.log("Room code set:", roomCode);
    } else {
        alert("Please enter a room code before joining.");
    }
});

// Connect to websocket
document.getElementById("connectWebSocketButton").addEventListener("click", function () {
    if (!roomCode) {
        alert("Room code is required to connect to WebSocket.");
        return;
    }

    // Call the setupWebSocket function with the room code
    setupWebSocket(roomCode);

});

// Shutdown peer connection, websockets, and streams
function leaveRoom() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log('Disconnected from WebSocket server.');
            sendLeaveMessage();
        });
    }
    if (peerConnection) {
        peerConnection.close();
        peerConnection = null;
        console.log('Peer connection closed.');
    }
    localStream.getTracks().forEach(track => track.stop());
    localVideo.srcObject = null;
    remoteVideo.srcObject = null;
}

// Fetch current username from the server
async function fetchUsername() {
    try {
        const response = await fetch('/api/users/current-username');
        return await response.text();
    } catch (error) {
        console.error('Error fetching username:', error);
        throw error;
    }
}

// Get media stream (camera and microphone)
async function setupMedia() {
    try {
        localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        localVideo.srcObject = localStream;
        document.getElementById("initiateCallButton").disabled = false;
        console.log('Local stream initialized');
    } catch (error) {
        console.error('Error accessing media devices:', error);
        throw error;
    }
}

// Set up WebSocket connection and subscriptions
function setupWebSocket(roomCode) {
    return new Promise((resolve, reject) => {
        if (stompClient && stompClient.connected) {
            console.log("WebSocket is already connected.");
            return;
        }

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function () {
            console.log('Connected to signaling server');
            stompClient.subscribe(`/topic/call/${roomCode}`, message => handleSignalingData(JSON.parse(message.body)));
            stompClient.subscribe(`/topic/chat/${roomCode}`, message => displayChatMessage(JSON.parse(message.body)));
            sendJoinMessage();
            resolve();
        }, reject);
    });
}

// Set up WebRTC peer connection and event handlers
function setupPeerConnection() {
    console.log("WebRTC Connection Created")
    peerConnection = new RTCPeerConnection(configuration);

    // Add local stream to peer connection
    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    // Event: ICE candidate generation
    peerConnection.onicecandidate = event => {
        if (event.candidate) sendSignalingData({ type: 'candidate', candidate: event.candidate, sender: currentUsername });
    };

    document.getElementById("sendOffer").disabled = false;

    // Event: Remote stream received
    peerConnection.ontrack = event => {
        remoteVideo.srcObject = event.streams[0];
        console.log('Remote stream received');
    };
}

// Send sdp offer to server
function sendOffer() {
    // Create and send offer
    console.log("Sending offer...")
    peerConnection.createOffer()
        .then(offer => peerConnection.setLocalDescription(offer))
        .then(() => sendSignalingData({ type: 'offer', sdp: peerConnection.localDescription.sdp, sender: currentUsername, roomCode: roomCode }))
        .catch(error => console.error('Error creating offer:', error));
}

// Send join message after establishing WebSocket and username
function sendJoinMessage() {
    sendChatMessage({
        sender: currentUsername,
        type: 'JOIN'
    });
}

// Send join message after establishing WebSocket and username
function sendLeaveMessage() {
    sendChatMessage({
        sender: currentUsername,
        type: 'LEAVE'
    });
}

// Handle incoming signaling data (offer, answer, candidate, chat)
function handleSignalingData(data) {
    console.log("---SENDER---" + data.sender)
    switch (data.type) {
        case 'offer':
            if (data.sender === currentUsername) {
                console.log("refusing same offer")
                return;
            }
            handleOffer(data.sdp);
            break;
        case 'answer':
            if (data.sender === currentUsername) {
                console.log("refusing same answer")
                return;
            }
            handleAnswer(data.sdp);
            break;
        case 'candidate':
            if (data.sender === currentUsername) {
                console.log("refusing same candidate")
                return;
            }
            handleCandidate(data.candidate);
            break;
        case 'CHAT':
            displayChatMessage(data.message);
            break;
        default:
            console.warn('Unknown signaling data type:', data.type);
    }
}

// Handle incoming WebRTC offer
function handleOffer(offer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp: offer }))
        .then(() => peerConnection.createAnswer())
        .then(answer => peerConnection.setLocalDescription(answer))
        .then(() => sendSignalingData({ type: 'answer', sdp: peerConnection.localDescription.sdp, roomCode: roomCode, sender: currentUsername }))
        .catch(error => console.error('Error handling offer:', error));
}

// Handle WebRTC answer from the other peer
function handleAnswer(answer) {
    if (!peerConnection.remoteDescription) { // Ensure remote description is not already set
        peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp: answer }))
            .catch(error => console.error('Error setting remote description:', error));
    }
}


// Handle incoming ICE candidate
function handleCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
        .catch(error => console.error('Error adding ICE candidate:', error));
}

// Send signaling data through WebSocket
function sendSignalingData(data) {
    stompClient.send(`/app/signal/${roomCode}`, {}, JSON.stringify(data));
}

// Chat Message Functions
sendButton.addEventListener('click', sendChat);
chatInput.addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendChat();
    }
});

function sendChat() {
    const message = chatInput.value.trim();
    if (message) {
        sendChatMessage({
            type: 'CHAT',
            sender: currentUsername,
            content: message
        });
        chatInput.value = '';
    }
}

// Send chat message through WebSocket
function sendChatMessage(data) {
    stompClient.send(`/app/chat/${roomCode}/sendMessage`, {}, JSON.stringify(data));
}

// Display incoming chat messages
function displayChatMessage(message) {
    const { sender, type, content } = message;
    const li = document.createElement('li');
    console.log("Creating chat message and appending...")

    if (type === 'JOIN') {
        li.textContent = `${sender} joined!`;
    } else if (type === 'CHAT') {
        li.textContent = `${sender}: ${content}`;
    } else if (type === 'LEAVE') {
        li.textContent = `${sender} left!`
    } else {
        console.warn('Unknown message type:', type);
        return;
    }

    chatMessages.appendChild(li);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}
