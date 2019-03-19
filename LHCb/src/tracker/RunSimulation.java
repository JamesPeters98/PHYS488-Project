package tracker;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;

class RunSimulation
{
    // Program to run a simulation of particles in a simple "experiment"

    // The program makes use of the class Particle (almost identical to the one used in week 3+4)
    // to store the components of the position fourvector (time, x, y, z)
    // and the four-momentum (technically stored as mass and px, py, pz)
    // this class incorporates a "history", that is filled e.g. by the "particle tracking"

    // The particle tracking is performed by the class particleTracker,
    // which is almost identical to the class used in week 3+4, however it is extended
    // to incorporate energy loss, multiple scattering
    // and a simple adaptive algorithm to ensure that single steps end approximately
    // at boundaries betwen different experimental features
    
    // The "experimental geometry" is defined in the class Geometry:
    //    * An experiment is a collection of "volumes", that are numbered with a unique
    //      identifier from 0 to the number of experimental features.
    //    * Currently all experiemntal features are cuboids of different sizes and materials,
    //      with the sides aligned to the coordinate system axes. The example is a block of iron
    //      (user-definable length) + two "planar detectors"
    //    * Internally to the "Geometry" class are two helper classes that implement the
    //      formulas for calculation of energy loss (class "Energy loss")
    //      and multiple scattering angles (class "MCS")
    //    * The main functionality is to check, if a certain particle is inside a certain volume
    //      and apply energy loss and multipel scattering during simulation
    //    * The class also provides a simple mechanism to detect changes in the volume as
    //      the particle propagates and suggest an adapted step length to keep one step within
    //      one volume (the granularity of this scan is adjusted as "featuresize")
    //    * At the end, the class is used to "detect" particle signals in certain volumes (detectors)
    //
    //
    // At the end of the simulation of each event, one may analyse the
    // results and fill histograms. Examples are provided to perform calculations using the:
    //    * Generated particles (Particles_gen)
    //    * Simulated particles (Particles_sim) - these include the effect of energy loss and
    //      multiple scattering
    //    * Detector response (Particles_det) - these provide measurement points that can be
    //      further used to reconstruct particles like in a real experiment, where
    //      Particles_gen and Particles_sim are unknown

    
    static Random randGen = new Random();
    static Scanner keyboard = new Scanner(System.in);

    // maximum allowed number of steps before simulation is aborted
    static final int numSteps = 1000;
    

