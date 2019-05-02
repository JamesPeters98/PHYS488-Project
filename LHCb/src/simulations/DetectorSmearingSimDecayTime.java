package simulations;

import java.util.HashMap;

import org.apache.commons.math3.linear.RealVector;

import exceptions.EventsReaderException;
import simulations.abstracts.Simulation;
import tracker.Event;
import tracker.EventSimulation;
import utils.AverageValue;
import utils.EventsReader;
import utils.GraphValues;

public class DetectorSmearingSimDecayTime extends Simulation {
	
	private double startResolution, powerStep, smearNum;
	
	private AverageValue trueDecayTimes; //List of trueDecayTimes from each Simulation - to be averaged at end of sim.
	private HashMap<Double,AverageValue> decayTimes; //Key - smear, Value - Decay Time value and Error.
	
	public static void main(String args[]) throws Exception {
		double startSmear = 0.0001;		//Starting smear - lowest resolution.
		double stepSmear = 0.05;		//Smear step, smear decreased logarithmically by 10^-stepSmear.
		int numberOfSmears = 100;		//Number of different smears to calculate from startSmear.
		int accuracy = 10;				//Number of times to run the simulation, each time calculating a more accurate result and error.
		
		
		DetectorSmearingSimDecayTime sim = new DetectorSmearingSimDecayTime(startSmear, stepSmear, numberOfSmears, accuracy);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("SmearingSimDecayTime.csv");
		sim.shutdown();
	}

	public DetectorSmearingSimDecayTime(double startResolution, double powerStep, int n, int accuracy) throws Exception {
		super("Smearing affects on Decay Time",accuracy,50,0,12);
		this.smearNum = n;
		this.startResolution = startResolution;
		this.powerStep = powerStep;
		decayTimes = new HashMap<Double, AverageValue>();
		trueDecayTimes = new AverageValue();
	}

	@Override
	public EventSimulation EventLoop(int eventId, int currentLoop) throws EventsReaderException {
		//Create simulation for all particles in event.
		EventSimulation sim = new EventSimulation(eventId);
		sim.startSimulation();
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
			if(decayTimes.containsKey(logr)) {
				AverageValue val = decayTimes.get(logr);
				val.add(reg);
			} else {
				AverageValue val = new AverageValue(logr,reg);
				decayTimes.put(logr, val);
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
		Event event = null;
		try {
			event = EventsReader.getEvent(sim.eventId);
		} catch (EventsReaderException e) {
			e.printStackTrace();
		}
		double time = Math.pow(10, 12)*(length)/(c*event.getInitialParticle().getGamma());
		return time;
	}
	

}
