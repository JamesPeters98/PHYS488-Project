package tracker;

import java.io.*;
import java.util.Scanner;
import java.util.Random;

class RunSimulation_MuonDistances
{
   
    static Random randGen = new Random();
    static Scanner keyboard = new Scanner(System.in);

    // maximum allowed number of steps before simulation is aborted
    static final int numSteps = 1000;
    

    public static void main (String [] args ) throws IOException {
	// set the seed for the random number generator so it always
	// produces the same sequence of random numbers
	randGen.setSeed(7894694);
	
	double startAngle = 0;
	double simTime = 1e-7;
	double ironThickness = 1;
	int numberOfEvents = 75000;
	
	double startMom = 400;
	double endMom = 450;
	double spacing = 10;
	
	int range = (int) ((endMom-startMom)/spacing) + 1;
	int[] det1 = new int[range];
	
	System.out.println("Simulating range of momentums from "+startMom+" MeV to "+endMom+" MeV in intervals of "+spacing+" MeV");
	System.out.println("Total Simulated Events = "+(range*numberOfEvents));


	// start of main loop: run the simulation numberOfEvents times
	for(double mom = startMom; mom <= endMom; mom+=spacing) {
		
		System.out.println("Simulating Momemtum: "+mom);
		
		// initial time and x,y,z position (m) of particles
		double[] pos0 = {0., 0., 0., 0.};
		
		// Define the genotrical properties of the experiment
		Geometry Experiment = SetupExperiment(ironThickness);
		
		for (int nev = 0; nev < numberOfEvents; nev++) {
	
		    if (nev % 1000 == 0) {
			//System.out.println("Simulating event " + nev);
		    }
	
		    // get the particles of the event to simulate
		    Particle [] Particles_gen = GetParticles(pos0, mom, startAngle);
		    
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
	
		    // after detection: uses initial position (assumed to be known with pos0) and detected positions
		    // the first detector has volume number 2 (see printout)
		    
		    
		    int momI = (int) ((int) (mom-startMom)/spacing);
		    //System.out.println("MomI: "+momI+", mom: "+mom);
		    
		    double [] det_pos = Particles_det[0].getPosition(2);
		    
		    double smear = 0.01;
		    double s = smear(0);

		    double det1Theta = Math.acos(((s+det_pos[3])-pos0[3])/
					 Math.sqrt(Math.pow((s+det_pos[1])-pos0[1], 2)
						   +Math.pow((s+det_pos[2])-pos0[2], 2)
						   +Math.pow((s+det_pos[3])-pos0[3], 2)));
	 	    
	 	   if((det1Theta < (Math.PI/4)) && (det1Theta > 0)) { det1[momI]++;}

		}
	}
	
	System.out.println("-------------");
	for(int i = 0; i < det1.length; i++) {
		int counts = det1[i];
		double mom = startMom + (i*spacing);
		System.out.println("Det1 - Mom("+mom+"MeV) "+counts+" Particles");
	}
	
	// end of main event loop

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
	
	//Experiment.Print();

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