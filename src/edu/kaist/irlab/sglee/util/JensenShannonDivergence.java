package edu.kaist.irlab.sglee.util;

public class JensenShannonDivergence {

	public static final double log2 = Math.log(2);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] p1 = {0.11, 0.52, 0.33, 0.04};
		double[] p2 = {0.21, 0.44, 0.32, 0.03};
		
		double jss = getJsDivergence(p1,p2);
		double kls = getKlDivergence(p1,p2);
		double kls2 = getKlDivergence(p2,p1);

		System.out.println("js = "+jss+" , kl = "+kls+" , kl2 = "+kls2);
		
	}
	
	public static double getJsDivergence(double[] prob1, double[] prob2){
		double[] meanProb = new double[prob1.length];
		for(int i=0;i<prob1.length;i++){
			meanProb[i]=(prob1[i]+prob2[i])/2;
		}
		double jsDiv = getKlDivergence(prob1, meanProb)/2 + getKlDivergence(prob2, meanProb)/2;
		return jsDiv;				
	}
	
	public static double getKlDivergence(double[] prob1, double[] prob2){
		double klDiv = 0.0;

		for (int i = 0; i < prob1.length; ++i) {
			if (prob1[i] == 0) { continue; }
	        if (prob2[i] == 0.0) { continue; } // Limin

	        klDiv += prob1[i] * Math.log( prob1[i] / prob2[i] );
		}

		return klDiv / log2; // moved this division out of the loop -DM
				
	}

}
