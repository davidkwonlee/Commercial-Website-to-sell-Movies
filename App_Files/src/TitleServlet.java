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
@WebServlet(name = "TitleServlet", urlPatterns = "/api/title")
public class TitleServlet extends HttpServlet {
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
        
        String genreId = request.getParameter("genre_id");
        
        String titleId = request.getParameter("title_id");
        titleId += "%";

        try {
        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        Connection dbcon1 = dataSource.getConnection();
        String query = null;
        
        if(sortType==null) {
        	query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId WHERE title LIKE ?";
        }else if(sortType.equals("titleasc")) {
        	query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId WHERE title LIKE ? order by title asc";        	
        }else if(sortType.equals("titledesc")) {
        	query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId WHERE title LIKE ? order by title desc";        	
        }else if(sortType.equals("ratingdesc")) {
        	query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId WHERE title LIKE ? order by cts.rating desc";        	
        }else if(sortType.equals("ratingasc")) {
        	query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId WHERE title LIKE ? order by cts.rating asc";        	
        }
        
//        String query1 = "select test.id,test.title,test.year,test.director,test.rating,stars_in_movies.starId,stars.name from (Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId where title LIKE \""+ titleId +"%\") as test inner join stars_in_movies on test.id = stars_in_movies.movieId inner join stars on stars_in_movies.starId = stars.id"; 
        String query1 = "select stars.id as StarId,movies.id,stars.name from movies inner join stars_in_movies on movies.id = stars_in_movies.movieId inner join stars on stars_in_movies.starId=stars.id where title like ?";
        //Perform the query
        
        //Declare 1st Prepared Statement for titleID and set parameters
        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
        preparedStatement.setString(1,titleId);
        
        //Declare 2nd Prepared Statement for titleID and set parameters
        PreparedStatement preparedStatement1 = dbcon.prepareStatement(query1); 
        preparedStatement1.setString(1, titleId);
        
        //Perform the query
        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSet resultSet1 = preparedStatement1.executeQuery();

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
			while(resultSet.next()) {
				String nextMovieId = resultSet.getString("id");
				if(movieId.equals(nextMovieId)) {
					genres = genres + ", " + resultSet.getString("name");
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
//			System.out.println(stars);
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
        	System.out.println(e.getMessage());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
    }
}
