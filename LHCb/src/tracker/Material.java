package tracker;

public class Material {
	
	private double Z,A,density;

	public Material(double density, double Z, double A) {
		this.A = A;
		this.Z = Z;
		this.density = density;
	}

	public double getZ() {
		return Z;
	}

	public double getA() {
		return A;
	}

	public double getDensity() {
		return density;
	}

}
