function submitPassword() {

    var password1 = document.getElementById('password1').value;
    var password2 = document.getElementById('password2').value;

    console.log(password1 + ":" + password2);

    var xhr = new XMLHttpRequest();

// Setup our listener to process completed requests
    xhr.onload = function () {

        // Process our return data
        if (xhr.status >= 200 && xhr.status < 300) {
            // What do when the request is successful
            console.log('success!', xhr);
        } else {
            // What do when the request fails
            console.log('The request failed!');
        }

        // Code that should run regardless of the request status
        console.log('This always runs...');
    };

// Create and send a GET request
// The first argument is the post type (GET, POST, PUT, DELETE, etc.)
// The second argument is the endpoint URL
    xhr.open('GET', 'localhost:8080/');
    xhr.send();
}