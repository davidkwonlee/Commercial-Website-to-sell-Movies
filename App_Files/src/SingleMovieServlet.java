import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();

		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			Connection dbcon1 = dataSource.getConnection();
			// Construct a query with parameter represented by "?"
			String query = "select cts.rating,cts.id,cts.title,cts.year,cts.director,genres.name from (select genres_in_movies.genreId,ratings.rating,movies.id,movies.title,movies.year,movies.director from movies inner join genres_in_movies on movies.id = genres_in_movies.movieId left join ratings on ratings.movieId = movies.id) as cts inner join genres on cts.genreId = genres.id where cts.id = ?";
			String query1 = "select cts.starId,cts.id,cts.title,cts.year,cts.director,stars.name from (select * from movies inner join stars_in_movies on movies.id = stars_in_movies.movieId) as cts inner join stars on cts.starId = stars.id where cts.id = ?";
			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);
			PreparedStatement statement1 = dbcon.prepareStatement(query1);
			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);
			statement1.setString(1, id);
			// Perform the query
			ResultSet resultSet = statement.executeQuery();
			ResultSet resultSet1 = statement1.executeQuery();
			
			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
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
	            jsonObject.addProperty("quantity", 0);         

	            
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
			
            // write JSON string to output
            out.write(jsonArray.toString());
            
            // set response status to 200 (OK)
            response.setStatus(200);

			resultSet.close();
			statement.close();
			dbcon.close();
			resultSet1.close();
			statement1.close();
			dbcon1.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());
			System.out.println(e.getMessage());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();
	}
	

}
