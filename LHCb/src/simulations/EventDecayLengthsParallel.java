package simulations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import tracker.Event;
import tracker.EventSimulation;
import tracker.Particle;
import utils.EventsReader;
import utils.FindNearestPoint;
import utils.Histogram;
import utils.StraightLineFactory;

public class EventDecayLengthsParallel {
	
	static int n = 0;

	public static void main(String[] args) throws Exception {
		
		long time = System.currentTimeMillis();
		
		//Set up Histogram.
		Histogram hist = new Histogram(50, 0, 20, "Decay Lengths");
		
		//Import CSV file and select event to graph.
		EventsReader events = new EventsReader();
		
		System.out.println("Events size: "+events.events.size());
		
		ExecutorService service = Executors.newFixedThreadPool(4);
//		ExecutorService service = Executors.newCachedThreadPool();
		
		//System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
		//Loop over every event
		events.events.forEach(event -> {
			service.execute(() -> {
			try {	
				
				if(event.getId() % 10 == 0)
				System.out.println("Simulating event " + event.getId());
				
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
				System.gc();
			} catch(Exception e) {
				
			}
			
			});
		});
		
		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		}
			
		//Write histogram to disk
		hist.writeToDisk("decay_lengths.csv");
		
		long finalTime = System.currentTimeMillis();
		
		System.out.println("Time taken: "+(finalTime-time)/1000+"s");

	}

}
