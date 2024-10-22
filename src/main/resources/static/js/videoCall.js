let socket = new WebSocket("ws://localhost:8080/call")

socket.onmessage = function(event) {
    let message = JSON.parse(event.data);

    if (message.type === "offer") {
        // Handle the offer
    }
}

function initiateCall() {
    let peerConnection = new RTCPeerConnection();

    // Create sdp offer
    peerConnection.createOffer().then(offer => {
        peerConnection.setLocalDescription(offer);
        socket.send(JSON.stringify({ type: "offer", sdp: offer.sdp }));
    })
}