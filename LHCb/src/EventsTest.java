import java.io.FileNotFoundException;

public class EventsTest {

	public static void main(String[] args) throws FileNotFoundException {
		EventsReader events = new EventsReader();

		events.events.get(0).print();
	}

}
