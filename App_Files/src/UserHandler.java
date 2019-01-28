
import java.util.ArrayList; 
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserHandler extends DefaultHandler {
	
	private Movie movie;
	ArrayList<Movie> myMovie = new ArrayList<Movie>();
	
	TreeMap<String, Movie> idToMovie = new TreeMap<String, Movie>();
	
	TreeMap<String, String> idToGenre = new TreeMap<String, String>();
	
	String idMap = "";
	
	String genreOfMov = "";
	
	String tempVal;  
	
	int tempInt;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("film")){
			//System.out.println("enter");
			System.out.println();
			movie = new Movie();
		}
		else if(qName.equalsIgnoreCase("dirs")){
			;
			//System.out.println("enter director");
		}
		else if(qName.equalsIgnoreCase("cats")){
			//System.out.println("Genre");
			;
		}
	}
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
			tempVal = new String(ch,start,length);
	}
	      
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(qName.equalsIgnoreCase("fid")){
			idMap = tempVal;
			movie.setId(idMap);
			//System.out.println("Movie ID: " + idMap  + " ");
		}
		else if(qName.equalsIgnoreCase("t")){
			movie.setTitle(tempVal);
			//System.out.print("Movie Title: " + tempVal + " ");
		}
		else if(qName.equalsIgnoreCase("year")){
			if(tempVal.matches("[0-9]+")){
				int s = Integer.parseInt(tempVal);
				//System.out.print("Movie Year: " + s + " ");
				movie.setYear(s);
			}
		}
		else if(qName.equalsIgnoreCase("dirn")){
			movie.setDirector(tempVal);
			//System.out.print("Movie Director: " + tempVal + " ");
		}
		else if(qName.equalsIgnoreCase("cat")){
			genreOfMov = tempVal;
			//System.out.print("Movie Genre: " + tempVal + " ");
			//System.out.println("\n");
		}
		
		if(qName.equalsIgnoreCase("film")){
			idToMovie.put(idMap, movie);
			movie.setTotalMovies(idToMovie.size());
			idToGenre.put(idMap, genreOfMov);
		}
		
	}
	
}
