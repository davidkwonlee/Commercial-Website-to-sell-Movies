
/**
 * Handle the data returned by SearchServlet
 * @param resultDataString jsonObject
 */

function handleSearchResult(resultData) {
	console.log(resultData)
	for(let i = 0; i < resultData.length; ++i){
		let HTML = '<table class="table table-striped"><caption>'+ resultData[i]["table"] +'</caption>';
		HTML += '<thead><tr><th>Column</th><th>Type</th></tr></thead><tbody>';
		for(let j = 0; j< resultData[i]["columns"].length; ++j){
			HTML += '<tr>';
			HTML += '<th>' + resultData[i]["columns"][j]["column"] + '</th>';
			HTML += '<th>' + resultData[i]["columns"][j]["type"] + '</th>';
			HTML += '</tr>';
		}
		HTML += '</tbody></table>';
		jQuery("#metadata").append(HTML);
	}
}

function handleStarResult(resultData){
	console.log(resultData)
	jQuery("#star_error_message").text(resultData["message"]);
}
function handleMovieResult(resultData){
	console.log(resultData)
	if(resultData["message"] === 'na'){
		let message = 'movie already exists';
		jQuery("#movie_error_message").text(message);
	}else{
		let message = 'Movie Added';
		jQuery("#movie_error_message").text(message);
	}
	
}
// Bind the submit action of the form to a handler function
$( document ).ready(function() {
	
	
	//if came from search then retrieve search data
	    jQuery.ajax({
	        dataType: "json", // Setting return data type
	        method: "GET", // Setting request method
	        data: window.location.search,
	        url: "api/dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
	        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
	    });
	    
	    function submitStarForm(formSubmitEvent) {

	        // Important: disable the default action of submitting the form
	        //   which will cause the page to refresh
	        //   see jQuery reference for details: https://api.jquery.com/submit/
	        formSubmitEvent.preventDefault();

	        jQuery.post(
	            "api/dashboardstar",
	            // Serialize the login form to the data sent by POST request
	            jQuery("#star_form").serialize(),
	            (resultDataString) => handleStarResult(resultDataString));

	    }
	    function submitMovieForm(formSubmitEvent) {

	        // Important: disable the default action of submitting the form
	        //   which will cause the page to refresh
	        //   see jQuery reference for details: https://api.jquery.com/submit/
	        formSubmitEvent.preventDefault();

	        jQuery.post(
	            "api/dashboardmovie",
	            // Serialize the login form to the data sent by POST request
	            jQuery("#movie_form").serialize(),
	            (resultDataString) => handleMovieResult(resultDataString));

	    }
	    // Bind the submit action of the form to a handler function
	    jQuery("#star_form").submit((event) => submitStarForm(event));
	    jQuery("#movie_form").submit((event) => submitMovieForm(event));
	
	
});

