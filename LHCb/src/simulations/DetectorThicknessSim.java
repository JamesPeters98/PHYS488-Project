package simulations;

import simulations.abstracts.AveragePointSimulation;
import tracker.EventSimulation;

public class DetectorThicknessSim extends AveragePointSimulation {

	public DetectorThicknessSim(int eventId, int repeatMeasurements, int n, double start, double step, double smear)
			throws Exception {
		super("Detector Thickness", eventId, repeatMeasurements, n, start, step,smear);
	}

	public static void main(String[] args) throws Exception {
		int eventID = 114; 						//Selected Event to simulate
		int numberOfRepeatSims = 1000; 			//Number of times to simulate each event (Higher provides more accuracy)
		int numberOfSteps = 20; 				//Number of steps to take (Total simulations ran = numberOfRepeatsSims*numberOfSteps
		double startThickness = 0.0001; 		//Starting Detector Thickness
		double step = 0.0005; 					//Step to increase the Detector thickness by each loop.
		double smear = 0;
		
		DetectorThicknessSim sim = new DetectorThicknessSim(eventID, numberOfRepeatSims, numberOfSteps, startThickness, step, smear);
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("DetectorThicknessSim.csv");
		sim.shutdown();

	}

	@Override
	public void eventManipulation(EventSimulation sim, int n) {
		sim.setDetectorThickness(start+n*step);		
	}

}
