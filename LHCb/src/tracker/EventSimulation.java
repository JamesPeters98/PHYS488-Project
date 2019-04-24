package tracker;

import java.io.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import exceptions.EventsReaderException;
import utils.EventsReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class EventSimulation {   
    Random randGen;
    Scanner keyboard = new Scanner(System.in);

    // maximum allowed number of steps before simulation is aborted
    final int numSteps = 1000;
    final double simTime = 1e-9;
        
    private ArrayList<ArrayList<RealVector>> detections;
    private RealVector truePos;
    public Particle [] Particles_sim;
    
    //Parameters of the Sim
	private int detectors = 19;										//Number of detectors
	private double detectorThickness = 0.0003;						//Thickness of the detectors (m)
	private double spacing = 0.03;									//Spacing of the detectors (m)
	private Material detectorMat = new Material(2.33, 14, 28.085); 	//Material of the detector, default Silicon.
	
	private boolean simStarted = false;
	
	public int eventId;
    
    public double initialParticleBeta;
    
    public EventSimulation(Integer eventId) {
    	this.eventId = eventId;
    }
    
    public void startSimulation() throws EventsReaderException {
    	simStarted = true;
    	Event event = EventsReader.getEvent(eventId);
    	this.truePos = event.getPositionVector();
    	this.initialParticleBeta = event.getInitialParticle().getBeta();
    	Particle[] particles = event.getParticles();    	
    	detections = new ArrayList<ArrayList<RealVector>>();
    	randGen = new Random();
    	//randGen.setSeed(7894694);
    	
    	// Define the genotrical properties of the experiment
    	Geometry Experiment = SetupExperiment();
    	
    	Particles_sim = new Particle[particles.length];
 	    
 	    for (int ip = 0; ip < particles.length; ip++) {
	 		// create an instance of the particle tracker
	 		// usage: particleTracker(particle, time to track (s), number of time steps)
	 		particleTracker tracker = new particleTracker(particles[ip], simTime, numSteps);
	
	 		// some output (need to disable before running large numbers of events!)
	 		//System.out.println("Simulating particle " + ip);
	 		//particles[ip].print();
	
	 		Particles_sim[ip] = tracker.track(Experiment);
	 		tracker = null;
 	    }
 	    particles = null;
 	    
 	    //Particles_sim[0].DumpTXYZPxPyPz("outputs/sample.csv");
 	    
 	    // simulate detection of each particle in each element
	    Particle [] Particles_det = Experiment.detectParticles(Particles_sim);
	    
	    for(int j =0; j<Particles_det.length; j++) {
	    	Particle p = Particles_det[j];
	    	ArrayList<RealVector> detection = new ArrayList<RealVector>();
	    	for(int i = 1; i < 20; i++) {
	    		double[] pos = p.getPosition(i);
	    		if(pos[0] != 0) detection.add(new ArrayRealVector(new double[] {pos[1],pos[2],pos[3]}));
	    	}
	    	detections.add(detection);
	    }
    }
    
    public ArrayList<ArrayList<RealVector>> getSmearedDetections(double smear) throws Exception{
    	if(!simStarted) throw new Exception("Simulation hasn't been started!");
    	
    	//Create new List to hold the lists of detections.
    	ArrayList<ArrayList<RealVector>> newDetections = new ArrayList<ArrayList<RealVector>>();
    	
    	//Loop over all the true detections.
    	for(ArrayList<RealVector> detection : detections) {
    		
    		//Create List to hold the smeared detections.
    		ArrayList<RealVector> newDetection = new ArrayList<RealVector>();
    		
    		//Loop over every detection and create a new vector with smeared position vectors.
    		for(RealVector v : detection) {
    			RealVector vNew = new ArrayRealVector(new double[] {v.getEntry(0)+smear(smear),v.getEntry(1)+smear(smear),v.getEntry(2)+smear(smear)});
    			newDetection.add(vNew);
    		}
    		newDetections.add(newDetection);
    	}
    	return newDetections;
    }
    
    public ArrayList<ArrayList<RealVector>> getDetections() throws Exception{
    	if(!simStarted) throw new Exception("Simulation hasn't been started!");
    	return detections;
    }
    
    public RealVector getTruePosition() {
    	return truePos;
    }
    

    private double smear(double resolution) {
    	return randGen.nextGaussian()*resolution;
    }

    
    public Geometry SetupExperiment () {
		// example setup the experiment:
		// * first line defines the size of the experiment in vacuum
		// * then add one block of iron: 0.3x0.3 m^2 wide in x,y-direction, ironThickness cm thick in z-direction
		// * then add two thin (1mm) "silicon detectors" 10cm and 20cm after the iron block

		Geometry Experiment = new Geometry(randGen, 0.00001);
		
		//Cuboid around the detector as vacuum space.
		Experiment.AddCuboid(-0.024, -0.024, 0, 0.024, 0.024, 1, 0,0,0);
	
		//Adds a row of disk detectors based on the parameters given.
		for(int i = 0; i < detectors; i++) {
			Experiment.AddDisk(0, 0, i*(spacing), 0.042, 0.008, detectorThickness, detectorMat.getDensity(), detectorMat.getZ(), detectorMat.getA());
		}	
		return Experiment;
    }

	public void setDetectors(int detectors) {
		this.detectors = detectors;
	}

	public void setDetectorThickness(double detectorThickness) {
		this.detectorThickness = detectorThickness;
	}

	public void setSpacing(double spacing) {
		this.spacing = spacing;
	}
	
	public void setDetectorMaterial(Material mat) {
		this.detectorMat = mat;
	}
}