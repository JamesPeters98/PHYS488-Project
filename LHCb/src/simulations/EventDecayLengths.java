package simulations;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import tracker.Event;
import tracker.EventSimulation;
import tracker.Particle;
import utils.EventsReader;
import utils.FindNearestPoint;
import utils.Histogram;
import utils.StraightLineFactory;

public class EventDecayLengths {

	public static void main(String[] args) throws Exception {
		
		//Set up Histogram.
		Histogram hist = new Histogram(50, 0, 20, "Decay Lengths");
		
		//Import CSV file and select event to graph.
		EventsReader events = new EventsReader();
		
		int eventN = 0;
		//Loop over every event
		for(Event event : events.events){
			eventN++;
			
			System.out.println("Simulating event " + eventN);
			if (eventN % 100 == 0) {
				System.out.println("Simulating event " + eventN);
			}
			
			//Create simulation for all particles in event.
			EventSimulation sim = new EventSimulation(event.getParticles());
				
			//Fit straight lines to points - and check if sufficient data to fit one.
			ArrayList<StraightLineFactory> factories = new ArrayList<StraightLineFactory>(); 				
			for(ArrayList<RealVector> v : sim.detections) {
				if(!v.isEmpty()) { 
					StraightLineFactory line = new StraightLineFactory(v);
					if(line.isValid()) {
						factories.add(line);
					}
				}
			}
				
			//Setup up line data to solve for nearest point.
			int n = factories.size();
			RealVector[] a = new RealVector[n];
			RealVector[] d = new RealVector[n];
			RealVector[][] lines = new RealVector[n][];	
				
			for(int i = 0; i < factories.size(); i++) {
				StraightLineFactory line = factories.get(i);
				lines[i] = line.rawVectors;
				a[i] = line.getOriginVector();
				d[i] = line.getDirectionVector();
			}
				
			//Find the nearest point to all lines.
			FindNearestPoint p = new FindNearestPoint(a, d, 3);

			//Add point to histogram.
			hist.fill(p.getPoint().getNorm());
		}
			
		//Write histogram to disk
		hist.writeToDisk("decay_lengths.csv");

	}

}
