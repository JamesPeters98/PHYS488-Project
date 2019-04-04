package simulations;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.math3.linear.RealVector;

import tracker.Event;
import tracker.EventSimulation;
import utils.AverageValue;
import utils.EventsReader;
import utils.GraphValues;
import utils.Value;

public class SmearingSimulation extends Simulation {
	
	private double startResolution, powerStep, smearNum;
	
	private AverageValue trueDecayTimes; //List of trueDecayTimes from each Simulation - to be averaged at end of sim.
	private HashMap<Double,AverageValue> decayTimes; //Key - smear, Value - Decay Time value and Error.
	
	public static void main(String args[]) throws Exception {
		SmearingSimulation sim = new SmearingSimulation(0.005, 0.05, 100);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("SmearingSim.csv");
		sim.shutdown();
	}

	public SmearingSimulation(double startResolution, double powerStep, int n) throws Exception {
		super("Smearing affects on Decay Time",1,50,0,12);
		this.smearNum = n;
		this.startResolution = startResolution;
		this.powerStep = powerStep;
		decayTimes = new HashMap<Double, AverageValue>();
		trueDecayTimes = new AverageValue();
	}

	@Override
	public EventSimulation EventLoop(int eventId, int currentLoop) {
		//Create simulation for all particles in event.
		EventSimulation sim = new EventSimulation(eventId);
		return sim;
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception { 
		trueDecayTimes.add(calculateRegression(0)); //Calculate true Decay time.
				
		//Loop over every resolution and calculate a regression.
		for(int i=0; i<smearNum; i++) {
			double r = startResolution*Math.pow(10, -i*powerStep);
			double[] reg = calculateRegression(r);
			double logr = Math.log10(r);
			System.out.println(logr+": "+Arrays.toString(reg));
			if(decayTimes.containsKey(logr)) {
				AverageValue val = decayTimes.get(logr);
				val.add(reg);
			} else {
				decayTimes.put(logr, new AverageValue(logr,reg));
			}
		}		
	}

	@Override
	public GraphValues configureGraph() {
		return new GraphValues(decayTimes, "Log10(resolution)", "Decay Time", "Log10(m)", "ps"); 
	}

	@Override
	public void postSimulationLoop() {
		System.out.println("True Decay Time: "+trueDecayTimes.Y()+" +/- "+trueDecayTimes.ErrorY());
	}

	@Override
	public double calculateHistogramValue(RealVector point, EventSimulation sim) {
		//Add point to histogram.
		double length =	point.getNorm();
		double c = 299792458;
		Event event = EventsReader.getEvent(sim.eventId);
		double time = Math.pow(10, 12)*(length)/(c*event.getInitialParticle().getGamma());
		return time;
	}
	

}
