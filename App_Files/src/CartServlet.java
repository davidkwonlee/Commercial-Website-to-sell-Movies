import javax.annotation.Resource;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
// Declaring a WebServlet called SessionServlet, which maps to url "/session"
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart-session")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException {
    	try {
    	Connection dbcon = dataSource.getConnection();
    	response.setContentType("application/json");
        HttpSession session = request.getSession(); // Get a instance of current session on the request
        HashMap<String,Integer> previousItems = (HashMap<String,Integer>) session.getAttribute("previousItems"); // Retrieve data named "previousItems" from session

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems ArrayList for the user
        if (previousItems == null) {
            previousItems = new HashMap<String,Integer>();
            session.setAttribute("previousItems", previousItems); // Add the newly created ArrayList to session, so that it could be retrieved next time

        }
        String removeMovie = request.getParameter("remove");
        String emptyCart = request.getParameter("emptyCart");
        String newItem = request.getParameter("movieId"); // Get parameter that sent by GET request url
        String test = request.getParameter("qty");
        Integer quantity = null;
        if(test != null) {
        	quantity = Integer.parseInt(request.getParameter("qty")); // Get parameter that sent by GET request url

        }
        System.out.println(emptyCart);
        PrintWriter out = response.getWriter();

        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we lock the ArrayList while updating
        
        synchronized (previousItems) {
        	JsonArray jsonArray = new JsonArray();
        	if (newItem != null && quantity != null && quantity == 0) {
            	previousItems.remove(newItem);
        	}else if (newItem != null && quantity != null) {
                previousItems.put(newItem,quantity); // Add the new item to the previousItems ArrayList
            }else if(emptyCart != null && emptyCart.equals("true")){
            	for (String key : previousItems.keySet()) {
            		previousItems.remove(key);
            	}
            }else if(removeMovie != null && removeMovie.equals("true")){
            	if(previousItems.containsKey(newItem)) {
            		previousItems.remove(newItem);
            	}
            }else {
            	
                if (previousItems.size() != 0) {

	            	for (String key : previousItems.keySet()) {
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
	            		jsonArray.add(jsonObject);
	        			resultSet.close();
	        			statement.close();
	                }
                }	
            }
            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();
        }
    	}catch (Exception e) {
			// write error message JSON object to output
			// set reponse status to 500 (Internal Server Error)
    		System.out.println(e.getMessage());
    		System.out.println(e);
			response.setStatus(500);
		}
        
        
    }
}

