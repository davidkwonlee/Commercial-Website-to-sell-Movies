import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

//
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("star_name");
        String sortType = request.getParameter("sort");
        starName = starName == null ? "" : starName;
        
        starName = "%" + starName + "%";
        director = "%" + director + "%";

        //Tokenize the search box query
		String[] splitText = title.split(" ");
		String prefix_query = "";
		String prefix_query_1 = "";
		
		if (splitText.length == 0) {
			System.out.println("no search words entered");
		}
		else {
			prefix_query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from (select distinct * from movies inner join (select stars_in_movies.movieId from stars inner join stars_in_movies on stars.id = stars_in_movies.starId where stars.name like ?) as movieTable on movieTable.movieId = movies.id where match (movies.title) against (";
			prefix_query_1 = "Select cts.id,title,year,director,cts.rating,cts.starId,stars.name from (Select id,title,year,director,bts.rating,starId from (Select id,title,year,director,rating from (select distinct * from movies inner join (select stars_in_movies.movieId from stars inner join stars_in_movies on stars.id = stars_in_movies.starId where stars.name like ?) as movieTable on movieTable.movieId = movies.id where match (movies.title) against (";

				for (int i = 0; i<splitText.length; i++){
				   if(i == splitText.length-1) {
					   prefix_query += "? in boolean mode)";
					   prefix_query_1 += "? in boolean mode)";
				   }
				   else {
					   prefix_query += "? ";
					   prefix_query_1 += "? ";
				   }
			   }
		}
        //End of tokenization
        
        try {
        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        Connection dbcon1 = dataSource.getConnection();

//        String query = "select distinct * from movies inner join (select stars_in_movies.movieId from stars inner join stars_in_movies on stars.id = stars_in_movies.starId where stars.name like \"%"+ starName +"%\") as movieTable on movieTable.movieId = movies.id where movies.title like \"%"+ title +"%\" and movies.year like \"%"+ year +"%\" and movies.director like \"%"+ director +"%\""; 
        String query = null;
        String query1 = null;
        year = year.equals("") ? null : year;
        if(sortType==null) {
        	if(year==null) {
            	query = prefix_query + " and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId";
        	}else {
            	query = prefix_query + " and movies.year =? and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId";
        	}
        }else if(sortType.equals("titleasc")) {
        	if(year==null) {
            	query = prefix_query + " and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by title asc";        	
        	}else {
            	query = prefix_query + " and movies.year =? and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by title asc";        	
        	}
        }else if(sortType.equals("titledesc")) {
        	if(year==null) {
            	query = prefix_query + " and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by title desc";        	
        	}else {
            	query = prefix_query + " and movies.year =? and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by title desc";        	
        	}
        }else if(sortType.equals("ratingdesc")) {
        	if(year==null) {
            	query = prefix_query + " and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by cts.rating desc";        	
        	}else {
            	query = prefix_query + " and movies.year =? and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by cts.rating desc";        	
        	}
        }else if(sortType.equals("ratingasc")) {
        	if(year==null) {
            	query = prefix_query + " and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by cts.rating asc";        	
        	}else {
            	query = prefix_query + " and movies.year =? and movies.director like ?) as search_movie left join ratings on ratings.movieId = search_movie.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId order by cts.rating asc";        	
        	}
        }
        if(year==null) {
            query1 = prefix_query_1 + " and movies.director like ?) as search_movies left join ratings on ratings.movieId = search_movies.id) as bts inner join stars_in_movies on bts.id = stars_in_movies.movieId) as cts inner join stars on stars.id = cts.starId";
        }else {
            query1 = prefix_query_1 + " and movies.year =? and movies.director like ?) as search_movies left join ratings on ratings.movieId = search_movies.id) as bts inner join stars_in_movies on bts.id = stars_in_movies.movieId) as cts inner join stars on stars.id = cts.starId";
        }
  
        //Declare 1st Prepared Statement set parameters
        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
        
    	if(year==null) {
            preparedStatement.setString(1, starName);
            for (int i = 2; i<= splitText.length + 2; i++) {
            	if (i == splitText.length + 2) {
                    preparedStatement.setString(i, director);	
            	}else {
            		if (splitText[i-2].toLowerCase().equals("the") || splitText[i-2].toLowerCase().equals("and") || splitText[i-2].toLowerCase().equals("of") || splitText[i-2].toLowerCase().equals("if") || splitText[i-2].toLowerCase().equals("1") || splitText[i-2].toLowerCase().equals("2") || splitText[i-2].toLowerCase().equals("3") || splitText[i-2].toLowerCase().equals("4") || splitText[i-2].toLowerCase().equals("5") || splitText[i-2].toLowerCase().equals("6") || splitText[i-2].toLowerCase().equals("7") || splitText[i-2].toLowerCase().equals("8") || splitText[i-2].toLowerCase().equals("9")) {
            			preparedStatement.setString(i, splitText[i-2] + "*");
            		}else {
            			preparedStatement.setString(i,"+" + splitText[i-2] +"*");
            		}
            	}
            }
    	}
    	
    	else {
            preparedStatement.setString(1, starName);	
            for (int i = 2; i<= splitText.length + 2; i++) {
            	if (i == splitText.length + 2) {
                    preparedStatement.setString(i, year);	
                    preparedStatement.setString(i + 1, director);	
            	}else {
            		if (splitText[i-2].toLowerCase().equals("the") || splitText[i-2].toLowerCase().equals("and") || splitText[i-2].toLowerCase().equals("of") || splitText[i-2].toLowerCase().equals("if") || splitText[i-2].toLowerCase().equals("1") || splitText[i-2].toLowerCase().equals("2") || splitText[i-2].toLowerCase().equals("3") || splitText[i-2].toLowerCase().equals("4") || splitText[i-2].toLowerCase().equals("5") || splitText[i-2].toLowerCase().equals("6") || splitText[i-2].toLowerCase().equals("7") || splitText[i-2].toLowerCase().equals("8") || splitText[i-2].toLowerCase().equals("9")) {
            			preparedStatement.setString(i, splitText[i-2] + "*");
            		}else {
            			preparedStatement.setString(i,"+" + splitText[i-2] +"*");
            		}
            	}
            }
    	}
    	
        //Declare 2nd Prepared Statement d set parameters
        PreparedStatement preparedStatement1 = dbcon.prepareStatement(query1); 
    	if(year==null) {
            preparedStatement1.setString(1, starName);
            for (int i = 2; i<= splitText.length + 2; i++) {
            	if (i == splitText.length + 2) {
                    preparedStatement1.setString(i, director);	
            	}else {
            		if (splitText[i-2].toLowerCase().equals("the") || splitText[i-2].toLowerCase().equals("and") || splitText[i-2].toLowerCase().equals("of") || splitText[i-2].toLowerCase().equals("if") || splitText[i-2].toLowerCase().equals("1") || splitText[i-2].toLowerCase().equals("2") || splitText[i-2].toLowerCase().equals("3") || splitText[i-2].toLowerCase().equals("4") || splitText[i-2].toLowerCase().equals("5") || splitText[i-2].toLowerCase().equals("6") || splitText[i-2].toLowerCase().equals("7") || splitText[i-2].toLowerCase().equals("8") || splitText[i-2].toLowerCase().equals("9")) {
            			preparedStatement1.setString(i, splitText[i-2] + "*");
            		}else {
            			preparedStatement1.setString(i,"+" + splitText[i-2] +"*");
            		}
            	}
            }
    	}
    	else {
            preparedStatement1.setString(1, starName);	
            for (int i = 2; i<= splitText.length + 2; i++) {
            	
            	if (i == splitText.length + 2) {
                    preparedStatement1.setString(i, year);	
                    preparedStatement1.setString(i + 1, director);	
            	}else {
            		if (splitText[i-2].toLowerCase().equals("the") || splitText[i-2].toLowerCase().equals("and") || splitText[i-2].toLowerCase().equals("of") || splitText[i-2].toLowerCase().equals("if") || splitText[i-2].toLowerCase().equals("1") || splitText[i-2].toLowerCase().equals("2") || splitText[i-2].toLowerCase().equals("3") || splitText[i-2].toLowerCase().equals("4") || splitText[i-2].toLowerCase().equals("5") || splitText[i-2].toLowerCase().equals("6") || splitText[i-2].toLowerCase().equals("7") || splitText[i-2].toLowerCase().equals("8") || splitText[i-2].toLowerCase().equals("9")) {
            			preparedStatement1.setString(i, splitText[i-2] + "*");
            		}else {
            			preparedStatement1.setString(i,"+" + splitText[i-2] +"*");
            		}
            	}
            }
    	}
    	System.out.println(preparedStatement);
    	System.out.println(preparedStatement1);
    	
    	System.out.println("HERE");
        //Perform the query
        ResultSet resultSet = preparedStatement.executeQuery();
        
        System.out.println("past");
        ResultSet resultSet1 = preparedStatement1.executeQuery();
		
        System.out.println("SUCCESS");

        JsonArray jsonArray = new JsonArray();
		while (resultSet.next()) {
			// get a star from result set
			String genres = "";
			String movieId = resultSet.getString("id");
			String movieTitle = resultSet.getString("title");
			String movieYear = resultSet.getString("year");
			String movieDirector = resultSet.getString("director");
			String rating = resultSet.getString("rating");
			String genre = resultSet.getString("name");
			genres = genres + genre;
			//get rest genres
			int nextRow = resultSet.getRow();
			while(true) {
				Boolean next = resultSet.next();
				if(next) {
					String nextMovieId = resultSet.getString("id");
					if(movieId.equals(nextMovieId)) {
						genres = genres + ", " + resultSet.getString("name");
					}else {
						nextRow = resultSet.getRow() - 1;
						break;
					}
				}else {
					nextRow = resultSet.getRow() - 1;
					break;
				}
			}
			resultSet.absolute(nextRow);
            // Create a JsonObject based on the data we retrieve from rs
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("movieId", movieId);
            jsonObject.addProperty("movieTitle", movieTitle);
            jsonObject.addProperty("movieYear", movieYear);
            jsonObject.addProperty("movieDirector", movieDirector);
            jsonObject.addProperty("rating", rating);
            jsonObject.addProperty("genres", genres);            

            
			//get rest stars
			String stars = "";
			String starIds = "";
			Boolean first = true;
			while(resultSet1.next()) {

				String starMovieId = resultSet1.getString("id");
				if(starMovieId.equals(movieId)) {
					if(first) {
						stars = resultSet1.getString("name");
						starIds = resultSet1.getString("StarId");
						first = false;
					}
					else {
						stars =  stars + ", " + resultSet1.getString("name");
						starIds = starIds + " " + resultSet1.getString("StarId");
					}
				}
			}
			resultSet1.absolute(0);
			jsonObject.addProperty("stars", stars);
			jsonObject.addProperty("starIds", starIds);
			jsonArray.add(jsonObject);
			

			}
        out.write(jsonArray.toString());
        // set response status to 200 (OK)
        response.setStatus(200);
        resultSet.close();
        preparedStatement.close();
        dbcon.close();
        resultSet1.close();
        preparedStatement1.close();
        dbcon1.close();
        
        }catch (Exception e) {
        	System.out.println(e);
        	
        	System.out.println(e.getMessage());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
    }
}
