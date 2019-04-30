package simulations.abstracts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import flanagan.analysis.Regression;
import tracker.EventSimulation;
import utils.GraphValues;
import utils.Histogram;
import utils.Value;

public abstract class AveragePointSimulation extends SimpleSimulation {
	
	protected double start;
	protected double step;
	protected Histogram hist;
	protected List<Value> averageDistances;
	
	
	public AveragePointSimulation(String name, int eventId, int repeatMeasurements, int n, double start, double step, double smear) throws Exception {
		super(name, eventId, repeatMeasurements, n);
		this.name = name;
		this.start = start;
		this.step = step;
		this.smear = smear;
		averageDistances = new ArrayList<Value>();
		hist = getHist();
	}
	
	public AveragePointSimulation(String name, int eventId, int repeatMeasurements, int n, double start, double step) throws Exception {
		this(name, eventId, repeatMeasurements, n, start, step, start);
	}
	
	protected Histogram getHist() {
		return new Histogram(100, 0, 2, "Hist");
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception {
		//hist.writeToDisk("DetectorThicknessSimple"+currentLoop+".csv");
		//System.out.println("Current Loop: "+currentLoop);
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
	public void CurrentLoop(int n) throws Exception {
		//System.out.println("n: "+n+" hist: "+hist.getNfilled());
		EventSimulation sim = new EventSimulation(eventId);
		eventManipulation(sim,n);
		sim.startSimulation();
		
		RealVector point = findNearestPoint(sim,smear);
		double dist = point.mapMultiply(1000).subtract(sim.getTruePosition()).getNorm();
		hist.fill(dist);
	}
	
	public abstract void eventManipulation(EventSimulation sim, int n);
	
	@Override
	public double[] calculateRegression(Histogram hist) {
		Regression reg = new Regression(hist.getX(),hist.getContent(),hist.getError());
		double[] result = new double[2];		
		try {
			reg.gaussian();
			result[0] = reg.getBestEstimates()[0];
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
	
	@Override
	public void postSimulationLoop() {
		
	}


}
