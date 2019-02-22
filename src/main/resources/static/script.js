function submitPassword() {

    var username = document.getElementById('username').value;
    var password1 = document.getElementById('password1').value;
    var password2 = document.getElementById('password2').value;

    if (password1 !== password2) {
        alert('Passwords do not match!');
        return;
    }

    console.log(username + ":" + password1 + ":" + password2);

    var xhr = new XMLHttpRequest();
    console.log(username + ":" + password1 + ":" + password2);
// Setup our listener to process completed requests
    xhr.onload = function () {

        // Process our return data
        if (xhr.status >= 200 && xhr.status < 300) {
            // What do when the request is successful
            console.log('success!', xhr);

            var jsonResponse = JSON.parse(xhr.responseText);

            console.log(jsonResponse);

            var status = jsonResponse['status'];
            alert(status + "! " + jsonResponse['message']);
            if(status === 'Success') {
                console.log('Redirecting');
                window.location.href = "http://localhost:8100";
            }
            console.log('Did not redirect');

        } else {
            // What do when the request fails
            alert('There was a problem with the server');
            console.log('The request failed!');
        }

        // Code that should run regardless of the request status
        console.log('This always runs...');
    };
    console.log(username + ":" + password1 + ":" + password2);

    // var host = 'http://localhost:8080';
    var host = 'https://shrouded-caverns-9.' +
        '2003.herokuapp.com';
    var url = host + '/api/users/password/forgot/reset?username='+username+'&password='+password1;
    console.log(url);
    xhr.open('GET', url);
    xhr.send();

}