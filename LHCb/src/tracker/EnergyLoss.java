package tracker;

public class EnergyLoss {
	
	private final double K = 0.307075; //MeV cm^2
	private final double me = 0.511; //MeV - Mass of electron.
	
	private double p, Z, A; //Material Density, Atomic number, Atomic Mass.
	
	
	//Calculates the energy loss for a given absorber for different charged particles.
	public EnergyLoss(double materialDensity, double Z, double A) {
		this.p = materialDensity;
		this.Z = Z;
		this.A = A;
	}
	
	//Mean Exciation Energy
	private double I(double Z) {
		return 0.0000135*Z;
	}
	
	//Average energy loss per distance of an electrically charged particle (MeV/m)
	public double getEnergyLoss(Particle particle) {
		if(A != 0) {
			double B = particle.getBeta();
			double g = particle.getGamma();		
			double wMax = WMax(particle);
			double I = I(Z);
			int z = particle.getCharge();
			
			double outer = K*z*z*p*(Z/A)*1/(B*B);
			double inner = 0.5*Math.log(2*me*B*B*g*g*wMax/(I*I))-(B*B);
			
			//Convert from MeV/cm to MeV/m;
			return 100*outer*inner;
		} else {
			return 0;
		}
	}
	
	//Energy transfer in single collision.
	private double WMax(Particle particle) {
		double B = particle.getBeta();
		double g = particle.getGamma();
		double M = particle.getMass();
		
		return (2*me*B*B*g*g)/(1+(2*g*me/M)+(me/M)*(me/M));
	}
	
	

}
