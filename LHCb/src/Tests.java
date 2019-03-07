import org.apache.commons.math3.linear.ArrayRealVector;

public class Tests {

	public static void main(String[] args) throws Exception {
		ArrayRealVector vec[] = { 
				new ArrayRealVector(new double[] {1,1,1}),
				new ArrayRealVector(new double[] {2,2,2}),
				new ArrayRealVector(new double[] {3,3,3})
				};
		
		StraightLineFactory factory = new StraightLineFactory(vec);
		
		System.out.println(factory.getDirectionVector());
		System.out.println(factory.getOriginVector());
		System.out.println("Unique: "+factory.isUnique());

	}

}
