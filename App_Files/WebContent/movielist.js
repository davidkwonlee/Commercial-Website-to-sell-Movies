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

function updateQueryStringParameter(uri, key, value) {
	  var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
	  var separator = uri.indexOf('?') !== -1 ? "&" : "?";
	  if (uri.match(re)) {
	    return uri.replace(re, '$1' + key + "=" + value + '$2');
	  }
	  else {
	    return uri + separator + key + "=" + value;
	  }
	}

/**
 * Handle the data returned by SearchServlet
 * @param resultDataString jsonObject
 */

function handleSearchResult(resultData) {
    let movieTableBodyElement = jQuery("#movie_table_body");
    
    //get appropriate parameters for display
    let itemsToShow = 10;
    let page = 1;
    if(getParameterByName("items_p_page") != null && getParameterByName("items_p_page") != ""){
    	itemsToShow = Number(getParameterByName("items_p_page"));
    }
    if(getParameterByName("page") != null && getParameterByName("page") != ""){
    	page = Number(getParameterByName("page"));
    }
    let num = (page-1)*itemsToShow
    
    //add data to front end
    for (let i = num; i < Math.min(itemsToShow*page,resultData.length); i++) {
    	console.log(resultData[i]["stars"]);
    	let currStars = resultData[i]["stars"].split(", ");
    	let currStarIds = resultData[i]['starIds'].split(" ");
    	let currentGenres = resultData[i]["genres"].split(", ");
    	let genre_to_id = new Map([["Action",1],["Adult",2],["Adventure",3],["Animation",4],["Biography",5],["Comedy",6],["Crime",7],["Documentary",8],["Drama",9],["Family",10],["Fantasy",11],["History",12],["Horror",13],["Music",14],["Musical",15],["Mystery",16],["Reality-TV",17],["Romance",18],["Sci-Fi",19],["Sport",20],["Thriller",21],["War",22],["Western",23]]);
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movieId"] + "</th>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">'
            + resultData[i]["movieTitle"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movieYear"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieDirector"] + "</th>";
//        rowHTML += "<th>" + resultData[i]["genres"] + "</th>";
        rowHTML += "<th>";
        for(let j = 0; j < currentGenres.length; j++){
        	if(j+1 == currentGenres.length){
        		rowHTML += '<a href="movielist.html?browse_by_genre=true&genre_id=' + genre_to_id.get(currentGenres[j]) + '">' + currentGenres[j] + '</a>';
        	}else{
        		rowHTML += '<a href="movielist.html?browse_by_genre=true&genre_id=' + genre_to_id.get(currentGenres[j]) + '">' + currentGenres[j] + ', </a>';
        	}
        }
        rowHTML += "</th>";        
        rowHTML += "<th>";
        for(let j = 0; j < currStars.length; j++){
        	if(j+1 == currStars.length){
        		rowHTML += '<a href="single-star.html?id=' + currStarIds[j] + '">' + currStars[j] + '</a>';
        	}else{
        		rowHTML += '<a href="single-star.html?id=' + currStarIds[j] + '">' + currStars[j] + ', </a>';
        	}
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += '<th><button type="button" id='+ resultData[i]["movieId"] +' class="add-cart btn btn-primary">Add to Cart</button></th>';
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        bindAddToCart();
    }    

}
function bindAddToCart(){
	var addQuantity = $(".add-cart").click(function() {
		var id = $(this).attr("id");
		console.log(id);
		window.location.href = "cart.html?addItem=true&movieId="+id;
	});
}

// Bind the submit action of the form to a handler function
$( document ).ready(function() {
	
	let urlParams = new URLSearchParams(window.location.search);
	//build hrefs
	$("#titleasc").attr("href", updateQueryStringParameter(window.location.href, "sort", "titleasc"));
	$("#titledesc").attr("href", updateQueryStringParameter(window.location.href, "sort", "titledesc"));
	$("#ratingasc").attr("href", updateQueryStringParameter(window.location.href, "sort", "ratingasc"));
	$("#ratingdesc").attr("href", updateQueryStringParameter(window.location.href, "sort", "ratingdesc"));
	
	if(getParameterByName("page") != null && getParameterByName("page") != ""){
		let pageNum = Number(getParameterByName("page"));
		if(pageNum === 1){
			$('#previd').hide();
			$("#next").attr("href", updateQueryStringParameter(window.location.href, "page", 2));
		}else{
			$("#prev").attr("href", updateQueryStringParameter(window.location.href, "page", pageNum-1));
			$("#next").attr("href", updateQueryStringParameter(window.location.href, "page", pageNum+1));
		}
	}else{
		$('#previd').hide();
		$("#next").attr("href", updateQueryStringParameter(window.location.href, "page", 2));
	}
	
	//set active dropdown and listener
	if(getParameterByName("items_p_page") != null && getParameterByName("items_p_page") != ""){
		$('#drop').val(getParameterByName("items_p_page"));
	}
	$('#drop').change(
		    function() {
		        var val = $('#drop option:selected').val();
		        console.log(val);
		        console.log(updateQueryStringParameter(window.location.href, "items_p_page", val));
		        window.location.href = updateQueryStringParameter(window.location.href, "items_p_page", val);
		    }
		);
	
	//if came from search then retrieve search data
	if(urlParams.has('search')){
	    jQuery.ajax({
	        dataType: "json", // Setting return data type
	        method: "POST", // Setting request method
	        data: window.location.search,
	        url: "api/search", // Setting request url, which is mapped by StarsServlet in Stars.java
	        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
	    });
	}
	
	//check if urlparam has search by genre if it does then make ajax request to genre servlet to get movie data
	if(urlParams.has('browse_by_genre')){
	    jQuery.ajax({
	    	
	        dataType: "json", // Setting return data type
	        method: "POST", // Setting request method
	        data: window.location.search,
	        url: "api/genre", // Setting request url, which is mapped by GenreServlet in Stars.java
	        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
	    });
	}
	
	//check if urlparam has search by title if it does then make ajax request to genre servlet to get movie data
	if(urlParams.has('browse_by_title')){
	    jQuery.ajax({
	        dataType: "json", // Setting return data type
	        method: "POST", // Setting request method
	        data: window.location.search,
	        url: "api/title", // Setting request url, which is mapped by titlesServlet in Stars.java
	        success: (resultData) => handleSearchResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
	    });
	}
});

