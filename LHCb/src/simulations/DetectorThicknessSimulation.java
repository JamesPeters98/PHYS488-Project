package simulations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import tracker.Event;
import tracker.EventSimulation;
import utils.AverageValue;
import utils.GraphValues;
import utils.Value;

public class DetectorThicknessSimulation extends Simulation {
	
	private double startThickness, step, n;
	
	private List<Value> decayTimes; //Key - detectorThickness, Value - Decay Time value and Error.
	
	public static void main(String args[]) throws Exception {
		DetectorThicknessSimulation sim = new DetectorThicknessSimulation(0.0003, 0.0003, 1, 0);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("DetectorThicknessSim.csv");
		sim.shutdown();
	}

	public DetectorThicknessSimulation(double startThickness, double step, int n, double smear) throws Exception {
		super("Detector Thickness affects on Decay Time",n,100,0,200);
		this.n = n;
		this.startThickness = startThickness;
		this.step = step;
		decayTimes = new ArrayList<Value>();
	}

	@Override
	public EventSimulation EventLoop(Event event,int currentLoop) {
		//Create simulation for all particles in event.
		EventSimulation sim = new EventSimulation(event);
		sim.setDetectorThickness(startThickness+(currentLoop*step));
		return sim;
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception { 
		decayTimes.add(new Value(startThickness+(currentLoop*step),calculateRegression(0)));
	}

	@Override
	public GraphValues configureGraph() {
		return new GraphValues(decayTimes, "Log10(resolution)", "Decay Time", "Log10(m)", "ps"); 
	}

	@Override
	public void postSimulationLoop() {

	}

	@Override
	public double calculateHistogramValue(RealVector point, EventSimulation sim) {
		//System.out.println(point);
		//System.out.println(sim.getTruePosition());
		
		double dist = point.mapMultiply(1000).subtract(sim.getTruePosition()).getNorm();
		System.out.println(dist);
		return dist;
	}
	

}
