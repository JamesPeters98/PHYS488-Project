package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphValues {
	
	List<Value> values;
	public double[] xVals, yVals, Errors;
	public String xAxis, yAxis, xUnit, yUnit;
	
	/**
	 * @param values - Key = x values, Entry = y value and error.
	 */
	public GraphValues(List<Value> values, String xAxis, String yAxis, String xUnit, String yUnit) {
		this.values = values;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.xUnit = xUnit;
		this.yUnit = yUnit;
		
		xVals = new double[values.size()];
		yVals = new double[values.size()];
		Errors = new double[values.size()];
		
		int n = 0;
		for(Value entry : values) {
			xVals[n] = entry.X();
			yVals[n] = entry.Y();
			Errors[n] = entry.ErrorY();
			n++;
		}
	}
	
	/**
	 * @param values - Key = x values, Entry = y value and error.
	 */
	public GraphValues(HashMap<Double, AverageValue> values, String xAxis, String yAxis, String xUnit, String yUnit) {
		this(new ArrayList<Value>(values.values()), xAxis, yAxis, xUnit, yUnit);
	}
}
