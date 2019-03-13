import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.RealVector;

public class Utils {
	
	public static Vector3D toVector3D(RealVector v) {
		return new Vector3D(v.toArray());
	}

}
