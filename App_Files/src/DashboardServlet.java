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
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/dashboard")
public class DashboardServlet extends HttpServlet {
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

			// Construct a query with parameter represented by "?"
			String query = "show tables from moviedb";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Perform the query
			ResultSet rs = statement.executeQuery();

			JsonArray tables = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {

				String tableName = rs.getString("Tables_in_moviedb");
				Connection dbcon1 = dataSource.getConnection();
				String query1 = "show columns from " + tableName;
				PreparedStatement statement1 = dbcon1.prepareStatement(query1);
				ResultSet rs1 = statement1.executeQuery();
				JsonArray cols = new JsonArray();
				while(rs1.next()) {
					JsonObject jsonObject = new JsonObject();
					String colName = rs1.getString("Field");
					String type = rs1.getString("Type");
					jsonObject.addProperty("column", colName);
					jsonObject.addProperty("type", type);
					cols.add(jsonObject);
				}
				rs1.close();
				statement1.close();
				dbcon1.close();
				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("table", tableName);
				jsonObject.add("columns", cols);;

				tables.add(jsonObject);
			}
			
            // write JSON string to output
            out.write(tables.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		out.close();

	}

}
