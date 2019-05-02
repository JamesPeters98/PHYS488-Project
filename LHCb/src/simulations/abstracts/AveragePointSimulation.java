package simulations.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.math3.linear.RealVector;

import flanagan.analysis.Regression;
import tracker.Event;
import tracker.EventSimulation;
import utils.EventsReader;
import utils.GraphValues;
import utils.Histogram;
import utils.Value;

public abstract class AveragePointSimulation extends Simulation {
	
	protected double start;
	protected double step;
	protected Histogram hist;
	protected List<Value> averageDistances;
	
	protected int eventId;
	private int repeatMeasurements;
	private int separateMeasurements;
	
	
	public AveragePointSimulation(String name, int eventId, int repeatMeasurements, int n, double start, double step, double smear) throws Exception {
		super(name);
		this.name = name;
		this.start = start;
		this.step = step;
		this.smear = smear;
		this.eventId = eventId;
		this.repeatMeasurements = repeatMeasurements;
		this.separateMeasurements = n;
		eventSims = new ArrayList<Future<EventSimulation>>();
		averageDistances = new ArrayList<Value>();
		hist = getHist();
	}
	
	public AveragePointSimulation(String name, int eventId, int repeatMeasurements, int n, double start, double step) throws Exception {
		this(name, eventId, repeatMeasurements, n, start, step, start);
	}
	
	@Override
	public void start() throws Exception {
		long time1 = System.currentTimeMillis();
		this.simStarted = true;
		
		for(int s = 0; s < separateMeasurements; s++) {
			System.out.println("Starting Loop "+(s+1)+" of repeated measurements.");
			for(int r = 0; r < repeatMeasurements; r++) {
				n = 0;
				
				//Loop over every event
				Event event = EventsReader.getEvent(eventId);
				
				event.setup();
				final int i = (s*repeatMeasurements)+r;
				Future<EventSimulation> f = service.submit(() -> {
					n++;
					EventSimulation e = EventLoop(event.getId(),i);
					return e;
				});
							
				eventSims.add(f);
				
				//Hold until complete.
				currentLoop++;
			}
			System.out.println("Waiting for sims to complete...");
			while(!allTasksComplete(eventSims)) {
				
			}
			postSimulation(s);
			eventSims = new ArrayList<Future<EventSimulation>>();
			System.out.println("Completed Loop "+(s+1)+" of repeated measurements.");
		}
		
		postSimulationLoop();
		System.out.println("Simulation Finished");
		long finalTime = System.currentTimeMillis()-time1;
		System.out.println("Running time: "+finalTime/1000.0+"s");
	}
	
	@Override
	public EventSimulation EventLoop(int eventId, int currentLoop) throws Exception {
		int i = (currentLoop)/repeatMeasurements;
		
		EventSimulation sim = new EventSimulation(eventId);
		eventManipulation(sim,i);
		sim.startSimulation();
		
		RealVector point = findNearestPoint(sim,smear);
		double dist = point.mapMultiply(1000).subtract(sim.getTruePosition()).getNorm();
		hist.fill(dist);
		return null;
	}

	@Override
	public double calculateHistogramValue(RealVector point, EventSimulation sim) {
		return 0;
	}
	
	protected Histogram getHist() {
		return new Histogram(100, 0, 2, "Hist");
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception {
		Value val = new Value(calculateRegression(hist));
		val.setX(start+currentLoop*step);
		val.print();
		averageDistances.add(val);
		hist = getHist();
	}

	@Override
	public GraphValues configureGraph() {
		return new GraphValues(averageDistances, name, "Average Distance from True Point", "m", "mm") ;
	}
		
	@Override
	public double[] calculateRegression(Histogram hist) {
		Regression reg = new Regression(hist.getX(),hist.getContent(),hist.getError());
		double[] result = new double[2];		
		try {
			reg.gaussian();
			result[0] = reg.getBestEstimates()[0];
			result[1] = reg.getBestEstimatesErrors()[0];
//			if(reg.getDegFree() < 20) {
//				result[0] = 0;
//				result[1] = 0;
// 			}
		} catch(Exception e) {
			result[0] = 0;
			result[1] = 0;
		}
		return result;
	}
	
	@Override
	public void postSimulationLoop() {
		
	}
	
	//Abstract methods
	public abstract void eventManipulation(EventSimulation sim, int n);

}
