import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

@WebServlet(name="CheckoutServlet", urlPatterns = "/api/checkout")

public class CheckoutServlet extends HttpServlet {
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    	
    		String firstName= request.getParameter("fname");
    		firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
    		String lastName = request.getParameter("lname");
    		lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
    		String cardNumber= request.getParameter("cardnumber");
    		String expDate = request.getParameter("cardexpdate");
    		expDate = expDate + " 00:00:00";
    	
    		//Will card number without spaces
    		String newCardNumber = null;
    	    	
    		//Checks to see if card number has spaces and creates the two variables
    		if (cardNumber.contains(" ")) {
    			System.out.println(cardNumber);
    		}
    		else{
    			newCardNumber = cardNumber.replaceAll("\\s+","");
    			newCardNumber = newCardNumber.replace("-", "");
    		}
    		
    		
    		try {
    	        Connection dbcon = dataSource.getConnection();

    	        String query = "select id, firstName, lastName, expiration from creditcards where id = ? OR id = ? AND firstName = ? AND lastName = ? AND expiration = ?";

    	        //Declare  Prepared Statement and set parameters
    	        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
    	        preparedStatement.setString(1, cardNumber);
    	        preparedStatement.setString(2, newCardNumber);
    	        preparedStatement.setString(3, firstName);
    	        preparedStatement.setString(4, lastName);
    	        preparedStatement.setString(5, expDate);

    	        //Perform the query
    	        ResultSet rs = preparedStatement.executeQuery();

    	        
    	        if(rs.next()) {
    	        	//Card in database, success,proceed to action
                    JsonObject responseJsonObject = new JsonObject();
                    
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Checkout Successful");
                    response.getWriter().write(responseJsonObject.toString());
    	        }else {
    	        	//Card not in database action
                    JsonObject responseJsonObject = new JsonObject();
                    
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "invalid Information");
                    
                    response.getWriter().write(responseJsonObject.toString());
    	        }
    	        
    	        rs.close();
    	        preparedStatement.close();
    	        dbcon.close();

            }
	        catch (Exception e) {
	        	System.out.println(e.getMessage());
				// set reponse status to 500 (Internal Server Error)
				response.setStatus(500);
	        }
    }
}