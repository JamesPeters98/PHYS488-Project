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

public class DetectorSpacingSimDecayTime extends Simulation {
	
	private double startSpacing, spacingStep;
	
	private AverageValue trueDecayTimes; //List of trueDecayTimes from each Simulation - to be averaged at end of sim.
	private HashMap<Double,AverageValue> decayTimes; //Key - smear, Value - Decay Time value and Error.
	
	public static void main(String args[]) throws Exception {
		int steps = 30;				
		double startSpacing = 0.001;
		double spacingStep = 0.001;
		
		
		DetectorSpacingSimDecayTime sim = new DetectorSpacingSimDecayTime(startSpacing, spacingStep, steps);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("DetectorSpacingSimDecayTime.csv");
		sim.shutdown();
	}

	public DetectorSpacingSimDecayTime(double startSpacing, double spacingStep, int steps) throws Exception {
		super("Smearing affects on Decay Time",steps,50,0,12);
		this.startSpacing = startSpacing;
		this.spacingStep = spacingStep;
		decayTimes = new HashMap<Double, AverageValue>();
		trueDecayTimes = new AverageValue();
	}

	@Override
	public EventSimulation EventLoop(int eventId, int currentLoop) throws EventsReaderException {
		//Create simulation for all particles in event.
		EventSimulation sim = new EventSimulation(eventId);
		sim.setSpacing(startSpacing+currentLoop*spacingStep);
		sim.startSimulation();
		return sim;
	}

	@Override
	public void postSimulation(int currentLoop) throws Exception { 
		trueDecayTimes.add(calculateRegression(0)); //Calculate true Decay time.
			
		double spacing = startSpacing+currentLoop*spacingStep;
		
		double[] reg = calculateRegression(smear);
		if(decayTimes.containsKey(spacing)) {
			AverageValue val = decayTimes.get(spacing);
			val.add(reg);
		} else {
			AverageValue val = new AverageValue(spacing,reg);
			decayTimes.put(spacing, val);
		}	
	}

	@Override
	public GraphValues configureGraph() {
		return new GraphValues(decayTimes, "Spacing", "Decay Time", "m", "ps"); 
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