    public static void main (String [] args ) throws IOException {
	// set the seed for the random number generator so it always
	// produces the same sequence of random numbers
	//randGen.setSeed(7894694);

	
//	System.out.println("Type in starting momentum in MeV [e.g. 1000]");
//	double startMomentum = keyboard.nextDouble();
//	System.out.println("Type in starting angle Theta in rad in x-z plane [e.g. 0]");
//	double startAngle = keyboard.nextDouble();
//	System.out.println("Type in total (maximum) simulation time s [e.g. 1E-9]");
//	double simTime = keyboard.nextDouble();
//	System.out.println("Type in the thickness of iron in m [e.g. 0.05]"); 
//	double ironThickness = keyboard.nextDouble();
//
//	System.out.println("Type in the number of events to simulate [e.g. 1000]");
//    int numberOfEvents = keyboard.nextInt();
	
	double startMomentum = 1000;
	double startAngle = 0;
	double simTime = 1e-7;
	double ironThickness = 1;
	double numberOfEvents = 50000;

	// Define the genotrical properties of the experiment
	Geometry Experiment = SetupExperiment(ironThickness);

	// initial time and x,y,z position (m) of particles
	double[] pos0 = {0., 0., 0., 0.};

	// setup histograms for analysis
	Histogram gen_startMom = new Histogram(50, 0., startMomentum*1.01, "initial generated Momentum");
	Histogram sim_endMom = new Histogram(50, 0., startMomentum*1.01, "simulated final Momentum");

	Histogram gen_startTheta = new Histogram(100, 0, Math.PI/4, "initial generated Theta");
	Histogram sim_endTheta = new Histogram(100, 0, Math.PI/4, "simulated final Theta");
	Histogram det_endTheta1 = new Histogram(100, 0, Math.PI/4, "measured final Theta Det1");
	Histogram det_endTheta2 = new Histogram(100, 0, Math.PI/4, "measured final Theta Det2");


	// start of main loop: run the simulation numberOfEvents times
	for (int nev = 0; nev < numberOfEvents; nev++) {

	    if (nev % 1000 == 0) {
		System.out.println("Simulating event " + nev);
	    }

	    // get the particles of the event to simulate
	    Particle [] Particles_gen = GetParticles(pos0, startMomentum, startAngle);
	    
	    // simulate propagation of each generated particle,
	    // store output in Particles_sim
	    Particle [] Particles_sim = new Particle[Particles_gen.length];
	    
	    for (int ip = 0; ip < Particles_gen.length; ip++) {
		// create an instance of the particle tracker
		// usage: particleTracker(particle, time to track (s), number of time steps)
		particleTracker tracker = new particleTracker(Particles_gen[ip], simTime, numSteps);

		// some output (need to disable before running large numbers of events!)
		//System.out.println("Simulating particle " + ip + " of event " + nev);
		//Particles_gen[ip].print();

		Particles_sim[ip] = tracker.track(Experiment);
	    }
	    // end of simulated particle propagation

	    
	    // write scatter plot for event 0, particle to disk into file "sample.csv"
	    if (nev == 0) {
	    	Particles_sim[0].DumpTXYZPxPyPz("outputs/sample.csv");
	    }
	    
	    // simulate detection of each particle in each element
	    Particle [] Particles_det = Experiment.detectParticles(Particles_sim);

	    // this is just for dumping the simulation to the screen
	    for (int ip = 0; ip < Particles_sim.length; ip++) {
	    	for (int idet = 1; idet < Experiment.getNshapes(); idet++) {
		    double [] txyz = Particles_det[ip].getPosition(idet);
	    	    //System.out.println("Particle " + ip + " detection in volume " + idet);
//	    	    System.out.println("[t,x,y,z] = [" + txyz[0] + ", " + txyz[1] + ", " +
//		    		       txyz[2] + ", " + txyz[3] + "]");
	    	}
	    }
	    // end of simulated particle detection


	    
	    // analyse event (= 1 particle) and fill histograms
	    // this is to show, how to access the variables
	    // and do some calculations with the results of the simulation

	    // retrieve initial generated particle momentum
	    double [] gen_mom = Particles_gen[0].getLastMomentum();
	    double gen_mommag = Math.sqrt(gen_mom[0]*gen_mom[0]+gen_mom[1]*gen_mom[1]
					  +gen_mom[2]*gen_mom[2]);

	    // retrieve simulated particle position and momentum at the end of the simulation
	    double [] sim_mom = Particles_sim[0].getLastMomentum();
	    double sim_mommag = Math.sqrt(sim_mom[0]*sim_mom[0]+sim_mom[1]*sim_mom[1]
					  +sim_mom[2]*sim_mom[2]);
	    double [] sim_pos = Particles_sim[0].getLastPosition();
	    
	    // fill histograms
	    gen_startMom.fill(gen_mommag);
	    sim_endMom.fill(sim_mommag);

	    // calculate theta angles, defined here as angle w.r.t. the z-plane
	    // theta ~ acos(Z/sqrt(X^2+Y^2+Z^2))

	    // generated - this should be equal to startAngle given at the start
	    double gen_Theta = Math.acos(gen_mom[2]/gen_mommag);
	    gen_startTheta.fill(gen_Theta);

	    // after simulation - muon will have scattered around a bit
	    double sim_Theta = Math.acos(sim_mom[2]/sim_mommag);
	    
	    sim_endTheta.fill(sim_Theta);

	    // after detection: uses initial position (assumed to be known with pos0) and detected positions
	    // the first detector has volume number 2 (see printout)
	    
	    double [] det_pos = Particles_det[0].getPosition(2);
	    
	    double s = smear(0.01);

	    double det1Theta = Math.acos(((s+det_pos[3])-pos0[3])/
				 Math.sqrt(Math.pow((s+det_pos[1])-pos0[1], 2)
					   +Math.pow((s+det_pos[2])-pos0[2], 2)
					   +Math.pow((s+det_pos[3])-pos0[3], 2)));
 	    
 	   if(!Double.isNaN(det1Theta)) { det_endTheta1.fill(det1Theta);}
 	   else System.out.println("NaN "+Arrays.toString(det_pos)+" : "+det1Theta+" : ");
 	   
 	   double s2 = smear(0.01);

	    // the second detector has volume number 3 (see printout)
	    det_pos = Particles_det[0].getPosition(3);
	    double det2Theta = Math.acos(((s2+det_pos[3])-pos0[3])/
				 Math.sqrt(Math.pow((s2+det_pos[1])-pos0[1], 2)
					   +Math.pow((s2+det_pos[2])-pos0[2], 2)
					   +Math.pow((s2+det_pos[3])-pos0[3], 2)));
 	    
  	   if(!Double.isNaN(det2Theta)) det_endTheta2.fill(det2Theta);
	    // end of analysis

	}
	// end of main event loop

	// write out histograms for plotting and futher analysis
	gen_startMom.writeToDisk("outputs/gen_startP.csv");
	sim_endMom.writeToDisk("outputs/sim_endP.csv");
	
	gen_startTheta.writeToDisk("outputs/gen_startTheta.csv");
	sim_endTheta.writeToDisk("outputs/sim_endTheta.csv");
	det_endTheta1.writeToDisk("outputs/det_endTheta1.csv");
	det_endTheta2.writeToDisk("outputs/det_endTheta2.csv");
    }
    
