import java.util.Arrays;

public class Event {
	
	protected double x,y,z; //True x,y,z;
	
	protected double decayTime, decayLength; //True decay time and position. time(ps) length(mm).
	
	protected double[][] momentums;
	
	/**
	 * @param particles - number of particles in system.
	 */
	public Event(int particles) {
		momentums = new double[particles][];
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
	 * @param px - x momentum of the particle.
	 * @param py - y momentum of the particle.
	 * @param pz - z momentum of the particle.
	 */
	public void setParticleMomentum(int n, int id, double px, double py, double pz) {
		momentums[n] = new double[] {id,px,py,pz};
	}
	
	public void print() {
		System.out.println("Pos| x:"+x+" y:"+y+" z:"+z);
		System.out.println("DecayTime| "+decayTime);
		System.out.println("DecayLength| "+decayLength);
		for(double[] mom : momentums) {
			System.out.println(Arrays.toString(mom));
		}
	}
	
	

}
