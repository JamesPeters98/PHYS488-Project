import java.io.FileNotFoundException;
import java.util.Arrays;

import flanagan.analysis.Regression;
import utils.EventsReader;
import utils.Histogram;

public class ShowHistogram {

	public static void main(String[] args) throws FileNotFoundException {
		//Decay Length Histogram
		Histogram histLength = new Histogram(50, 0, 25, "Decay Length");
		//Decay Time Histogram
		Histogram histTime = new Histogram(50, 0, 15, "Decay Time");
		
		//Read events from CSV.
		EventsReader reader = new EventsReader();
		
		//Loop over every event.
		reader.events.forEach(event -> {
			histLength.fill(event.getDecayLength());
			histTime.fill(event.getDecayTime());
		});
		
		//Regression  for Decay Length.
		Regression regLength = new Regression(histLength.getX(),histLength.getContent(),histLength.getError());
		regLength.exponentialSimplePlot();
		System.out.println(Arrays.toString(regLength.getBestEstimates()));
		
		//Regression  for Decay Length.
		Regression regTime = new Regression(histTime.getX(),histTime.getContent(),histTime.getError());
		regTime.exponentialSimplePlot();
		System.out.println(Arrays.toString(regTime.getBestEstimates()));
		
		

	}

}
