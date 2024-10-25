const roomForm = document.getElementById('roomForm');
const roomCodeInput = document.getElementById('roomCode');
let roomCode;

// Video elements
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

// WebRTC and WebSocket variables
let localStream;
let peerConnection;
let stompClient = null;

// WebRTC configuration for STUN server
const configuration = {
    iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
};

// Handle room form submission to start the call
roomForm.addEventListener('submit', function(event) {
    event.preventDefault();
    roomCode = roomCodeInput.value;
    connect(roomCode);  // Call connect to set up WebSocket connection when the room is submitted
});

let currentUsername = ''; // Variable to store the current user's username


// Initialize WebSocket connection and subscribe to the room
function connect(roomCode) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        console.log('Connected to signaling server');
        // Subscribe to the signaling channel for the room
        stompClient.subscribe('/topic/call/' + roomCode, function (message) {
            const data = JSON.parse(message.body);
            handleSignalingData(data);
        });

        // Subscribe to the public chat messages
        stompClient.subscribe('/topic/public', function (message) {
            const chatMessage = JSON.parse(message.body);
            onMessageReceived(chatMessage);  // Handle incoming chat messages
        });

        getCurrentUsername().then(username => {
            // Tell your username to the server
            stompClient.send("/app/chat.addUser",
                {},
                JSON.stringify({sender: username, type: 'JOIN'})
            );

            // Get media (camera/mic) and start WebRTC
            getMedia().then(() => {
                initiateWebRTC();
            }).catch(error => {
                console.error('Media access error: ', error);
            });
        });
    });
}

// Function to get the current username from the server
function getCurrentUsername() {
    return fetch('/api/users/current-username')
        .then(response => response.text())
        .then(username => {
            currentUsername = username;
            console.log('Current username:', currentUsername);
            return username; // Return the username string
        })
        .catch(error => {
            console.error('Error fetching username:', error);
            throw error; // Rethrow the error to be caught by the caller
        });
}

// Get user's webcam and microphone
function getMedia() {
    return navigator.mediaDevices.getUserMedia({ video: true, audio: true })
        .then(stream => {
            localVideo.srcObject = stream;
            localStream = stream;
            console.log('Local stream initialized');
        })
        .catch(error => {
            console.error('Error accessing media devices:', error);
            throw error; // Propagate error to prevent further setup
        });
}

// Set up WebRTC peer connection and event handlers
function initiateWebRTC() {
    peerConnection = new RTCPeerConnection(configuration);

    // Add local stream tracks to peer connection
    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    // Handle ICE candidates
    peerConnection.onicecandidate = event => {
        if (event.candidate) {
            sendSignalingData({
                type: 'candidate',
                candidate: event.candidate
            });
        }
    };

    // Handle remote stream
    peerConnection.ontrack = event => {
        remoteVideo.srcObject = event.streams[0];
        console.log('Remote stream received');
    };

    // Create and send offer to the other peer
    peerConnection.createOffer()
        .then(offer => peerConnection.setLocalDescription(offer))
        .then(() => {
            sendSignalingData({
                type: 'offer',
                sdp: peerConnection.localDescription.sdp
            });
        })
        .catch(error => console.error('Error creating offer:', error));
}


// Handle incoming signaling data (offer, answer, candidate, chat)
function handleSignalingData(data) {
    if (data.type === 'offer') {
        handleOffer(data.sdp);
    } else if (data.type === 'answer') {
        handleAnswer(data.sdp);
    } else if (data.type === 'candidate') {
        handleCandidate(data.candidate);
    } else if (data.type === 'chat') {
        displayChatMessage(data.sender, data.message);
    }
}

// Handle WebRTC offer from the other peer
function handleOffer(offer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'offer', sdp: offer }))
        .then(() => peerConnection.createAnswer())
        .then(answer => peerConnection.setLocalDescription(answer))
        .then(() => {
            sendSignalingData({
                type: 'answer',
                sdp: peerConnection.localDescription.sdp
            });
        })
        .catch(error => console.error('Error handling offer:', error));
}

// Handle WebRTC answer from the other peer
function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription({ type: 'answer', sdp: answer }))
        .catch(error => console.error('Error setting remote description:', error));
}

// Handle incoming ICE candidates
function handleCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate))
        .catch(error => console.error('Error adding ICE candidate:', error));
}

// Send signaling data through WebSocket, including roomCode in the endpoint
function sendSignalingData(data) {
    stompClient.send(`/app/signal/${roomCode}`, {}, JSON.stringify(data));
}

// Chat functionality
const chatInput = document.getElementById('chatInput');
const sendButton = document.getElementById('sendButton');
const chatMessages = document.getElementById('chatMessages');

// Chat functionality
sendButton.addEventListener('click', sendChatMessage);
chatInput.addEventListener('keypress', function(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        sendChatMessage();
    }
});

function sendChatMessage() {
    const message = chatInput.value.trim();
    if (message) {
        let data = {
            type: 'CHAT',
            sender: currentUsername,
            content: message
        };
        stompClient.send(`/app/chat.sendMessage`, {}, JSON.stringify(data));
        displayChatMessage(currentUsername, message);
        chatInput.value = '';
    }
}

// Display chat message in the chat window
function displayChatMessage(sender, message) {
    if (sender === currentUsername) {
        return;
    }

    const li = document.createElement('li');
    li.textContent = `${sender}: ${message}`;
    chatMessages.appendChild(li);
    chatMessages.scrollTop = chatMessages.scrollHeight; // Auto-scroll to the bottom
}

function onMessageReceived(message) {
    // The payload is already a JavaScript object

    // Create a new list item (li) element
    let messageElement = document.createElement('li');

    // Set the content of the message
    if (message.type === 'JOIN') {
        messageElement.textContent = message.sender + ' joined!';
    } else if (message.type === 'CHAT') {
        let text = message.sender + ': ' + message.content;
        messageElement.textContent = text;
    } else {
        // Handle other message types or log unknown types
        console.log('Unknown message type:', message.type);
        return; // Don't add unknown message types to the chat
    }

    // Append the new message to the messageArea (ul)
    document.querySelector('#chatMessages').appendChild(messageElement);
}
