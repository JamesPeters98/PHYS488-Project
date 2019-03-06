import flanagan.analysis.Regression;

public class Tests {
	
	public static void main(String[] args) {
		Regression reg = new Regression();
		
		double x[][] = {{1,1},{2,2},{3,3}};
		double y[][] = {{1,1},{2,2},{3,3}};
		double z[][] = {{1,1},{2,2},{3,3}};
		
		System.out.println(x.length);
		System.out.println(y.length);
		System.out.println(z.length);
		
		reg.enterData(x, y);
		reg.linearGeneral();
	}

}
