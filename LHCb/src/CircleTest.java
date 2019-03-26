
public class CircleTest {

	public static void main(String[] args) {
		double x = 0.1;
		double y = 0.1;
		double z = 0;
		
		double x0 = 0;
		double y0 = 0;
		double z0 = 0;
		
		double r1 = 1;
		double r2 = 0.1;
		double thickness = 0.0003;
		
		
	    if((Math.pow(x-x0,2)+Math.pow(y-y0,2)) <= Math.pow(r1, 2)) {
	    	//If we are in the x-y plane of the whole disk.
	    	if((Math.pow(x-x0,2)+Math.pow(y-y0,2)) >= Math.pow(r2, 2)) {
	    		//If we are outside of the inner hollow ring.
	    		if((z >= z0) && (z <= z0+thickness) ){
	    			//Check if in the thickness of detector in the z-plane.
	    		}
	    	}
	    	
	    }

	}

}
