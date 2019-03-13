import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;

public class Tests {

	public static void main(String[] args) throws Exception {
				
		SimulateSmearing smear = new SimulateSmearing(new ArrayRealVector(new double[] {0,0,0}), new ArrayRealVector(new double[] {1,2,1}), 0.5, 20);
		StraightLineFactory factory1 = new StraightLineFactory(smear.getVectors());
		
	}

}
