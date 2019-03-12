import java.util.Arrays;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class FindNearestPoint {
	
	private int dim; //Dimension of system.
	private int n; //Number of lines.
	private RealVector[] a, d; //Arrays of: Position Vectors, Direction Vectors.
	private RealVector p;
	
	private RealVector b;
	private RealMatrix M;
	
	public FindNearestPoint(RealVector[] a, RealVector[] d, int dimensions) throws Exception {
		if(a.length != d.length) throw new Exception("Number of position vectors doesn't match number of direction vectors.");
		this.n = a.length;
		this.dim = dimensions;
		this.a = a;
		this.d = d;
		
		p = new ArrayRealVector(dim);
		
		findNearestPoint();
		//GaussianElimination ge = new GaussianElimination(M.getData(), b.toArray(), dim);
	}
	
	private void findNearestPoint() {
		M = new BlockRealMatrix(dim, dim);
		b = new ArrayRealVector(dim);
		
//		for(int i = 0; i < n; i++) {
//			double d2 = d[i].dotProduct(d[i]);
//			double da = d[i].dotProduct(a[i]);
//			System.out.println("v: "+d[i]+" d2: "+d2+", da: "+da);
//			
//			for(int ii = 0; ii < dim; ii++) {
//				for(int jj = 0; jj < dim; jj++) {
//					double incM = d[i].getEntry(ii)*d[i].getEntry(jj);
//					System.out.println(ii+","+jj+": "+M);
//					M.addToEntry(ii, jj, incM);
//					System.out.println(ii+","+jj+": "+M);
//					M.addToEntry(ii, ii, -d2);
//					System.out.println(ii+","+jj+": "+M);
//					System.out.println("---------");
//					
//					
//					double incB = (d[i].getEntry(ii)*da) - (a[i].getEntry(ii)*d2);
//					b.addToEntry(ii, incB);
//				}
//			}
//		}
		
		double[][] M = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
		double[] b = {0, 0, 0};
	
		
		
		
		
		for(int i = 0; i < n; i++) {
			
//			double d2 = d[i].dotProduct(d[i]);
//			double da = d[i].dotProduct(a[i]);
//			System.out.println("v: "+d[i]+" d2: "+d2+", da: "+da);
						
			
//			double D = d1.dotProduct(a[i]);
//			
//			for(int ii = 0; ii < dim; ii++) {
//				b[ii] += dir[ii]*D;
//				for(int jj = 0; jj < dim; jj++) {
//					M[ii][jj] += dir[ii]*dir[jj];
//					//-M[ii][ii] -= d2;
//					//b[ii] += dir[ii]*D;
//				}
//			}
		}
		
		RealMatrix mat = new BlockRealMatrix(M);
		System.out.println(mat);
		
		GaussianElimination ge = new GaussianElimination(M,b,3);

	}
	

}
