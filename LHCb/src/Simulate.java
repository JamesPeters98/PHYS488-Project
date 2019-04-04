import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import flanagan.plot.PlotGraph;
import simulations.DecayTimeSim;

public class Simulate {
	
	static HashMap<Double,Double> decayLengths = new HashMap<Double, Double>();
	static HashMap<Double,Double> decayLengthErrors = new HashMap<Double, Double>();
	static boolean allSimsComplete = false;
	
	static double trueDecayLength;
	static double trueDecayLengthError;

	public static void main(String[] args) throws Exception {		
		double s1 = 0.01;
		double pow = 0.05;
		int n = 125;
		
		//DecayLengthSim sim = new DecayLengthSim();
		DecayTimeSim sim = new DecayTimeSim();
		while(!sim.allTasksComplete());
			
		double[] trueR = sim.calculateRegression(0);
		trueDecayLength = trueR[0];
		trueDecayLengthError = trueR[1];
		System.out.println("True Decay Length:"+trueDecayLength);
		
		for(int i=0; i<n; i++) {
			double smear = s1*Math.pow(10, -i*pow);
			double[] result = sim.calculateRegression(smear);
			decayLengths.put(smear, result[0]);
			decayLengthErrors.put(smear, result[1]);
		}
				
		double[] xVals = new double[decayLengths.size()];
		double[] data = new double[decayLengths.size()];
		double[] errorBars = new double[decayLengthErrors.size()];
		
		int count = 0;
		for(Entry<Double, Double> entry : decayLengths.entrySet()){
			xVals[count] =  -Math.log10(entry.getKey());
		    data[count] = entry.getValue();
		    errorBars[count] = decayLengthErrors.get(entry.getKey());
		    count++;
		}
		
		PlotGraph plot = new PlotGr
		PlotGraph plot = new PlotGraph(xVals,data);	
		plot.setErrorBars(0, errorBars);
		plot.plot();
		
//		Regression reg = new Regression(xVals, data);
//		reg.setTitle("Fit of Decay Length vs Natural Log of Smear Resolution");
//		reg.setXlegend("ln(x)");
//		reg.setYlegend("Decay Length (mm)");
//		reg.fiveParameterLogisticPlot(0,trueDecayLength);
		
		sim.service.shutdown();
		try {
			sim.service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		}
	}

}
