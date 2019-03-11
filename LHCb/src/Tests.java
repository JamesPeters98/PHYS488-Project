import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Tests {

	public static void main(String[] args) throws Exception {
		
		Line line = new Line(new Vector3D(0,0,0),new Vector3D(1,1,1),0.01);
		
//		ArrayRealVector vec[] = { 
//				new ArrayRealVector(new double[] {1,1,0}),
//				new ArrayRealVector(new double[] {2,1,0}),
//				};
//		
//		ArrayRealVector vec2[] = { 
//				new ArrayRealVector(new double[] {0,0,0}),
//				new ArrayRealVector(new double[] {0,1,0}),
//				};
//		
//		
//		StraightLineFactory factory1 = new StraightLineFactory(vec);
//		StraightLineFactory factory2 = new StraightLineFactory(vec2);
		
		RealVector[] a = {
				new ArrayRealVector(new double[] {1,1,0}),
				new ArrayRealVector(new double[] {2,2,0}),
				new ArrayRealVector(new double[] {3,3,0})
				};
		
		RealVector[] d = {
				new ArrayRealVector(new double[] {1,0,0}),
				new ArrayRealVector(new double[] {0,1,0}),
				new ArrayRealVector(new double[] {0,0,1})
				};
		
		
		FindNearestPoint find = new FindNearestPoint(a, d, 3);

	}

}
