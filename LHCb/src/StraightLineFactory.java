import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class StraightLineFactory {
	
	RealVector mean;
	int dimensionSize = 0;
	EigenDecomposition ed;
	public RealVector[] rawVectors;
	
	private boolean isValid = true; //Set to false if only one point is provided.
	
	//Find straight from set of 3D points (Use ArrayRealVector).
	public StraightLineFactory(RealVector[] vectors) throws Exception {
		
		this.rawVectors = vectors;
		
		//Take first vectors dimensions as the dimension size for the set.
		if(dimensionSize == 0) dimensionSize = vectors[0].getDimension();
		
		if(vectors.length <= 1) isValid = false;
		
		//Create X Matrix
		RealMatrix X = new BlockRealMatrix(vectors.length,3);
		for(int i = 0; i < vectors.length; i++) {
			X.setRowVector(i, vectors[i]);
		}
		
		//Calculate the mean of the group of vectors.
		mean = new ArrayRealVector(dimensionSize);
		for(RealVector v : vectors) {
			if(v.getDimension() != dimensionSize) throw new Exception("Dimension of vector not :"+dimensionSize+" for vector "+v);
			mean = mean.add(v);
		}
		mean.mapDivideToSelf(vectors.length);
		
		//Create PX Matrix
		RealMatrix PX = new BlockRealMatrix(vectors.length,3);
		for(int i = 0; i < vectors.length; i++) {
			PX.setRowVector(i, vectors[i].subtract(mean));
		}
		
		//Compute the covariance of the vectors as a Matrix
		RealMatrix XPX = X.transpose().multiply(PX);
		
//		for(double[] row : XPX.getData()) {
//			System.out.println(Arrays.toString(row));
//		}
		
		//Calculate Eigenvectors and Eigenvalues.
		ed = new EigenDecomposition(XPX);
		
		if(!isValid()) System.out.println("Invalid line!!!");
		System.out.println("Direction Vector | "+getDirectionVector());
		System.out.println("Origin Vector    | "+getOriginVector());	
		System.out.println("------------------------------------------");	

	}
	
	public StraightLineFactory(ArrayList<RealVector> vectors) throws Exception {
		this(vectors.toArray(new RealVector[vectors.size()]));
	}
	
	public RealVector getDirectionVector() {
		return ed.getEigenvector(0);
	}
	
	public RealVector getOriginVector() {
		return mean;
	}
	
	public boolean isValid() {
		return isValid;
	}
}
