package simulations;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.linear.RealVector;

import flanagan.analysis.Regression;
import flanagan.plot.PlotGraph;
import tracker.Event;
import tracker.EventSimulation;
import utils.EventsReader;
import utils.FindNearestPoint;
import utils.GraphValues;
import utils.Histogram;
import utils.StraightLineFactory;

public abstract class Simulation {
	
	public String name; //Simulation name.
	public ExecutorService service;
	
	private int n = 0;
	private ArrayList<Future<EventSimulation>> eventSims;
	private int simulationLoops;
	private int currentLoop = 0;
	private boolean simStarted = false;
	private double histStart, histEnd;
	private int bins;
		
	public Simulation(String name, int simulationLoops, int bins, double histStart, double histEnd) throws Exception {
		this.name = name;
		this.simulationLoops = simulationLoops;
		this.bins = bins;
		this.histStart = histStart;
		this.histEnd = histEnd;
		if(simulationLoops <= 0) throw new Exception("Number of Simulation loops can't be 0 or less!");
		eventSims = new ArrayList<Future<EventSimulation>>();
		
		System.out.println("Performing Simulation of "+name);
		
		//Import CSV file and select event to graph.
		new EventsReader();
		
		//Set up Executor to multi-thread the simulation.
		service = Executors.newCachedThreadPool();
	}
	
	//Starts simulation and holds until Simulation complete.
	public void start() throws Exception {
		simStarted = true;
		while(currentLoop < simulationLoops) {
			n = 0;
			System.out.println("Loop "+currentLoop);
			//Loop over every event
			EventsReader.getEvents().values().forEach(event -> {
				event.setup();
				Future<EventSimulation> f = service.submit(() -> {
					n++;
					//Print out simulation progress.
					if(n % 250 == 0)
					System.out.println("Simulating event "+n+"/"+EventsReader.getEvents().size());
					
					return EventLoop(event.getId(),currentLoop);
				});
						
				eventSims.add(f);
				});
			
			//Hold until complete.
			while(!allTasksComplete());
			System.out.println("Simulation Finished");
			postSimulation(currentLoop);
			eventSims = new ArrayList<Future<EventSimulation>>();
			currentLoop++;
		}
		postSimulationLoop();
		//events.events = null;
	}
	
	/**
	 * Called after every Loop
	 * 
	 * @param event
	 * @return EventSimulation
	 */
	public abstract EventSimulation EventLoop(int eventId, int currentLoop);
	
	
	/**
	 * Called after each simulation has finished.
	 * @param currentLoop
	 * @throws Exception
	 */
	public abstract void postSimulation(int currentLoop) throws Exception;
	
	/**
	 *  Called after all simulation loops have completed.
	 */
	public abstract void postSimulationLoop();
	
	public abstract GraphValues configureGraph();
	
	public void plotGraph() throws Exception {
		if(!simStarted) throw new Exception("Simulation must be run with start() before plotting graph");
		GraphValues vals = configureGraph();
		PlotGraph plot = new PlotGraph(vals.xVals,vals.yVals);
		plot.setGraphTitle(name);
		plot.setErrorBars(0, vals.Errors);
		plot.setXaxisLegend(vals.xAxis);
		plot.setYaxisLegend(vals.yAxis);
		plot.plot();
	}
	
	public void saveRawDataToCSV(String filename) throws Exception {
		if(!simStarted) throw new Exception("Simulation must be run with start() before saving data");
        
		GraphValues vals = configureGraph();			
		FileWriter file = new FileWriter(filename);     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
        outputFile.println(vals.xAxis+"("+vals.xUnit+")"+ "," + vals.yAxis+"("+vals.yUnit+")" + ", Error");

        // now make a loop to write the contents of each bin to disk, one number at a time
        // together with the x-coordinate of the centre of each bin.
        for (int n = 0; n < vals.xVals.length; n++) {
            // comma separated values
            outputFile.println(vals.xVals[n] + "," + vals.yVals[n] + "," + vals.Errors[n]);
        }
        outputFile.close(); // close the output file
	}
	
	public void shutdown() {
		service.shutdown();
		try {
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		}
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
		Histogram hist = new Histogram(bins,histStart,histEnd, "Decay Length - "+smear);
		
		for(Future<EventSimulation> f : eventSims) {
			n++;
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
			
			if(p.getPoint().getDimension() == 3) hist.fill(calculateHistogramValue(p.getPoint(), sim));
		}
		//hist.print();
		return hist;
	}
	
	//Return a value to add to histogram.
	public abstract double calculateHistogramValue(RealVector point, EventSimulation sim);
	
	public double[] calculateRegression(double smear) throws Exception {
		Histogram hist = getHist(smear);
		//System.out.println(Arrays.toString(hist.getX()));
		//System.out.println(Arrays.toString(hist.getContent()));

		Regression reg = new Regression(hist.getX(),hist.getContent(),hist.getError());
		double[] result = new double[2];		
		try {
			reg.exponentialSimple();
			result[0] = -reg.getBestEstimates()[0];
			result[1] = reg.getBestEstimatesErrors()[0];
			if(result[1] > 100 || reg.getDegFree() < 20) {
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
