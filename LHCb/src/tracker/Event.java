package tracker;
import java.util.Arrays;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Event {
	
	private int id;
	protected double x,y,z; //True x,y,z;
	protected RealVector positionVector;
	
	protected double decayTime, decayLength; //True decay time and position. time(ps) length(mm).
	
	protected double[][] momentums;
	
	private Particle[] particles;
	
	/**
	 * @param particles - number of particles in system.
	 */
	public Event(int id, int particles) {
		this.momentums = new double[particles][];
		this.particles = new Particle[particles-1];
		this.id = id;
	}
	
	
	public void setup() {
		//Convert to m!!
		double[] pos0 = new double[] {0,0.001*x,0.001*y,0.001*z};
		for(int i = 0; i < momentums.length-1; i++) {
			double[] mom = new double[] {momentums[i][1],momentums[i][2],momentums[i][3]};
			this.particles[i] = new Particle((int) momentums[i+1][0],pos0, mom,1000);
		}
	}
	
	/**
	 * @param x - true x position.
	 * @param y	- true y position.
	 * @param z	- true z position.
	 */
	public void setTruePos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		positionVector = new ArrayRealVector(new double[] {x,y,z});
	}
	
	
	/**
	 * @return position vector of x,y,z in mm.
	 */
	public RealVector getPositionVector() {
		return positionVector;
	}


	/**
	 * @param decayTime - decay time of the particle in ps.
	 */
	public void setDecayTime(double decayTime) {
		this.decayTime = decayTime;
	}
	
	/**
	 * @param decayLength - decay length of the particle in mm.
	 */
	public void setDecayLength(double decayLength) {
		this.decayLength = decayLength;
	}
	
	
	/**
	 * @param n - particle number
	 * @param id - PDG id of the particle
	 * @param px - x momentum of the particle. (GeV)
	 * @param py - y momentum of the particle. (GeV)
	 * @param pz - z momentum of the particle. (GeV)
	 */
	public void setParticleMomentum(int n, int id, double px, double py, double pz) {
		momentums[n] = new double[] {id,1000*px,1000*py,1000*pz};
	}
	
	public void print() {
		System.out.println("Pos| x:"+x+" y:"+y+" z:"+z);
		System.out.println("DecayTime| "+decayTime);
		System.out.println("DecayLength| "+decayLength);
		for(double[] mom : momentums) {
			System.out.println(Arrays.toString(mom));
		}
	}
	
	public Particle[] getParticles() {
		return particles;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}
	
	//Remove unneeded data when we are done with it.
	public void setForRemoval() {
		this.momentums = null;
		this.particles = null;
	}
	

}
