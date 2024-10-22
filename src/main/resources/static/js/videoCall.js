// SockJS and STOMP for signaling
let socket = new SockJS('/ws');  // Connect to the WebSocket endpoint
let stompClient = Stomp.over(socket);  // STOMP protocol over the socket

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    // Subscribe to the topic for receiving messages
    stompClient.subscribe('/topic/video', function (messageOutput) {
        let message = JSON.parse(messageOutput.body);
        if (message.type === "offer") {
            handleOffer(message.sdp);
        } else if (message.type === "answer") {
            handleAnswer(message.sdp);
        }
    });
});

// Send a message to initiate the call
function sendCall() {
    stompClient.send("/app/call", {}, JSON.stringify({'message': 'Initiate Video Call'}));
}

// WebRTC logic for handling offers/answers
function handleOffer(offerSdp) {
    let peerConnection = new RTCPeerConnection();

    peerConnection.setRemoteDescription(new RTCSessionDescription({ type: "offer", sdp: offerSdp }));

    // Create an answer and send it back to the server
    peerConnection.createAnswer().then(answer => {
        peerConnection.setLocalDescription(answer);
        stompClient.send("/app/call", {}, JSON.stringify({ type: "answer", sdp: answer.sdp }));
    });
}

function handleAnswer(answerSdp) {
    let peerConnection = new RTCPeerConnection();

    // Here you'd apply the answer received to your peer connection
    peerConnection.setRemoteDescription(new RTCSessionDescription({ type: "answer", sdp: answerSdp }));
}

function initiateCall() {
    let peerConnection = new RTCPeerConnection();

    // Create SDP offer and send to server
    peerConnection.createOffer().then(offer => {
        peerConnection.setLocalDescription(offer);
        stompClient.send("/app/call", {}, JSON.stringify({ type: "offer", sdp: offer.sdp }));
    });
}
