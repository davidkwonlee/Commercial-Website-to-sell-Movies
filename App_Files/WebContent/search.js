///**
// * Handle the data returned by SearchServlet
// * @param resultDataString jsonObject
// */
//
//function handleSearchResult(resultDataString) {
//    resultDataJson = JSON.parse(resultDataString);
//
//    console.log("handle search response");
//    console.log(resultDataJson);
//    console.log(resultDataJson["status"]);
//
//    // If search success, redirect to index.html page
//    if (resultDataJson["status"] === "success") {
//        window.location.replace("index.html");
//    }
//    // If search fail, display error message on <div> with id "search_error_message"
//    else {
//
//        console.log("show error message");
//        console.log(resultDataJson["message"]);
//        jQuery("#search_error_message").text(resultDataJson["message"]);
//    }
//}
//
///**
// * Submit the form content with POST method
// * @param formSubmitEvent
// */
//function submitSearchForm(formSubmitEvent) {
//    console.log("submit search form");
//
//    // Important: disable the default action of submitting the form
//    //   which will cause the page to refresh
//    //   see jQuery reference for details: https://api.jquery.com/submit/
//    formSubmitEvent.preventDefault();
//
//    jQuery.post(
//        "api/search",
//        // Serialize the search form to the data sent by POST request
//        jQuery("#search_form").serialize(),
//        (resultDataString) => handleSearchResult(resultDataString));
//
//}
//
//// Bind the submit action of the form to a handler function
//jQuery("#search_form").submit((event) => submitSearchForm(event));

