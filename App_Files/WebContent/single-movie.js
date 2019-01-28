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
	let currStars = resultData[0]["stars"].split(", ")
	let currStarIds = resultData[0]['starIds'].split(" ")
    
    //add data to front end
        // Concatenate the html tags with resultData jsonObject
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["movieId"] + "</th>";
    rowHTML +=
        "<th>" +
        // Add a link to single-star.html with id passed with GET url parameter  
         resultData[0]["movieTitle"] +     // display star_name for the link text
        "</th>";
    rowHTML += "<th>" + resultData[0]["movieYear"] + "</th>";
    rowHTML += "<th>" + resultData[0]["movieDirector"] + "</th>";
    rowHTML += "<th>" + resultData[0]["genres"] + "</th>";
    rowHTML += "<th>";
    for(let j = 0; j < currStars.length; j++){
    	if(j+1 == currStars.length){
    		rowHTML += '<a href="single-star.html?id=' + currStarIds[j] + '">' + currStars[j] + '</a>';
    	}else{
    		rowHTML += '<a href="single-star.html?id=' + currStarIds[j] + '">' + currStars[j] + ', </a>';
    	}
    }
    rowHTML += "</th>";
    rowHTML += "<th>" + resultData[0]["rating"] + "</th>";
    rowHTML += "<th>"
//    rowHTML += '<div class="container"> <input type="text" name="qty" class="qty" maxlength="12" value="0" class="input-text qty" /> <div class="button-container"> <button class="cart-qty-plus" type="button" value="+">+</button> <button class="cart-qty-minus" type="button" value="-">-</button> </div>'
    rowHTML += '<button type="button" id='+ resultData[0]["movieId"] +' class="add-cart btn btn-primary">Add to Cart</button>';
    rowHTML += "</th>"
    rowHTML += "</tr>";
    
    
    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
    bindQuantity();
    bindAddToCart();
    
}
function bindQuantity(){
	var incrementPlus;
	var incrementMinus;

	var buttonPlus  = $(".cart-qty-plus");
	var buttonMinus = $(".cart-qty-minus");

	var incrementPlus = buttonPlus.click(function() {
		console.log("listener working");
		var $n = $(this)
			.parent(".button-container")
			.parent(".container")
			.find(".qty");
		$n.val(Number($n.val())+1 );
	});

	var incrementMinus = buttonMinus.click(function() {
			var $n = $(this)
			.parent(".button-container")
			.parent(".container")
			.find(".qty");
		var amount = Number($n.val());
		if (amount > 0) {
			$n.val(amount-1);
		}
	});
}

function bindAddToCart(){
//	var addQuantity = $(".add-cart").click(function() {
//		var quanity = $(this)
//			.parent(".container")
//			.find(".qty");
//		var id = $(this).attr("id");
//		console.log(id);
//		jQuery.ajax({
//		    dataType: "json",  // Setting return data type
//		    method: "GET",// Setting request method
//		    url: "api/session-cart?movieId=" + id+"&qty="+quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
//		    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
//		});
//	});
	var addQuantity = $(".add-cart").click(function() {
		var id = $(this).attr("id");
		console.log(id);
		window.location.href = "cart.html?addItem=true&movieId="+id;
	});
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
$( document ).ready(function() {
// Get id from URL
	let movieId = getParameterByName('id');
	
	// Makes the HTTP GET request and registers on success callback function handleResult
	jQuery.ajax({
	    dataType: "json",  // Setting return data type
	    method: "GET",// Setting request method
	    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
	    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
	});

});