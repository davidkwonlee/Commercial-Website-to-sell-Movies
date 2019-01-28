

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// this annotation maps this Java Servlet Class to a URL
@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MovieServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // change this to your own mysql username and password
		
        String loginUser = "root";
        String loginPasswd = "123";//configure sql for aws
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        // set response mime type
        response.setContentType("text/html"); 

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head><title>Fabflix</title></head>");
        
        
        try {
        		Class.forName("com.mysql.jdbc.Driver").newInstance();
        		// create database connection
        		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		Connection connection1 = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        		// declare statement
        		Statement statement = connection.createStatement();
        		Statement statement1 = connection.createStatement();
        		// prepare query
        		String query = "Select cts.id,title,year,director,cts.rating,genres.name from (Select id,title,year,director,bts.rating,genreId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id order by ratings.rating desc limit 20) as bts inner join genres_in_movies on bts.id = genres_in_movies.movieId) as cts inner join genres on genres.id = cts.genreId";
        		String query1 = "Select cts.id,title,year,director,cts.rating,cts.starId,stars.name from (Select id,title,year,director,bts.rating,starId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id order by ratings.rating desc limit 20) as bts inner join stars_in_movies on bts.id = stars_in_movies.movieId) as cts inner join stars on stars.id = cts.starId";
//        		String query = "Select * from movies inner join ratings on ratings.movieId = movies.id order by ratings.rating desc limit 20";
        		// execute query
        		ResultSet resultSet = statement.executeQuery(query);
        		ResultSet resultSet1 = statement1.executeQuery(query1);
//              Select cts.id,title,year,director,cts.rating,cts.starId,stars.name from (Select id,title,year,director,bts.rating,starId from (Select id,title,year,director,rating from movies inner join ratings on ratings.movieId = movies.id order by ratings.rating desc limit 20) as bts inner join stars_in_movies on bts.id = stars_in_movies.movieId) as cts inner join stars on stars.id = cts.starId        		
        		out.println("<body>");
        		out.println("<h1>MovieDB Stars</h1>");
        		
        		out.println("<table border>");
        		
        		// add table header row
        		out.println("<tr>");
        		out.println("<td>Title</td>");
        		out.println("<td>Year</td>");
        		out.println("<td>Director</td>");
        		out.println("<td>List of Genres</td>");
        		out.println("<td>List of Stars</td>");
        		out.println("<td>Rating</td>");
        		out.println("</tr>");
        		
        		// add a row for every star result
       			String movieTitle = null;
    			String movieYear = null;
    			String movieDirector = null;
    			String genre = null;
    			String rating = null;
        		
        		while (resultSet.next()) {
        			// get a star from result set
        			String genres = "";
        			String movieId = resultSet.getString("id");
        			movieTitle = resultSet.getString("title");
        			movieYear = resultSet.getString("year");
        			movieDirector = resultSet.getString("director");
        			rating = resultSet.getString("rating");
        			genre = resultSet.getString("name");
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
        			//get rest stars
        			String stars = "";
    				Boolean first = true;
        			while(resultSet1.next()) {

        				String starMovieId = resultSet1.getString("id");
        				if(starMovieId.equals(movieId)) {
        					if(first) {
        						stars = resultSet1.getString("name");
        						first = false;
        					}
        					else {
        						stars =  stars + ", " + resultSet1.getString("name");
        					}
    					}
        			}
        			resultSet1.absolute(0);
        			
        			out.println("<tr>");
        			out.println("<td>" + movieTitle + "</td>");
        			out.println("<td>" + movieYear + "</td>");
        			out.println("<td>" + movieDirector + "</td>");
        			out.println("<td>" + genres + "</td>");
        			out.println("<td>" + stars + "</td>");
        			out.println("<td>" + rating + "</td>");
        			out.println("</tr>");
        			

        			}

        		
        		out.println("</table>");
        		
        		out.println("</body>");
        		
        		resultSet.close();
        		resultSet1.close();
        		statement.close();
        		connection.close();
        		
        } catch (Exception e) {
        		/*
        		 * After you deploy the WAR file through tomcat manager webpage,
        		 *   there's no console to see the print messages.
        		 * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
        		 * 
        		 * To view the last n lines (for example, 100 lines) of messages you can use:
        		 *   tail -100 catalina.out
        		 * This can help you debug your program after deploying it on AWS.
        		 */
        		e.printStackTrace();
        		
        		out.println("<body>");
        		out.println("<p>");
        		out.println("Exception in doGet: " + e.getMessage());
        		out.println("</p>");
        		out.print("</body>");
        }
        
        out.println("</html>");
        out.close();
        
	}


}
