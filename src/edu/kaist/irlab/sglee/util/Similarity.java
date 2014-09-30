package edu.kaist.irlab.sglee.util;

public class Similarity {

	public static final double log2 = Math.log(2);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[] p1 = {0.11, 0.52, 0.33, 0.04};
		double[] p2 = {0.21, 0.44, 0.32, 0.03};
		double[] p3 = {0.4, 0.01, 0.5, 0.09};
		double[] orig = {0.21, 0.44, 0.32, 0.03};
		double[] sa1 = {0.6,0.4};
		double[] sa2 = {0.2,0.8};
		double[] vectorSimilarity;
		double[][] p = new double[3][p1.length];
		p[0]=p1;
		p[1]=p2;
		p[2]=p3;
				
		vectorSimilarity=getVectorsSimilarity(p,orig);
		
		System.out.println("p vector similarity = "+vectorSimilarity[0]+", "+vectorSimilarity[1]+", "+vectorSimilarity[2]);
		
	}
	
	public static double[] getVectorsSimilarity(double[][] p, double[] orig) {
		double[] vectorSim= new double [p.length];
		for(int i=0;i<p.length;i++){
			vectorSim[i]=VectorsSimilarity.getAverageCDSimilarity(p[i], orig);
		}		
		
		return vectorSim;
	}


}
