package simulations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.linear.RealVector;

import flanagan.analysis.Regression;
import tracker.EventSimulation;
import utils.EventsReader;
import utils.FindNearestPoint;
import utils.Histogram;
import utils.StraightLineFactory;

public class DecayLengthSim {
	
	int n = 0;
	
	ArrayList<Future<EventSimulation>> eventSims;
	public ExecutorService service;

	public DecayLengthSim() throws Exception {
		eventSims = new ArrayList<Future<EventSimulation>>();
			
		System.out.println("Calculating Decay Lengths...");
		
		//Set up Histogram.
		//Histogram hist = new Histogram(50, 0, 0.5, "Decay Lengths");
		
		//Import CSV file and select event to graph.
		EventsReader events = new EventsReader();
		
		System.out.println("Events size: "+events.events.size());
		
		service = Executors.newCachedThreadPool();
		
		//Loop over every event
		events.events.forEach(event -> {
			Future<EventSimulation> f = service.submit(() -> {
				n++;
				if(n % 250 == 0)
				System.out.println("Simulating event "+n+"/"+events.events.size());
				
				//Create simulation for all particles in event.
				EventSimulation sim = new EventSimulation(event.getParticles(),event.getPositionVector());
				
				event.setForRemoval();
			
				return sim;
			});
			
		eventSims.add(f);
		});
		System.out.println(eventSims.size());
	}
	
	public boolean allTasksComplete() {
		boolean allDone = false;
		Iterator<Future<EventSimulation>> iter = eventSims.iterator();
		while(iter.hasNext()) {
			Future<?> future = iter.next();
			if(future.isDone()) {
				allDone = true;
			} else {
				allDone = false;
			}
		}
		return allDone;
	}
	
	private Histogram getHist(double smear) throws Exception {
		Histogram hist = new Histogram(200, 0, 2, "Decay Length - "+smear);
		
		for(Future<EventSimulation> f : eventSims) {
			EventSimulation sim = f.get();
			//Fit straight lines to points - and check if sufficient data to fit one.
			ArrayList<StraightLineFactory> factories = new ArrayList<StraightLineFactory>(); 				
			for(ArrayList<RealVector> v : sim.getSmearedDetections(smear)) {
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
			hist.fill(p.getPoint().getNorm()*1000-sim.getTruePosition().getNorm());	
		}
		return hist;
	}
	
	public double[] calculateRegression(double smear) throws Exception {
		Histogram hist = getHist(smear);
		System.out.println(Arrays.toString(hist.getContent()));
		Regression reg = new Regression(hist.getX(),hist.getContent(),hist.getError());
		double[] result = new double[2];		
		try {
			reg.exponentialSimple();
			result[0] = -reg.getBestEstimates()[0];
			result[1] = reg.getBestEstimatesErrors()[0];
			if(result[1] > 100 || reg.getDegFree() < 10) {
				result[0] = 0;
				result[1] = 0;
 			}
		} catch(Exception e) {
			result[0] = 0;
			result[1] = 0;
		}
		return result;
	}

}
