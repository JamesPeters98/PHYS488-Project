package simulations;

import simulations.abstracts.AveragePointSimulation;
import tracker.EventSimulation;
import tracker.Material;

public class DetectorMaterialSim extends AveragePointSimulation {
	
	Material[] mats; //Array of materials to test.

	public DetectorMaterialSim(int eventId, int repeatMeasurements, int n, double start, double step,
			double smear) throws Exception {
		super("Detector Material", eventId, repeatMeasurements, n, start, step, smear);
		
		mats = new Material[n];
	}

	@Override
	public void eventManipulation(EventSimulation sim, int n) {
		sim.setDetectorMaterial(mats[n]);
	}

	public static void main(String[] args) throws Exception {
		int eventID = 114; 						//Selected Event to simulate
		int numberOfRepeatSims = 2000; 			//Number of times to simulate each event (Higher provides more accuracy)
		int numberOfMaterials = 7; 			//Number of materials to test (Total simulations ran = numberOfRepeatsSims*numberOfMaterials
		double smear = 0;
		
		DetectorMaterialSim sim = new DetectorMaterialSim(eventID, numberOfRepeatSims, numberOfMaterials, 0, 1, smear);
		
		sim.mats[0] = new Material(2.33, 14, 28.085); 	//Silicon
		sim.mats[1] = new Material(19.3,79,196.9655);	//Gold
		sim.mats[2] = new Material(2.2,6,12.011);		//Carbon
		sim.mats[3] = new Material(5.3,32,72.59);		//Germanium
		sim.mats[4] = new Material(3.67,32,149.89);		//Sodium Iodide
		sim.mats[5] = new Material(7.8,26,55.85);		//Iron
		sim.mats[6] = new Material(11.34,82,207.2);		//Lead
		
		sim.start();
		sim.plotGraph();
		sim.saveRawDataToCSV("DetectorMaterialSim.csv");
		sim.shutdown();
	}

}
