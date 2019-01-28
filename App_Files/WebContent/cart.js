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
    //add data to front end
        // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    console.log(resultData);
	for (let i = 0; i < resultData.length; i++) {
	    rowHTML += "<tr>";
	    rowHTML +=
	        "<th>" +
	        // Add a link to single-star.html with id passed with GET url parameter
	        '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">'
	        + resultData[i]["movieTitle"] + '(' + resultData[i]["movieYear"] + ')'+    // display star_name for the link text
	        '</a>' +
	        "</th>";
	    rowHTML += "<th>" + '<input id='+ resultData[i]['movieId'] +' type="text" name="qty" value="'+ resultData[i]['quantity'] +'" onkeypress="return isNumberKey(event)">'+  "</th>";
	    rowHTML += "<th>" + '<button id='+ resultData[i]['movieId'] +' class="add-cart">Update</button>' + "</th>";
	    rowHTML += "<th>" + '<button id='+ resultData[i]['movieId'] +' class="remove-cart">Remove</button>' + "</th>";
	    rowHTML += "</tr>";
	}
    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
//    bindQuantity();
    bindAddToCart();
    bindRemoveItem();
}

function bindRemoveItem(){
	var addQuantity = $(".remove-cart").click(function() {
		let id = $(this).attr("id");
		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/cart-session?movieId=" + id+"&remove=true", // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: (resultData) => refresh() // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
	});
}

function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : evt.keyCode;
   if (charCode != 46 && charCode > 31 
     && (charCode < 48 || charCode > 57))
      return false;

   return true;
}

function refresh(){
	window.location.href = "cart.html";
}

function bindAddToCart(){
	var addQuantity = $(".add-cart").click(function() {
		let id = $(this).attr("id");
		let quantity = $( "input[id]" ).val();
		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/cart-session?movieId=" + id+"&qty="+quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: () => refresh() // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
	});
}

function bindEmptyCart(){
	var guess = $("#emptycart").click(function() {
		console.log('wtf');
		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/cart-session?emptyCart=true", // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: () => refresh() // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
	});
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
$( document ).ready(function() {
// Get id from URL
	bindEmptyCart();
	$("#checkout").click(function(){
		window.location.href = "checkout.html";
	});
	let movieId = getParameterByName('movieId');
	let addItem = getParameterByName('addItem');
	// Makes the HTTP GET request and registers on success callback function handleResult

	if(addItem === 'true'){
		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
	}else{
		jQuery.ajax({
		    dataType: "json",  // Setting return data type
		    method: "GET",// Setting request method
		    url: "api/cart-session", // Setting request url, which is mapped by StarsServlet in Stars.java
		    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
		});
	}
});


