package utils;
import java.util.Arrays;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class FindNearestPoint {
	
	private int dim; //Dimension of system.
	private int n; //Number of lines.
	private RealVector[] a, d; //Arrays of: Position Vectors, Direction Vectors.
	
	private RealMatrix I;
	private RealMatrix R;
	private RealVector q;
	
	private RealVector solution;
	
	public FindNearestPoint(RealVector[] a, RealVector[] d, int dimensions) throws Exception {
		if(a.length != d.length) throw new Exception("Number of position vectors doesn't match number of direction vectors.");
		this.n = a.length;
		this.dim = dimensions;
		this.a = a;
		this.d = d;
		
		
		findNearestPoint();
	}
	
	private void findNearestPoint() {
		//Create the Matrix R, Identity Matrix I and Vector q
		I = MatrixUtils.createRealIdentityMatrix(dim);
		R = MatrixUtils.createRealMatrix(dim, dim);
		q = new ArrayRealVector(dim);
		
		//Performs summation of R and q according to algorithm
		for(int i = 0; i < n; i++) {
			R = R.add(I.subtract(d[i].outerProduct(d[i])));
			q = q.add(I.subtract(d[i].outerProduct(d[i])).operate(a[i]));
		}
		
		GaussianElimination g = new GaussianElimination(R.getData(),q.toArray());
		solution = g.getSolution();
	}
	
	/**
	 * @return RealVector of true position in metres.
	 */
	public RealVector getPoint() {
		return solution;
	}
	

}
