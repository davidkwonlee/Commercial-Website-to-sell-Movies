import com.google.gson.JsonArray;

import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;


import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
//
@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        try {
//            RecaptchaVerifyUtils.verify(gRecaptchaResponse,RecaptchaConstants.ANDROID_SECRET_KEY);
//        } catch (Exception e) {
//            JsonObject responseJsonObject = new JsonObject();
//            responseJsonObject.addProperty("status", "fail");
//            responseJsonObject.addProperty("message", e.getMessage());
//            response.getWriter().write(responseJsonObject.toString());
//            response.setStatus(500);
//            return;
//        }
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        
        try {
        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
           
        
        String query = "SELECT customers.password,customers.id FROM customers WHERE email = ?";
 //Prepared Statement Stuff  
        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
        preparedStatement.setString(1, username);

//End of prepared Statement
        
        // Perform the query
        ResultSet rs = preparedStatement.executeQuery();
        
        Boolean userFound = false;
        String actual_pass = null;
        String userId = null;
        
    	//userFound = new StrongPasswordEncryptor().checkPassword(password, actual_pass);
        
        //Initialize Password check success
		boolean success = false;

        if(rs.next()) {
        	userFound = true;
        	
        	actual_pass = rs.getString("password");
        	
        	//Compares password to encrypted password in db
			success = new StrongPasswordEncryptor().checkPassword(password, actual_pass);

        	userId = rs.getString("id");
    	
        }else {
        	//user not found
        }

        rs.close();
        preparedStatement.close();
        dbcon.close();
        System.out.println(success);
        if (userFound && success == true) {
            // Login success:
            // set this user into the session
            request.getSession().setAttribute("user", new User(username));
            JsonObject responseJsonObject = new JsonObject();
            session.setAttribute("userId", userId);
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            response.getWriter().write(responseJsonObject.toString());
            
        } else {
            // Login fail
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            if (!userFound) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else{
                responseJsonObject.addProperty("message", "incorrect password");
            }
            response.getWriter().write(responseJsonObject.toString());
        }
        }catch (Exception e) {
        	System.out.println(e);
        	System.out.println(e.getMessage());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
    }
}
