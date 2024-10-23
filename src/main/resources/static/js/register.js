document.querySelector('#registerForm').addEventListener('submit', function(event) {
    // Collect the form data if you still want to validate or process it before submission
    let username = document.querySelector('#username').value;
    let password = document.querySelector('#password').value;
    let email = document.querySelector('#email').value;

    // Simple client-side validation
    if (!username || !password || !email) {
        alert("Please fill in all fields.");
        event.preventDefault(); // Prevent submission if validation fails
    }
});
