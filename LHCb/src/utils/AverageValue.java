package utils;

import java.util.ArrayList;
import java.util.List;

public class AverageValue extends Value {
	
	List<Double> yList;
	List<Double> errorList;

	public AverageValue() {
		super(0,0);
		yList = new ArrayList<Double>();
		errorList = new ArrayList<Double>();
	}
	
	public AverageValue(double x, double[] y) {
		this();
		this.x = x;
		add(y);
	}
	
	public void add(double y, double error) {
		this.yList.add(y);
		this.errorList.add(error);
		calculate();
	}
	
	public void add(double[] y) {
		add(y[0],y[1]);
	}
	
	public void calculate() {
		double avgY = 0;
		double avgEr = 0;
		for(int i = 0; i < yList.size(); i++) {
			avgY += yList.get(i);
			avgEr += Math.pow(errorList.get(i),2);
		}
		
		this.y = avgY/yList.size();
		this.errorY = Math.sqrt(avgEr)/errorList.size();
	}
	

}
