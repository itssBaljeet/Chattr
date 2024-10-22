document.querySelector('#registerForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form from submitting in the default way

    // Collect the form data
    let formData = {
        username: document.querySelector('#username').value,
        password: document.querySelector('#password').value,
        email: document.querySelector('#email').value
    };

    // Send POST request to the backend
    fetch('/api/users/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
        .then(response => {
            if (response.ok) {
                console.log("Registered successfully");
                alert("Registration successful!");
            } else {
                // Get the response body as text
                response.text().then(text => {
                    console.log("Registration failed: " + text);
                    alert("Registration failed: " + text);
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("An error occurred while registering.");
        });
});
