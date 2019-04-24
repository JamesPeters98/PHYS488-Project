# LHCb Simulations

This project is a java simulation of the Large Hadron Collider Beauty Experiment.

# Types of Simulations
  - Resolution of Detectors (Smearing)
  - Detector Thickness
  - Detector Spacing
  - Detector Material

# How to run a Simulation

Open the project and go to the 'simulations' package and run one of the following classes listed below, along with its variables that can be modified:

# SmearingSim.java

A simulation comparing a range of different resolution detectors.

```
double startSmear = 0.0001;	  //Starting smear - lowest resolution.
double stepSmear = 0.05;	    //Smear step, smear decreased logarithmically by 10^-stepSmear.
int numberOfSmears = 100;	    //Number of different smears to calculate from startSmear.
int accuracy = 20;		        //Number of times to run the simulation, each time calculating a more accurate result and error.
```

# DetectorThicknessSim.java

A simulation comparing detectors of different thickness.

```
int eventID = 114; 						//Selected Event to simulate
int numberOfRepeatSims = 1000; 			//Number of times to simulate each event (Higher provides more accuracy)
int numberOfSteps = 20; 				//Number of steps to take (Total simulations ran = numberOfRepeatsSims*numberOfSteps
double startThickness = 0.0001; 		//Starting Detector Thickness
double step = 0.00075; 					//Step to increase the Detector thickness by each loop.
```

# DetectorSpacingSim.java

A simulation comparing detectors of different spacings.

```
int eventID = 10; 						//Selected Event to simulate
int numberOfRepeatSims = 1500; 			//Number of times to simulate each event (Higher provides more accuracy)
int numberOfSteps = 15; 				//Number of steps to take (Total simulations ran = numberOfRepeatsSims*numberOfSteps
double startSpacing = 0.005; 			//Starting Detector Spacing
double step = 0.0025; 					//Step to increase the Detector spacing by each loop.
```

# DetectorMaterialSim.java

A simulation comparing detectors of different materials.

```
int eventID = 114; 						//Selected Event to simulate
int numberOfRepeatSims = 2000; 			//Number of times to simulate each event (Higher provides more accuracy)
int numberOfMaterials = 8; 				//Number of materials to test (Total simulations ran = numberOfRepeatsSims*numberOfMaterials
```
List of materials:
```
sim.mats[0] = new Material(2.33, 14, 28.085); 	//Silicon
sim.mats[1] = new Material(2.66, 14, 28.085); 	//Silicon
sim.mats[2] = new Material(19.3,79,196.9655);	//Gold
sim.mats[3] = new Material(2.2,6,12.011);		//Carbon
sim.mats[4] = new Material(5.3,32,72.59);		//Germanium
sim.mats[5] = new Material(3.67,32,149.89);		//Sodium Iodide
sim.mats[6] = new Material(7.8,26,55.85);		//Iron
sim.mats[7] = new Material(11.34,82,207.2);		//Lead
```
