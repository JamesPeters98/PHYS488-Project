public class Tests {

	public static void main(String[] args) throws Exception {
		SimulateSmearing smear = new SimulateSmearing(Utils.getVector(0, 0, 0), Utils.getVector(1, 1, 1), 0.1, 19);
		StraightLineFactory line = new StraightLineFactory(smear.getVectors());
	}

}