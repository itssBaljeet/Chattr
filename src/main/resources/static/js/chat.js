let stompClient = null;
let currentUsername = null;
fetchUsername().then(username => {
    currentUsername = username;
    console.log("Current username");
}).catch(error => {
    console.log("Error fetching username: ", error);
    throw error;
})

async function fetchUsername() {
    try {
        const response = fetch("/api/users/current-username");
        return (await response).text();
    } catch (error) {
        console.log("Error fetching username: ", error);
        throw error;
    }
}

function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: currentUsername, type: 'JOIN'})
    );
}

function onError(error) {
    // Handle the error
}

function sendMessage(event) {
    let messageContent = document.querySelector('#message').value.trim();

    console.log("Sending data")

    if(messageContent && stompClient) {
        let chatMessage = {
            sender: currentUsername,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        document.querySelector('#message').value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    // Create a new list item (li) element
    let messageElement = document.createElement('li');

    // Set the content of the message
    if (message.type === 'JOIN') {
        messageElement.textContent = message.sender + ' joined!';
    } else if (message.type === 'CHAT') {
        let text = message.sender + ': ' + message.content;
        messageElement.textContent = text;
    }

    // Append the new message to the messageArea (ul)
    document.querySelector('#messageArea').appendChild(messageElement);
}

// Event listeners
document.querySelector('#messageForm').addEventListener('submit', sendMessage, true);
