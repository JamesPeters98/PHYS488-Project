package simulations;

import simulations.abstracts.AveragePointSimulation;
import tracker.EventSimulation;
import utils.Value;

public class DetectorSmearingSimAveragePoint extends AveragePointSimulation {

	public DetectorSmearingSimAveragePoint(int eventId, int repeatMeasurements, int n, double start, double step)
			throws Exception {
		super("Smearing", eventId, repeatMeasurements, n, start, step);
	}

	public static void main(String[] args) throws Exception {
		int eventID = 114; 						//Selected Event to simulate
		int numberOfRepeatSims = 1000; 			//Number of times to simulate each event (Higher provides more accuracy)
		int numberOfSteps = 100; 				//Number of steps to take (Total simulations ran = numberOfRepeatsSims*numberOfSteps
		double startSmear = 0.0001; 			//Starting Detector Thickness
		double step = 0.05; 						//Step to increase the Detector thickness by each loop.
		
		DetectorSmearingSimAveragePoint sim = new DetectorSmearingSimAveragePoint(eventID, numberOfRepeatSims, numberOfSteps, startSmear, step);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("SmearingSimAveragePoint.csv");
		sim.shutdown();

	}

	@Override
	public void eventManipulation(EventSimulation sim, int n) {
		this.smear = start*Math.pow(10, -n*step);
		//System.out.println("Smear: "+smear+" n:"+n);
	}
	
	@Override
	public void postSimulation(int currentLoop) throws Exception {
		System.out.println("Smear: "+smear+" n:"+currentLoop);
		Value val = new Value(calculateRegression(hist));
		val.setX(smear);
		val.print();
		averageDistances.add(val);
		hist = getHist();
	}

}
