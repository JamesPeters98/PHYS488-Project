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

```
double startSmear = 0.0001;		//Starting smear - lowest resolution.
double stepSmear = 0.05;		//Smear step, smear decreased logarithmically by 10^-stepSmear.
int numberOfSmears = 100;		//Number of different smears to calculate from startSmear.
int accuracy = 20;				//Number of times to run the simulation, each time calculating a more accurate result and error.

```







 
