/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    let movieTableBodyElement = jQuery("#movie_table_body");
    console.log(resultData);
    //add data to front end
        // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    console.log(resultData);
	for (let i = 0; i < resultData.length; i++) {
	    rowHTML += "<tr>";
	    rowHTML +=
	        "<th>" +
	        // Add a link to single-star.html with id passed with GET url parameter
	        resultData[i]['saleId']+
	        "</th>";
	    rowHTML += "<th>" + resultData[i]['movieTitle'] +  "</th>";
	    rowHTML += "<th>" + resultData[i]['quantity'] + "</th>";
	    rowHTML += "</tr>";
	}
    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
}




/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
$( document ).ready(function() {
// Get id from URL

		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/confirmation", // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
});


