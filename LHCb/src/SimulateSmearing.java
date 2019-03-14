import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class SimulateSmearing {
	
	ArrayList<RealVector> vectors;
	
	public SimulateSmearing(RealVector a, RealVector d, double smear, int n) {
		Random rand = new Random();
		vectors = new ArrayList<RealVector>();
		for(int i = 0; i <= n; i++) {
			RealVector v = a.add(d.mapMultiply(i*0.25));
			RealVector spread = new ArrayRealVector(new double[] {smear*rand.nextGaussian(),smear*rand.nextGaussian(),smear*rand.nextGaussian()});
			vectors.add(v.add(spread));
		}
	}
	
	public RealVector[] getVectors() {
		RealVector[] v = new RealVector[vectors.size()];
		v = vectors.toArray(v);
		return v;
	}

}
