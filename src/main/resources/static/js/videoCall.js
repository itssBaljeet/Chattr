// DOM Elements
const roomForm = document.getElementById('roomForm');
const roomCodeInput = document.getElementById('roomCode');
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');
const chatInput = document.getElementById('chatInput');
const sendButton = document.getElementById('sendButton');
const chatMessages = document.getElementById('chatMessages');

// State Object to manage variables
const callState = {
    roomCode: null,
    currentUsername: '',
    localStream: null,
    peerConnection: null,
    stompClient: null
};

async function init() {
    callState.currentUsername = await fetchUsername();
    console.log("Current Username:", callState.currentUsername);
}

init();

document.getElementById('initiateCallButton').addEventListener('click', startCall);
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
        callState.roomCode = inputRoomCode; // Store the room code
        document.getElementById("leaveRoomButton").disabled = false; // Enable the WebSocket button
        console.log("Room code set:", callState.roomCode);
        setupWebSocket(callState.roomCode);
    } else {
        alert("Please enter a room code before joining.");
    }
});

// Shutdown peer connection, websockets, and streams
function leaveRoom() {
    if (callState.stompClient && callState.stompClient.connected) {
        callState.stompClient.disconnect(() => {
            console.log('Disconnected from WebSocket server.');
            sendLeaveMessage();
        });
    }
    if (callState.peerConnection) {
        callState.peerConnection.close();
        callState.peerConnection = null;
        console.log('Peer connection closed.');
    }
    callState.localStream.getTracks().forEach(track => track.stop());
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



// Set up WebSocket connection and subscriptions
function setupWebSocket(roomCode) {
    return new Promise((resolve, reject) => {
        if (callState.stompClient && callState.stompClient.connected) {
            console.log("WebSocket is already connected.");
            return;
        }

        const socket = new SockJS('/ws');
        callState.stompClient = Stomp.over(socket);

        callState.stompClient.connect({}, function () {
            console.log('Connected to signaling server');
            callState.stompClient.subscribe(`/topic/call/${roomCode}`, message => handleSignalingData(JSON.parse(message.body)));
            callState.stompClient.subscribe(`/topic/chat/${roomCode}`, message => displayChatMessage(JSON.parse(message.body)));
            sendJoinMessage();
            resolve();
        }, reject);
    });
}

async function startCall() {
    try {
        console.log("Starting media and webRTC");
        callState.localStream = await navigator.mediaDevices.getUserMedia({video: true, audio: true});
        localVideo.srcObject = callState.localStream

        callState.peerConnection = new RTCPeerConnection(configuration)

        callState.localStream.getTracks().forEach(track =>
            callState.peerConnection.addTrack(track, callState.localStream)
        );

        callState.peerConnection.ontrack = (event) => {
            remoteVideo.srcObject = event.streams[0];
        };

        document.getElementById("sendOffer").disabled = false;

        console.log("Call initialized");
    } catch (error) {
        console.error("Error starting call:", error);
    }
}

// Send sdp offer to server
function sendOffer() {
    // Create and send offer
    console.log("Sending offer...")
    callState.peerConnection.createOffer()
        .then(offer => callState.peerConnection.setLocalDescription(offer))
        .then(() => sendSignalingData({ type: 'offer', sdp: callState.peerConnection.localDescription.sdp, sender: currentUsername, roomCode: callState.roomCode }))
        .catch(error => console.error('Error creating offer:', error));
}

// Send join message after establishing WebSocket and username
function sendJoinMessage() {
    sendChatMessage({
        sender: callState.currentUsername,
        type: 'JOIN'
    });
}

// Send join message after establishing WebSocket and username
function sendLeaveMessage() {
    sendChatMessage({
        sender: callState.currentUsername,
        type: 'LEAVE'
    });
}

// Handle incoming signaling data (offer, answer, candidate, chat)
function handleSignalingData(data) {
    console.log("---SENDER---" + data.sender)
    switch (data.type) {
        case 'offer':
            if (data.sender === callState.currentUsername) {
                console.log("refusing same offer")
                return;
            }
            handleOffer(data.sdp);
            break;
        case 'answer':
            if (data.sender === callState.currentUsername) {
                console.log("refusing same answer")
                return;
            }
            handleAnswer(data.sdp);
            break;
        case 'candidate':
            if (data.sender ===callState.currentUsername) {
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
    callState.peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp: offer }))
        .then(() => callState.peerConnection.createAnswer())
        .then(answer => callState.peerConnection.setLocalDescription(answer))
        .then(() => sendSignalingData({ type: 'answer', sdp: callState.peerConnection.localDescription.sdp, roomCode: callState.roomCode, sender: callState.currentUsername }))
        .catch(error => console.error('Error handling offer:', error));
}

// Handle WebRTC answer from the other peer
function handleAnswer(answer) {
    if (!callState.peerConnection.remoteDescription) { // Ensure remote description is not already set
        callState.peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp: answer }))
            .catch(error => console.error('Error setting remote description:', error));
    }
}


// Handle incoming ICE candidate
function handleCandidate(candidate) {
    callState.peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
        .catch(error => console.error('Error adding ICE candidate:', error));
}

// Send signaling data through WebSocket
function sendSignalingData(data) {
    callState.stompClient.send(`/app/signal/${callState.roomCode}`, {}, JSON.stringify(data));
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
            sender: callState.currentUsername,
            content: message
        });
        chatInput.value = '';
    }
}

// Send chat message through WebSocket
function sendChatMessage(data) {
    callState.stompClient.send(`/app/chat/${callState.roomCode}/sendMessage`, {}, JSON.stringify(data));
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
