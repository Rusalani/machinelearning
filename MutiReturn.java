
public class MutiReturn {

	double[][] weight;
	int[] mistakes;
	int length;
	int[] accuracy;
	
	public MutiReturn(double[][] d, int[] a, int l) {
		weight = d;
		mistakes = a;
		length = l;
		accuracy = new int[mistakes.length];
		for (int i = 0; i < accuracy.length; i++) {
			double test = 1-((mistakes[i] * 1.0) / length);
			accuracy[i] = (int) ( test* 100);
		}
		
	}
	public int[] getAcc() {
		return accuracy;
	}

	

	public int[] getMistakes() {
		return mistakes;
	}

	public int getLength() {
		return length;
	}
}