    public static double smear(double resolution) {
    	return randGen.nextGaussian()*resolution;
    }

    
    public static Geometry SetupExperiment (double ironThickness)
    {
	// example setup the experiment:
	// * first line defines the size of the experiment in vacuum
	// * then add one block of iron: 0.3x0.3 m^2 wide in x,y-direction, ironThickness cm thick in z-direction
	// * then add two thin (1mm) "silicon detectors" 10cm and 20cm after the iron block

	Geometry Experiment = new Geometry(randGen, 0.0005);
	Experiment.AddCuboid(-0.15, -0.15, 0.,
			     0.15, 0.15, ironThickness+0.25,
			     0., 0., 0.);
	Experiment.AddCuboid(-0.10, -0.10, 0.,
			     0.10, 0.10, ironThickness,
			     7.87, 26, 55.845);
	
	Experiment.AddCuboid(-0.15, -0.15, ironThickness+0.10,
			     0.15, 0.15, ironThickness+0.101,
			     2.33, 14, 28.085);
	
	Experiment.AddCuboid(-0.15, -0.15, ironThickness+0.20,
			     0.15, 0.15, ironThickness+0.201,
			     2.33, 14, 28.085);
	
	Experiment.Print();

	return Experiment;
    }


    public static Particle[] GetParticles(double[] pos0, double startMomentum, double startAngle)
    {
	// example to simulate just one muon starting at p0
	// with a total momentum startMomentum and theta=startAngle
	// we follow the particle physics "convention"
	// to have the z-axis in the (approximate) direction of the beam
	
	Particle [] Particles_gen = new Particle[1];
	// initial momentum px,py,pz (MeV)
	double phi = 0;
	double[] mom0 = {startMomentum*Math.sin(startAngle)*Math.cos(phi),
			 startMomentum*Math.sin(startAngle)*Math.sin(phi),
			 startMomentum*Math.cos(startAngle)};
	Particles_gen[0] = new Particle("muon", pos0, mom0, numSteps);

	return Particles_gen;
    }
}