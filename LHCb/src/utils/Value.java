package utils;

public class Value {
	
	protected double x;
	protected double y;
	protected double errorY;
	
	public Value(double x,double y, double errorY) {
		this.x = x;
		this.y = y;
		this.errorY = errorY;
	}
	
	public Value(double y, double error) {
		this.errorY = error;
		this.y = y;
	}
	
	public Value(double[] y) {
		this.y = y[0];
		this.errorY = y[1];
	}
	
	public Value(double x, double[] y) {
		this(y);
		this.x = x;
	}
	
	public double X() {
		return x;
	}

	public double Y() {
		return y;
	}

	public double ErrorY() {
		return errorY;
	}
	
	public void print() {
		System.out.println(y+" +/- "+errorY);
	}
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setErrorY(double errorY) {
		this.errorY = errorY;
	}


}
