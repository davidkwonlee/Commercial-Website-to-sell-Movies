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
@WebServlet(name = "DashboardStarServlet", urlPatterns = "/api/dashboardstar")
public class DashboardStarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviemasterdb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("name");
        String birthYear = request.getParameter("year");
		response.setContentType("application/json"); // Response mime type

        try {
        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        Connection dbcon1 = dataSource.getConnection();
        String query1 = "select max(id) as id from stars";
        PreparedStatement preparedStatement1 = dbcon1.prepareStatement(query1); 
        ResultSet rs1 = preparedStatement1.executeQuery();
        int highId = 0;
        String newId = null;
        if(rs1.next()) {
        	String preId = rs1.getString("id");
        	highId = Integer.parseInt(preId.substring(2)) + 1;
        	newId = "nm" + highId;
        }else {
        	System.out.println("couldnt find max id");
        }
        String query = null;
        System.out.println(birthYear);
        if(birthYear == null || birthYear.equals("")) {
        	query = "INSERT INTO stars (id,name) VALUES (\""+newId+"\",\""+starName+"\")";
        }else {
        	query = "INSERT INTO stars (id,name,birthYear) VALUES (\""+newId+"\",\""+starName+"\","+ birthYear +")";
        }
 //Prepared Statement Stuff  
        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 

//End of prepared Statement
        
        // Perform the query
        preparedStatement.executeUpdate();
        

        preparedStatement.close();
        dbcon.close();
        preparedStatement1.close();
        dbcon1.close();
            // Login success:
            // set this user into the session
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Added Star");
            response.getWriter().write(responseJsonObject.toString());
            
        }catch (Exception e) {
        	System.out.println(e);
        	System.out.println(e.getMessage());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
    }
}
