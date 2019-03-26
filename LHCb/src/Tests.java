import java.util.Random;

import org.apache.commons.math3.linear.RealVector;

import graphs.Graph;
import utils.FindNearestPoint;
import utils.SimulateSmearing;
import utils.StraightLineFactory;
import utils.Utils;

public class Tests {
	
	static RealVector[] a;
	static RealVector[] d;
	static RealVector[][] lines;

	public static void main(String[] args) throws Exception {
		int n = 4;
		a = new RealVector[n];
		d = new RealVector[n];
		lines = new RealVector[n][];
		
		Random rand = new Random();
		double x = (rand.nextDouble()-0.5);
		double y = (rand.nextDouble()-0.5);
		double z = (rand.nextDouble()-0.5);
		
		for(int i = 0; i < n; i++) {
			StraightLineFactory line = createLine(x,y,z, (rand.nextDouble()-.5),rand.nextDouble()-.5,rand.nextDouble()-.5,0.01);
			lines[i] = line.rawVectors;
			a[i] = line.getOriginVector();
			d[i] = line.getDirectionVector();
		}
		
		FindNearestPoint p = new FindNearestPoint(a, d, 3);
		
		new Graph("Test",lines,a,d);
	}
	
	public static StraightLineFactory createLine(double ax, double ay, double az,
			double dx, double dy, double dz, double smear) throws Exception {
		SimulateSmearing smearValues = new SimulateSmearing(Utils.getVector(ax,ay,az), Utils.getVector(dx,dy,dz), smear, 19);
		return new StraightLineFactory(smearValues.getVectors());
	}

}