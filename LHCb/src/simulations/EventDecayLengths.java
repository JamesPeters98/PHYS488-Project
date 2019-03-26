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
		
		long time = System.currentTimeMillis();
		
		//Set up Histogram.
		Histogram hist = new Histogram(50, 0, 20, "Decay Lengths");
		
		//Import CSV file and select event to graph.
		EventsReader events = new EventsReader();
		
		System.out.println("Events size: "+events.events.size());
		int size = events.events.size();
		//Loop over every event
		for(int n=0; n<size; n++){
			Event event = events.events.get(n);
			
			if (n % 10 == 0) {
				System.out.println("Events size: "+events.events.size());
				System.out.println("Simulating event " + n);
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
			int n2 = factories.size();
			RealVector[] a = new RealVector[n2];
			RealVector[] d = new RealVector[n2];
			RealVector[][] lines = new RealVector[n2][];	
				
			for(int i = 0; i < factories.size(); i++) {
				StraightLineFactory line = factories.get(i);
				lines[i] = line.rawVectors;
				a[i] = line.getOriginVector();
				d[i] = line.getDirectionVector();
			}
				
			//Find the nearest point to all lines.
			FindNearestPoint p = new FindNearestPoint(a, d, 3);

			//Add point to histogram.
			hist.fill(p.getPoint().getNorm()*1000);	
			
			event.setForRemoval();
			sim = null;
			p = null;
			factories = null;
			
			//System.gc();
		}
			
		//Write histogram to disk
		hist.writeToDisk("decay_lengths.csv");
		
		long finalTime = System.currentTimeMillis();
		
		System.out.println("Time taken: "+(finalTime-time)/1000+"s");

	}

}
