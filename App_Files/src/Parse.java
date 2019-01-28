
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

public class Parse {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
	File mov = new File("mains243.xml");
	File act = new File("actors63.xml");
	File cast = new File("casts124.xml");
	int numberOfMovies;
	
	InputStream inputStreamMov = new FileInputStream(mov);
	InputStream inputStreamAct = new FileInputStream(act);
	InputStream inputStreamCast = new FileInputStream(cast);
	
	InputStreamReader inputReader1 = new InputStreamReader(inputStreamMov, "ISO-8859-1");
	InputStreamReader inputReader2 = new InputStreamReader(inputStreamAct, "ISO-8859-1");
	InputStreamReader inputReader3 = new InputStreamReader(inputStreamCast, "ISO-8859-1");
	InputSource INPUT1 = new InputSource(inputReader1);
	
	INPUT1.setEncoding("ISO-8859-1");
	InputSource INPUT2 = new InputSource(inputReader2);
	INPUT2.setEncoding("ISO-8859-1");
	InputSource INPUT3 = new InputSource(inputReader3);
	INPUT3.setEncoding("ISO-8859-1");
	
	if (mov != null) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			javax.xml.parsers.SAXParser saxParser = factory.newSAXParser();
			
			UserHandler userHandler = new UserHandler();
			saxParser.parse(INPUT1, userHandler);
			
			TreeMap<String, Movie> movie_data = userHandler.idToMovie;
			
			TreeMap<String, String> genre_data = userHandler.idToGenre;
			
			CastsHandler castsHandler = new CastsHandler();		
			saxParser.parse(INPUT2, castsHandler);
			TreeMap<String, Star> star_data = castsHandler.starInfo;

			JointHandler jointHandler = new JointHandler(movie_data, star_data,genre_data);

			saxParser.parse(INPUT3, jointHandler);

		} catch (Exception e) {
			System.out.println("DUPLICATE VALUE ERROR");
		}
	}
}
}
