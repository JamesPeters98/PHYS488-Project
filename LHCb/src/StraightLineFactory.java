import java.util.Arrays;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class StraightLineFactory {
	
	RealVector mean;
	int dimensionSize = 0;
	EigenDecomposition ed;
	
	//Find straight from set of 3D points (Use ArrayRealVector).
	public StraightLineFactory(RealVector[] vectors) throws Exception {
		
		//Take first vectors dimensions as the dimension size for the set.
		if(dimensionSize == 0) dimensionSize = vectors[0].getDimension();
		
		//Calculate the mean of the group of vectors.
		mean = new ArrayRealVector(dimensionSize);
		for(RealVector v : vectors) {
			if(v.getDimension() != dimensionSize) throw new Exception("Dimension of vector not :"+dimensionSize+" for vector "+v);
			mean = mean.add(v);
		}
		mean.mapDivideToSelf(vectors.length);
		
		//Compute the covariance of the vectors as a Matrix
		RealMatrix mat = null;
		
		for (int i = 0 ; i < vectors.length ; ++i )
		{	
			RealMatrix m = vectors[i].outerProduct(vectors[i]);
			if(mat == null) mat = m;
			mat = mat.add(m);
		}
		
		//Calculate Eigenvectors and Eigenvalues.
		ed = new EigenDecomposition(mat);
		
		System.out.println(getDirectionVector());
		System.out.println(getOriginVector());
		if(!isUnique()) System.out.println("WARNING: Line is not unique!");
		
	}
	
	public RealVector getDirectionVector() {
		return ed.getEigenvector(0);
	}
	
	public RealVector getOriginVector() {
		return mean;
	}
	
	
	
	//The fitted line (hyperplane) is unique when the smallest eigenvalue has multiplicity of 1.
	public boolean isUnique() {
		return ed.getRealEigenvalue(dimensionSize-1) < ed.getRealEigenvalue(dimensionSize-2);
	}

}
