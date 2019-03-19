package tracker;

import java.io.*;

class runEloss
{
    // minimal test program to test EnergyLoss and MCS classes
    // uses Particle class to set up some simple test cases
	
	private static double materialDensity = 7.87; //g/cm^3
	private static double Z = 26; //Atomic Number.
	private static double A = 55.845; //Atomic Mass.

    public static void main (String [] args )
    {
        // COMPLETE HERE
        EnergyLoss ironEloss = new EnergyLoss(materialDensity,Z,A);
	
	    // set up a little loop to run a test with a few different momenta
		double [] momenta = {30, 300, 3000, 10000, 30000, 100000}; // MeV
		for (int i = 0; i < momenta.length; i++) {
		    double[] pos0 = {0., 0., 0., 0.};      // initial time and x,y,z position (m)
		    double[] mom0 = {momenta[i], 0., 0.};  // initial momentum px,py,pz (MeV)
		    Particle particle = new Particle("muon", pos0, mom0, 0);
	
		    // get dE/dx for this particle in iron and print
		    // CHECK HERE: make sure this works or adjust
		    double dEdx_Iron = ironEloss.getEnergyLoss(particle);
		    System.out.printf("dE/dx = %.0f MeV/m for p = %.1f MeV%n", 
				      dEdx_Iron, particle.getLastMomentumMag());
	}

	// multiple scattering for 1cm
	double thickness = 0.01; //m

        // set up MCS class with iron parameters
        // COMPLETE HERE
        MCS ironMCS = new MCS(materialDensity,Z,A);

	// CHECK HERE: make sure this works or adjust
	System.out.printf("Iron X0 %.5f m%n", ironMCS.getX0());

	// set up a little loop to run a test with a few different momenta
	double [] momenta2 = {500, 1000, 3000};
	for (int i = 0; i < momenta2.length; i++) {
	    // set four-momenta to correspond to muon(s) with a certain momentum
	    double[] pos = {0., 0., 0., 0.};       // initial time and x,y,z position (m)
	    double[] mom = {momenta2[i], 0., 0.};  // initial momentum px,py,pz (MeV)
	    Particle particle = new Particle("muon", pos, mom, 1);

	    // set next step time and x,y,z position (m) and momenta px,py,pz (MeV)
	    // this is to simulate a step of 1cm
	    double[] final_pos_mom = {0., thickness, 0., 0., momenta2[i], 0., 0.};
	    particle.updateParticle(final_pos_mom);

	    // interface takes two instances of muon to calculate distance between them
	    // CHECK HERE: make sure the lines below works or adjust
	    // NOTE: use particle.getLastDistance() to get the length of the last step,
	    //       here 0.01m
	    double theta0 = ironMCS.getTheta0(particle);

	    System.out.printf("theta0 = %.4f for p = %.1f MeV and %.3f m thickness%n",
			      theta0, particle.getLastMomentumMag(), particle.getLastDistance());
	}
	

    }
}
