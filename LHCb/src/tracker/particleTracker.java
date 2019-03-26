package tracker;

import java.util.Arrays;

public class particleTracker
{
    // Contains Euler+Runge-Kutta4 integrator methods

    private double dt;
    private int steps;
    private Particle input;
    private Particle output;

    //Constants
    final double c = 3e8; // speed of light in m/s
    
    public particleTracker(Particle particle, double Tmax, int N)
    {
        input = particle;
        steps = N;
        dt = Tmax/N;    // deltaT to arrive at final time
    }

    public Particle track(Geometry Experiment)
    {
        output = new Particle(input.getType(), input.getLastPosition(), input.getLastMomentum(), steps);

        int lastVolume = Experiment.getVolume(output);

		// the main particle propagation loop
        for(int n=0; n<steps; n++){

        	// take one step with RK4 (or Euler) method
            //propagateParticle(dt);      // use this for Euler integrator
        	propagateParticleRK4(dt);     // use this for Runge-Kutta 4th order integrator

	    	// check, if the last step crossed into a new experimental volume
    	    double reduceStep = Experiment.scanVolumeChange(output, lastVolume);
		    if (reduceStep < 1.0) {
			// yes, we crossed a boundary: go back and move forward with a reduced step size
		    	output.undoLastStep();
		    	propagateParticleRK4(dt*reduceStep);
		    }
	    
    	    // implement Multiple scattering
    	    Experiment.doMultScatter(output);
		
    	    // implement Energy Loss, abort if we're out of energy!
    	    Experiment.doEloss(output);
    	    
    	    if (output.getLastMomentumMag() <= 0.) {
    	    	System.out.println("Particle out of energy, done.");
    	    	break;
    	    }

		    lastVolume = Experiment.getVolume(output);
		    
		    // huge output: need to disable before running large numbers of steps+events
//		    System.out.println("Step " + n);
//		    output.print();
//		    System.out.println("In volume " + lastVolume);

		    if (!Experiment.isInVolume(output, 0)) {
				//System.out.println("Particle left the world, done.");
				//output.print();
				break;
		    }
        }
        return output;
    }
    
    public double[] Bfield(double x, double y, double z) {
    	// define B-field in Tesla at position x, y, z
        double [] myBfield = new double[3];

		// a constant uniform field
		myBfield[0] = 0.;
		myBfield[1] = 0.;
		myBfield[2] = 0.;

        return myBfield;
    }
    
    public double[] Efield(double x, double y, double z) {
    	// define E-field in V/m at position x, y, z
        double [] myEfield = new double[3];

		// no E field
		myEfield[0] = 0.;
		myEfield[1] = 0.;
		myEfield[2] = 0.;

        return myEfield;
    }
    
    public void propagateParticle(double mydt)
    {
        double[] Yi = output.getLastState(); //previous state of particle (t,x,y,z,px,py,pz)
        double q = input.getCharge();
	
	// get the E and B fields at coordinates x, y, z
	double[] myEfield = Efield(Yi[1], Yi[2], Yi[3]);
	double[] myBfield = Bfield(Yi[1], Yi[2], Yi[3]);
	
	double Etot = input.getLastE();
	
	// these are the three componens of the relativistic speed v=c*p/E
	double[] v1 = {c*Yi[4]/Etot,
		       c*Yi[5]/Etot,
		       c*Yi[6]/Etot};

	// this is the vectorial Lorentz force: q*E + q*(v x B)
	double[] l1 = {q*c*1e-6*(myEfield[0]+v1[1]*myBfield[2]-v1[2]*myBfield[1]),
		       q*c*1e-6*(myEfield[1]+v1[2]*myBfield[0]-v1[0]*myBfield[2]),
		       q*c*1e-6*(myEfield[2]+v1[0]*myBfield[1]-v1[1]*myBfield[0])};

	// make an Euler step and store result to Yf
        double[] Yf = new double[7];
	// advance time by mydt
	Yf[0] = Yi[0]+mydt;
	for(int i=1; i<4; i++){
	    // move in space by mydt*v
	    Yf[i] = Yi[i] + mydt*v1[i-1];
	    // update the momentum according to Lorentz force
	    Yf[i+3] = Yi[i+3] + mydt*l1[i-1];
        }
	// store the new position and momentum
        output.updateParticle(Yf);    
    }                 

    public void propagateParticleRK4(double mydt)
    {
	double[] Yi = output.getLastState(); //previous state of particle (t,x,y,z,px,py,pz)
	//if(Yi[0] == 0) System.out.println("Particle : "+Arrays.toString(Yi));
        double q = input.getCharge();
        double[] a = {0,0.5,0.5,1};
        double[] b = {1,2,2,1};
        double[] Yf = new double[7];
        double[] k1 = new double[7];
        double[] l1 = new double[7];
        
	for(int i = 0; i < 4; i++) {
	    double[] Yii = new double[7];

	    Yii[0] = Yi[0] + a[i]*mydt;
	    for(int j=1; j<4; j++){
		// move in space by mydt*v
		Yii[j] = Yi[j] + a[i]*mydt*k1[j-1];
		// update the momentum according to Lorentz force
		Yii[j+3] = Yi[j+3] + a[i]*mydt*l1[j-1];
            }
        
	    // get the E and B fields at coordinates x, y, z
	    double[] myEfield = Efield(Yii[1], Yii[2], Yii[3]);
	    double[] myBfield = Bfield(Yii[1], Yii[2], Yii[3]);

	    //calculate total energy of particle
	    double Etot = input.getLastE();
	    
	    // these are the three componens of the relativistic speed v=c*p/E
	    k1[0] = c*Yii[4]/Etot;
	    k1[1] = c*Yii[5]/Etot;
	    k1[2] = c*Yii[6]/Etot;
	    l1[0] = q*c*1e-6*(myEfield[0]+k1[1]*myBfield[2]-k1[2]*myBfield[1]);
	    l1[1] = q*c*1e-6*(myEfield[1]+k1[2]*myBfield[0]-k1[0]*myBfield[2]);
	    l1[2] = q*c*1e-6*(myEfield[2]+k1[0]*myBfield[1]-k1[1]*myBfield[0]);

	    for(int j=1; j<4; j++){
		// move in space by mydt*v
		Yf[j] = Yf[j] + b[i]*mydt*k1[j-1]/6;
		// update the momentum according to Lorentz force
		Yf[j+3] = Yf[j+3] + b[i]*mydt*l1[j-1]/6;
	    }
	}
	
	//add change of state to initial state
	Yf[0]=Yi[0]+mydt;
	for(int i=1; i<7; i++){
	    Yf[i] = Yi[i] + Yf[i];
	}
	// store the new position and momentum
	output.updateParticle(Yf);              
    }
    
}