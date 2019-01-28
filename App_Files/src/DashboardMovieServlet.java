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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
//
@WebServlet(name = "DashboardMovieServlet", urlPatterns = "/api/dashboardmovie")
public class DashboardMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviemasterdb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String starName = request.getParameter("star_name");
        String genre = request.getParameter("genre");
        String director = request.getParameter("director");
        String movieYear = request.getParameter("year");
        String movieTitle = request.getParameter("title");
        genre = genre.substring(0, 1).toUpperCase() + genre.substring(1);

		response.setContentType("application/json"); // Response mime type

        try {
        // Get a connection from dataSource
        Connection dbcon = dataSource.getConnection();
        String newId = null;

        String query = "{call add_movie('"+ movieTitle +"',"+ movieYear +",'"+director+"','"+starName+"','"+genre+"',?)}";
 //Prepared Statement Stuff  

//End of prepared Statement
        
        // Perform the query
        CallableStatement cs = dbcon.prepareCall(query);
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.execute();
        String outParam = cs.getString(1);
        cs.close();
        dbcon.close();
            // Login success:
            // set this user into the session
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", outParam);
            response.getWriter().write(responseJsonObject.toString());
            
        }catch (Exception e) {
        	JsonObject responseJsonObject = new JsonObject();
        	System.out.println(e);
        	System.out.println(e.getMessage());
			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
    }
}
