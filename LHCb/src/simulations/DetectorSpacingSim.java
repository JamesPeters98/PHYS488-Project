package simulations;

import simulations.abstracts.AveragePointSimulation;
import tracker.EventSimulation;

public class DetectorSpacingSim extends AveragePointSimulation {

	public DetectorSpacingSim(int eventId, int repeatMeasurements, int n, double start, double step, double smear)
			throws Exception {
		super("Detector Spacing", eventId, repeatMeasurements, n, start, step,smear);
	}

	public static void main(String[] args) throws Exception {
		int eventID = 10; 						//Selected Event to simulate
		int numberOfRepeatSims = 1500; 			//Number of times to simulate each event (Higher provides more accuracy)
		int numberOfSteps = 15; 				//Number of steps to take (Total simulations ran = numberOfRepeatsSims*numberOfSteps
		double startSpacing = 0.005; 			//Starting Detector Spacing
		double step = 0.0025; 					//Step to increase the Detector spacing by each loop.
		double smear = 0.00001;
		
		DetectorSpacingSim sim = new DetectorSpacingSim(eventID, numberOfRepeatSims, numberOfSteps, startSpacing, step, smear);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("DetectorThicknessSim.csv");
		sim.shutdown();

	}

	@Override
	public void eventManipulation(EventSimulation sim, int n) {
		sim.setSpacing(start+n*step);	
	}

}
