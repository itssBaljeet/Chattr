document.addEventListener('DOMContentLoaded', function () {
    // Connect to WebSocket and subscribe once the document is ready
    let socket = new SockJS('/ws'); // Update with your WebSocket endpoint if different
    let stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // Subscribe to the user's private notification queue
        stompClient.subscribe('/user/queue/callNotifications', function (message) {
            const callRequest = JSON.parse(message.body);
            displayIncomingCallNotification(callRequest);
        });
    });

    function displayIncomingCallNotification(callRequest) {
        // Show the notification (integrate with your Thymeleaf fragment)
        const notification = document.getElementById('incoming-call-notification');
        document.getElementById('caller-username').textContent = callRequest.callerUsername;
        notification.style.display = 'block';
    }
});
