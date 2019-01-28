
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.sql.DriverManager;

// server endpoint URL
@WebServlet("/hero-suggestion")
public class HeroSuggestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static HashMap<String, String> movies_Map = new HashMap<>();
	
	private static java.sql.Connection dbcon;
	private static final String loginUser = "root";
	private static final String loginPassword = "123";
	private static final String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";
	
    
    public HeroSuggestion() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {			
			movies_Map.clear();
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
			
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			// get the query string from parameter
			String char_from_search = request.getParameter("query");
			
			String query = "select id, title from movies where match(title) against(? in boolean mode) limit 10;";
			java.sql.PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
			preparedStatement.setString(1,char_from_search + "*" );  
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String movieId = resultSet.getString("id");
				String movieTitle = resultSet.getString("title");
				movies_Map.put(movieId , movieTitle);
			}
			
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	

			for(String movies_returned: movies_Map.keySet()) {
				String movie_name = movies_Map.get(movies_returned);
				if (movie_name.toLowerCase().contains(char_from_search.toLowerCase())) {
					jsonArray.add(generateJsonObject(movies_returned, movie_name));
				}
				
			}
	        resultSet.close();
	        preparedStatement.close();
	        dbcon.close();
			response.getWriter().write(jsonArray.toString());
			return;
			
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}

	private static JsonObject generateJsonObject(String movie_ID, String movie_title) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movie_title);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		String categoryName = "movies";
		additionalDataJsonObject.addProperty("movie", categoryName);
		additionalDataJsonObject.addProperty("movieID", movie_ID);
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}


}
