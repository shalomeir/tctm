package edu.kaist.irlab.sglee.util;

public class VectorsSimilarity {

	public static final double log2 = Math.log(2);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] p1 = {0.11, 0.52, 0.33, 0.04};
		double[] p2 = {0.21, 0.44, 0.32, 0.03};
		double[] sa1 = {0.6,0.4};
		double[] sa2 = {0.2,0.8};
		
		double cs = getCosineSimilarity(p1,p2);
		double ds = getDotProductsSimilarity(p1,p2);
		double acds = getAverageCDSimilarity(p1,p2);

		System.out.println("Cosine Similarity = "+cs+" , DotProducts Similarity = "+ds+" Average Similarity = "+acds);
		
	}

	public static double getAverageCDSimilarity(double[] prob1, double[] prob2){
		double acds = 0.0;
		
		acds = (getCosineSimilarity(prob1,prob2)+getDotProductsSimilarity(prob1,prob2))/2;

		return acds;				
	}
	
	
	public static double getDotProductsSimilarity(double[] prob1, double[] prob2){
		double ds = 0.0;
		
		for (int i = 0; i < prob1.length; ++i) {
			ds += prob1[i] * prob2[i];
		}

		return ds;				
	}

	public static double getCosineSimilarity(double[] prob1, double[] prob2){
		double cs = 0.0;
		double normalisedLength1 = 0.0;
		double normalisedLength2 = 0.0;
		
		for (int i = 0; i < prob1.length; ++i) {
			normalisedLength1 += Math.pow(prob1[i],2);
		}
		normalisedLength1 = Math.sqrt(normalisedLength1);
		for (int i = 0; i < prob2.length; ++i) {
			normalisedLength2 += Math.pow(prob2[i],2);
		}
		normalisedLength2 = Math.sqrt(normalisedLength2);

		cs=getDotProductsSimilarity(prob1,prob2)/(normalisedLength1*normalisedLength2);
		
		return cs;				
	}
}
