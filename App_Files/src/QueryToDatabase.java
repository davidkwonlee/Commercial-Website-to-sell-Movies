import java.io.IOException;
import java.util.Random;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

public class QueryToDatabase {
	
	private static Connection dbcon;
	private static final String loginUser = "root";
	private static final String loginPassword = "123";
	private static final String loginUrl = "jdbc:mysql://localhost:3306/moviedb?useSSL=false";

	public static void batchInsert(Movie movie, String genre, ArrayList<Star> stars) throws SQLException{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
	      
			dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPassword);

			for(int i = 0; i < stars.size(); i++){
				if(stars.get(i) != null){
					//QUERY FOR MOVIES
					String query = "select * from movies where id=?";
					
			        PreparedStatement preparedStatement = dbcon.prepareStatement(query); 
			        
                    preparedStatement.setString(1, movie.getId());  
					ResultSet resultSet = preparedStatement.executeQuery();
					
					if(!resultSet.next()) {
						String query2 = "INSERT INTO movies VALUES('" + movie.getId() + "', " + '"'+ movie.getTitle() + '"' + " , "+ movie.getYear() + ", '" + movie.getDirector() + "')";
					 	preparedStatement.executeUpdate(query2);
					}
					else {
						//System.out.println("Duplicate value for Movie: " + movie.getTitle());
						;
					}
				    preparedStatement.close();
//------------------------------------------------------------------------------------------------------------------------------------------------
			        //HANDLE STARS
				    String query_stars = "select MAX(id) from stars";
				    PreparedStatement preparedStatementStars = dbcon.prepareStatement(query_stars); 
				    ResultSet resultSetStars = preparedStatementStars.executeQuery();

				    if(resultSetStars.next()) {

				    	Long highestID = Long.parseLong(resultSetStars.getString(1).substring(2)) + 1;
			        	String newId = "nm" + highestID;
				    	String fname = stars.get(i).getFirst_name();
				    	String lname = stars.get(i).getLast_name();	
				    	String dateofbirth = stars.get(i).getDob();	
				    	String concatenated_name =  fname + " " +lname;
				    	
				    	if(fname.length() > 0 && dateofbirth.length() > 0) {;
				    		
						    PreparedStatement preparedStatementStars2 = null;
						    
				    		String query_stars_2 = "select * from stars where stars.name = \""+ concatenated_name + "\"";
				            preparedStatementStars2 = dbcon.prepareStatement(query_stars_2);	
				            System.out.println(preparedStatementStars2);
				    		ResultSet resultSetStars2 = preparedStatementStars2.executeQuery();
				    		
				    		//System.out.println("ID: " + movie.getId() + " " + " Title: " + movie.getTitle() + " Year: " + movie.getYear() + " Movie Director: " + movie.getDirector());
				    		try {
					    		if(!resultSetStars2.next()) {

					    			System.out.println("here1");
					    			try {
						    			System.out.println("here2");
					    				String query_stars_3 = "INSERT INTO stars VALUES('" + newId + "', " + '"'+ fname + " " + lname + '"' + " , "+ dateofbirth + ")";
					    				
					    				//dbcon1 = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
					    				
					    				Connection dbcon1 = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
					    				
					    				preparedStatementStars = dbcon1.prepareStatement(query_stars_3); 
					    				preparedStatementStars.executeUpdate(query_stars_3);
    				
					    				String query_stars_movies= "INSERT INTO stars_in_movies VALUES('" +  newId + "' , '" + movie.getId() + "')";
					    				
					    				Connection dbcon2 = DriverManager.getConnection(loginUrl, loginUser, loginPassword);
					    				preparedStatementStars = dbcon2.prepareStatement(query_stars_movies); 		
					    				preparedStatementStars.executeUpdate(query_stars_movies);	
					    				
					    				//String genreid_movieid_query = "INSERT INTO stars_in_movies ('" + genre+ '"' + movie.getId() + ")";

					    			}catch(Error e){
					    				System.out.println("REPEATED ID Error");
					    			}catch(NullPointerException e){
					    	           System.out.print("NullPointerException caught");
					    	           
					    	        
					    	        }
					    	}else {
						    		;
						    	}
				    		}catch(NullPointerException e){
				             System.out.print("NullPointerException caught");
				            }
				    		 preparedStatementStars2.close();
				    }
				    preparedStatementStars.close();
			}
		}
	}
	}catch(Exception e){
		e.printStackTrace();
		dbcon.close();
	}finally{
		dbcon.close();
		}
	}
}


/*
String query_stars = "select MAX(id) from stars";
PreparedStatement preparedStatementStars = dbcon.prepareStatement(query_stars); 
ResultSet resultSetStars = preparedStatementStars.executeQuery();
PreparedStatement preparedStatementStars2 = null;

if(resultSetStars.next()) {
	Long highestID = Long.parseLong(resultSetStars.getString(1).substring(3));
	String fname = stars.get(i).getFirst_name();
	String lname = stars.get(i).getLast_name();	
	String dateofbirth = stars.get(i).getDob();
	
	String concatenated_name =  fname + " " +lname;
	if(fname.length() > 0 && dateofbirth.length() > 0) {
		String query_stars_2 = "select * from stars where stars.name = '"+ concatenated_name + "'";
        preparedStatementStars2 = dbcon.prepareStatement(query_stars_2); 
		ResultSet resultSetStars2 = preparedStatementStars2.executeQuery();
		Random rand = new Random();
		if(!resultSetStars2.next()) {
			try {
				highestID++;
				//String query_stars_3 = "INSERT INTO stars VALUES('" + highestID + "', " + '"'+ fname + " " + lname + '"' + " , "+ dateofbirth + ")";
				String query_stars_3 = "INSERT INTO stars VALUES('" +highestID+ "', '" +  concatenated_name  + "', '" + dateofbirth + "')";
				preparedStatementStars.executeUpdate(query_stars_3);
			}catch(Error e){
				System.out.println("REPEATED ID Error");
			}catch(NullPointerException e){
	           System.out.print("NullPointerException caught");
	        }
	}
	else {
		;
	}
}

preparedStatementStars.close();
preparedStatementStars2.close();
 */



/*
if(!resultSet.next()) {
	String query2 = "INSERT INTO movies VALUES('" + movie.getId() + "', " + '"'+ movie.getTitle() + '"' + " , "+ movie.getYear() + ", '" + movie.getDirector() + "')";
 	preparedStatement.executeUpdate(query2);
}
else {
	System.out.println("Duplicate value for Movie: " + movie.getTitle());
}			        
 */

//System.out.println("Star ID: " + stars.get(i).getId() + " Stars: " + stars.get(i).getFirst_name() + " DOB " + stars.get(i).getDob());


//System.out.println("MOVIE TITLE: " + movie.getTitle() + ". " +"MOVIE YEAR: " + movie.getYear() + ". " + "MOVIE DIRECTOR: " + movie.getDirector() + ". " + "Movie Genre: " + genre  + ". " + "Stars: " + stars.get(i).getFirst_name() + ". " + "Last Name: " + stars.get(i).getLast_name() + ". " + "set DOB " +  );
//For movies
//System.out.println("ID: " + movie.getId() + " " + " Title: " + movie.getTitle() + " Year: " + movie.getYear() + " Movie Director: " + movie.getDirector());

//For Stars
//YOU GENERATE ID
//System.out.println("Star ID: " + stars.get(i).getId() + " Stars: " + stars.get(i).getFirst_name() + " DOB " + stars.get(i).getDob());


//For Genres
//ID -> generate
//Name
//System.out.println("GENRE: " + genre);