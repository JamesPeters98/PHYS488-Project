import java.io.IOException;

import utils.Histogram;


public class EventsTest {

	public static void main(String[] args) throws IOException {
		EventsReader events = new EventsReader();
		
		Histogram hist = new Histogram(50, 0, 20, "Decay Length");
		
		for(Event event : events.events) {
			hist.fill(event.decayLength);
		}
		
		hist.writeToDisk("decay_lengths.csv");
	}

}
