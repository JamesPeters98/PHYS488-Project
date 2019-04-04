import java.util.ArrayList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import graphs.Graph;
import tracker.Event;
import tracker.EventSimulation;
import tracker.Particle;
import utils.EventsReader;
import utils.FindNearestPoint;
import utils.Histogram;
import utils.StraightLineFactory;


public class EventsTest {

	public static void main(String[] args) throws Exception {
		//Import CSV file and select event to graph.
		EventsReader events = new EventsReader();
		Event event = events.events.get(114);
		EventSimulation sim = new EventSimulation(event);
		
		//Fit straight lines to points - and check if sufficient data to fit one.
		ArrayList<StraightLineFactory> factories = new ArrayList<StraightLineFactory>(); 
		
		for(ArrayList<RealVector> v : sim.getDetections()) {
			if(!v.isEmpty()) { 
				StraightLineFactory line = new StraightLineFactory(v);
				if(line.isValid()) {
					factories.add(line);
				}
			}
		}
		
		//Setup up line data to graph.
		int n = factories.size();
		RealVector[] a = new RealVector[n];
		RealVector[] d = new RealVector[n];
		RealVector[][] lines = new RealVector[n][];	
		
		for(int i = 0; i < factories.size(); i++) {
			StraightLineFactory line = factories.get(i);
			lines[i] = line.rawVectors;
			a[i] = line.getOriginVector();
			d[i] = line.getDirectionVector();
		}
		
		//Setup simulated line data.
		RealVector[][] simLines = new RealVector[sim.Particles_sim.length][];
		for(int i = 0; i < sim.Particles_sim.length; i++) {
			Particle p = sim.Particles_sim[i];
			simLines[i] = new RealVector[p.getSteps()];
			for(int j = 0; j < p.getSteps(); j++) {
				double[] state = p.getState(j);
				simLines[i][j] = new ArrayRealVector(new double[] {state[1],state[2],state[3]});
				
			}
		}
		
		//Find the nearest point to all lines.
		FindNearestPoint p = new FindNearestPoint(a, d, 3);
		
		System.out.println("Sim pos: "+p.getPoint().mapMultiply(1000)+" mm");
		System.out.println("True pos: "+event.getPositionVector()+" mm");
		
		//Produce graphs
		new Graph("Detections", lines, a, d);
		new Graph("Track Path", simLines, null,null);
		
	}

}
