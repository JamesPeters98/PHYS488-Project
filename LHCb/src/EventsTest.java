import java.io.FileNotFoundException;

public class EventsTest {

	public static void main(String[] args) throws FileNotFoundException {
		EventsReader events = new EventsReader();

		for(Event event : events.events) {
			System.out.println("Pos| x:"+event.x+" y:"+event.y+" z:"+event.z);
		}
	}

}
