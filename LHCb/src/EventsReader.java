import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class EventsReader {
	
	public ArrayList<Event> events;
	
	public EventsReader() throws FileNotFoundException {
		events = new ArrayList<Event>();
		
		Scanner scanner = new Scanner(new File("b0vectors.csv"));
        scanner.useDelimiter(" Next... ");
        
        while(scanner.hasNext()){
            Scanner event = new Scanner(scanner.next());
            event.useDelimiter("\n ");
            
            ArrayList<Double> data = new ArrayList<Double>();
            Event e = new Event(5);
            
            while(event.hasNext()) {
            	String row = event.next();
            	String[] values = row.split(", ");
            	for(String value : values) {
            		double v = toDouble(value);
            		if(!Double.isNaN(v)) data.add(v);
            	}
            }
            
        	e.setTruePos(data.get(0),data.get(1),data.get(2));
        	// Add rest of values here.
        	
        	events.add(e);
            
            
            event.close();
        }
        scanner.close();
	}
	
	private double toDouble(String s) {
		try { 
			return Double.valueOf(s);
		} catch(Exception e) { 
			return Double.NaN;
		}
	}

}

