import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Date.*;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviemasterdb")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession(); // Get a instance of current session on the request
        String userId = (String)session.getAttribute("userId");
        HashMap<String,Integer> previousItems = (HashMap<String,Integer>) session.getAttribute("previousItems");
		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		System.out.println(userId);
		System.out.println(previousItems);
		// Output stream to STDOUT

		try {
			Connection dbcon = dataSource.getConnection();
		     PrintWriter out = response.getWriter();

		        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we lock the ArrayList while updating
		        
		        synchronized (previousItems) {
		        	JsonArray jsonArray = new JsonArray();
		            	
		                if (previousItems.size() != 0) {

			            	for (String key : previousItems.keySet()) {
			            		System.out.println(key);
			            		JsonObject jsonObject = new JsonObject();
			            		jsonObject.addProperty("movieId", key);
			                	String query = "Select * from movies Where id=?";
			            		PreparedStatement statement = dbcon.prepareStatement(query);
			            		statement.setString(1, key);
			            		ResultSet resultSet = statement.executeQuery();
			            		resultSet.next();
			            		String name = resultSet.getString("title");
			            		String year = resultSet.getString("year");
			            		jsonObject.addProperty("movieTitle", name);
			            		jsonObject.addProperty("movieYear", year);
			            		jsonObject.addProperty("quantity", previousItems.get(key));
			            		
			        			resultSet.close();
			        			statement.close();
			        			System.out.println("work to here");
			        			int generatedKey = 0;
			        			for(int i = 0;i<previousItems.get(key);i++) {
				        			java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
				        			String query1 = "INSERT INTO sales (customerId,movieId,saleDate) VALUES ("+userId +",\""+ key +"\",\""+sqlDate+"\")";
				        			PreparedStatement ps = dbcon.prepareStatement(query1,
				        			        Statement.RETURN_GENERATED_KEYS);
				        			 
				        			ps.execute();
				        			 
				        			ResultSet rs = ps.getGeneratedKeys();
				        			if (rs.next()) {
				        			    generatedKey = rs.getInt(1);
				        			}
			        			}
//			            		PreparedStatement statement1 = dbcon.prepareStatement(query1);
//			            		statement1.executeUpdate();
//			                	String query2 = "Select * from sales Where customerId="+userId+" and movieId=\""+key+"\"";
//			            		PreparedStatement statement2 = dbcon.prepareStatement(query2);
//			            		ResultSet resultSet2 = statement2.executeQuery();
//			            		resultSet2.next();
//			        			jsonObject.addProperty("saleId", resultSet2.getInt("id"));
			        			jsonObject.addProperty("saleId", generatedKey);

			        			jsonArray.add(jsonObject);
			                }
		                }
	                System.out.println("its working");
	                System.out.println(jsonArray);
		            out.write(jsonArray.toString());
		            response.setStatus(200);
		            dbcon.close();
		        }
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
//			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
//		out.close();

	}

}
