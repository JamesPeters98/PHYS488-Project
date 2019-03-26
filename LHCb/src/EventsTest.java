import java.util.ArrayList;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import graphs.Graph;
import tracker.EventSimulation;
import tracker.Particle;


public class EventsTest {

	public static void main(String[] args) throws Exception {
		EventsReader events = new EventsReader();
		
//		Histogram hist = new Histogram(50, 0, 20, "Decay Length");
//		
//		for(Event event : events.events) {
//			hist.fill(event.decayLength);
//		}
//		
//		hist.writeToDisk("decay_lengths.csv");
		
		Particle[] particles = new Particle[1];
		particles[0] = new Particle(13, new double[]{0,0,0,0}, new double[]{20,20,400}, 10000);
				
		Event event = events.events.get(1);
		EventSimulation sim = new EventSimulation(event.getParticles());
		
		ArrayList<StraightLineFactory> factories = new ArrayList<StraightLineFactory>(); 
		
		for(ArrayList<RealVector> v : sim.detections) {
			if(!v.isEmpty()) { 
				StraightLineFactory line = new StraightLineFactory(v);
				if(line.isValid()) {
					factories.add(line);
				}
			}
		}
		
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
		
		RealVector[][] simLines = new RealVector[sim.Particles_sim.length][];
		for(int i = 0; i < sim.Particles_sim.length; i++) {
			Particle p = sim.Particles_sim[i];
			simLines[i] = new RealVector[p.getSteps()];
			for(int j = 0; j < p.getSteps(); j++) {
				double[] state = p.getState(j);
				simLines[i][j] = new ArrayRealVector(new double[] {state[1],state[2],state[3]});
				
			}
		}
		
		FindNearestPoint p = new FindNearestPoint(a, d, 3);
		
		System.out.println("Sim pos: "+p.getPoint().mapMultiply(1000)+" mm");
		System.out.println("True pos: "+event.getPositionVector()+" mm");
		new Graph("Detections", lines, a, d);
		new Graph("Track Path", simLines, null,null);
		
		
		
	}

}
