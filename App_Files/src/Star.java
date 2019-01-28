
import java.sql.Date;  
import java.util.ArrayList;

public class Star {
	private int id;
	private String first_name;
	private String last_name;
	private String date_of_birth;
	
	ArrayList<Movie> Movie_list;
	
	@SuppressWarnings("deprecation")
	public Star(){
		super();
		this.id = 0;
		this.first_name = "";
		this.last_name = "";
		this.date_of_birth = "";
		this.Movie_list = new ArrayList<Movie>();
	}
	public Star(int id, String first_name, String last_name, String dob) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.date_of_birth = dob;
		this.Movie_list = new ArrayList<Movie>();
	}
	public int getId() {
		return id;
	}
	public void addMovie(Movie m){
		Movie_list.add(m);
	}
	public ArrayList<Movie> getMovies(){
		return Movie_list;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String firstName) {
		this.first_name = firstName;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String lastName) {
		this.last_name = lastName;
	}
	public String getDob() {
		return date_of_birth;
	}
	public void setDob(String dob) {
		this.date_of_birth = dob;
	}
}