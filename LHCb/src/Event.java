
public class Event {
	
	protected double x,y,z; //True x,y,z;
	
	protected double decayTime, decayPosition; //True decay time and position.
	
	protected double[][] momentums;
	
	/**
	 * @param particles - number of particles in system.
	 */
	public Event(int particles) {
		momentums = new double[particles][];
	}
	
	public void setTruePos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	

}
