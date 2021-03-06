
import java.sql.Connection;
import java.util.ArrayList;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class JointHandler extends DefaultHandler {
	ArrayList<Star> stars;
	TreeMap<String, Movie> m;
	TreeMap<String, String> g;
	
	Movie movie;
	//int movieCount;
	
	TreeMap<String, Star> s;
	
	String tempVal;
	String genreOfMov; 
	String idMap;
	int tempInt;
	
	//private Connection connection = QueryToDatabase.initConnection();
	public JointHandler(TreeMap<String, Movie> mov, TreeMap<String, Star> star, TreeMap<String, String> genre){
		//movieCount = numberOfMovies;
		this.m = mov;
		this.s = star;
		this.g = genre;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(qName.equalsIgnoreCase("filmc")){
			//System.out.println("called");
			stars = new ArrayList<Star>();
		}
	}
	
	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
			tempVal = new String(ch,start,length);
	}
	      
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if(qName.equalsIgnoreCase("f")){
			if(m.containsKey(tempVal)){
				movie = m.get(tempVal);
				genreOfMov = g.get(tempVal); 
				//System.out.println(m.get(tempVal).getTitle());
			}
		}
		else if(qName.equalsIgnoreCase("a")){
			if(s.containsKey(tempVal)){
				stars.add(s.get(tempVal));
			}
		}
		else if(qName.equalsIgnoreCase("filmc")){
			try{	
				QueryToDatabase.batchInsert(movie, genreOfMov, stars);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}
}

