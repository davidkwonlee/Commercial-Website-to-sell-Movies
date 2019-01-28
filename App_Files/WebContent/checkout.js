/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */

function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
    	jQuery("#checkout_error_message").text(resultDataJson["message"]);
    	window.location.href = "confirmation.html";
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#checkout_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit checkout form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        jQuery("#checkoutForm").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#checkoutForm").submit((event) => submitLoginForm(event));

