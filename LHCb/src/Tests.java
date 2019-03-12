import org.apache.commons.math3.linear.ArrayRealVector;

public class Tests {

	public static void main(String[] args) throws Exception {
				
		ArrayRealVector vec[] = { 
				new ArrayRealVector(new double[] {1,0,0}),
				new ArrayRealVector(new double[] {2,0,0}),
				new ArrayRealVector(new double[] {3,1,0})
				};
		
		StraightLineFactory factory1 = new StraightLineFactory(vec);
		//StraightLineFactory factory2 = new StraightLineFactory(vec2);
		
//		RealVector[] a = {
//				new ArrayRealVector(new double[] {1,1,0}),
//				new ArrayRealVector(new double[] {1,1,0}),
//				new ArrayRealVector(new double[] {1,1,0})
//				};
//		
//		RealVector[] d = {
//				new ArrayRealVector(new double[] {1,0,0}),
//				new ArrayRealVector(new double[] {0,1,0}),
//				new ArrayRealVector(new double[] {0,0,1})
//				};
//		
//		
//		FindNearestPoint find = new FindNearestPoint(a, d, 3);

	}

}
