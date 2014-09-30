/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ProbEvaluation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
				double[] p1 = {0.11, 0.52, 0.33, 0.35};
				double[] p2 = {0.21, 0.44, 0.32, 0.03};
				double[] jd = {0.32, 0.21, 0.14, 0.33};
				
				int rank = 4; //몇개 까지 볼건지. precision, recall 에 영향
				
				double precision = getPrecision(p1,jd,rank);
				double recall = getRecall(p1,jd,rank);
				double f1 = getF1score(p1,jd,rank);
				double ndcg = getNdcg(p1,jd,rank);

				System.out.println("Precision = "+precision+" , Recall = "+recall+" , F1 Score= "+f1);
				System.out.println("ndcg at "+rank+" = "+ndcg);

	}

	public static double getNdcg(double[] p1, double[] jd, int rank) {
		// TODO Auto-generated method stub
		double ndcg=0.0;
		double idcg=0.0;

		int correct=0;
		double[] testDist = p1.clone();
		double[] solutionDist = jd.clone();
		int solutionChoice = SortingArray.getMaxIndex(jd);
		
		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>(new DataComparator());
//		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>();
		for(int i=0;i<testDist.length;i++){
			testDistMap.put(testDist[i],i);
		}
		TreeMap<Double, Integer> solutionDistMap = new TreeMap<Double, Integer>(new DataComparator());
		for(int i=0;i<testDist.length;i++){
			solutionDistMap.put(solutionDist[i],i);
		}
		
		
		Iterator<Entry<Double, Integer>> testDistMapIt = testDistMap.entrySet().iterator();
		Iterator<Entry<Double, Integer>> solutionDistMapIt = solutionDistMap.entrySet().iterator();

		ndcg+=solutionDist[testDistMapIt.next().getValue()];
		idcg+=solutionDist[solutionDistMapIt.next().getValue()];
		int r = 1;
		while(testDistMapIt.hasNext() && solutionDistMapIt.hasNext()&&r<rank){
			ndcg+=solutionDist[testDistMapIt.next().getValue()]/logB(r+1,2.0);
			idcg+=solutionDist[solutionDistMapIt.next().getValue()]/logB(r+1,2.0);
			r++;
		}
		return ndcg/idcg;
	}

	public static double logB(double x, double base) {
		return Math.log(x) / Math.log(base);
	}
	
	public static double getF1score(double[] p1, double[] jd, int rank) {
		// TODO Auto-generated method stub
		double precision=0.0;
		double recall=0.0;
		double f1=0.0;

		int correct=0;
		double[] testDist = p1.clone();
		double[] solutionDist = jd.clone();
		int solutionChoice = SortingArray.getMaxIndex(jd);
		
		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>(new DataComparator());
//		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>();
		for(int i=0;i<testDist.length;i++){
			testDistMap.put(testDist[i],i);
		}
		
		Iterator<Entry<Double, Integer>> testDistMapIt = testDistMap.entrySet().iterator();
		
		int r=0;
		while(testDistMapIt.hasNext()&&r<rank){
			if(testDistMapIt.next().getValue()==solutionChoice){
				correct+=1;
				recall=1.0;
			}
			r++;
		}
		
		precision = ((double)correct)/((double)rank);
		
		if(precision==0||recall==0) { 
			f1=0.0;
		}else{
			f1=2*precision*recall/(precision+recall);
		}
				
		return f1;
	}


	public static double getRecall(double[] p1, double[] jd, int rank) {
		// TODO Auto-generated method stub
		double recall=0.0;
		int correct=0;
		double[] testDist = p1.clone();
		double[] solutionDist = jd.clone();
		int solutionChoice = SortingArray.getMaxIndex(jd);
		
		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>(new DataComparator());
//		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>();
		for(int i=0;i<testDist.length;i++){
			testDistMap.put(testDist[i],i);
		}
		
		Iterator<Entry<Double, Integer>> testDistMapIt = testDistMap.entrySet().iterator();
		
		int r=0;
		while(testDistMapIt.hasNext()&&r<rank){
			if(testDistMapIt.next().getValue()==solutionChoice){
				recall=1.0;
			}
			r++;
		}
		return recall;
	}

	public static double getPrecision(double[] p1, double[] jd, int rank) {
		// TODO Auto-generated method stub
		double precision=0.0;
		int correct=0;
		double[] testDist = p1.clone();
		double[] solutionDist = jd.clone();
		int solutionChoice = SortingArray.getMaxIndex(jd);
		
		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>(new DataComparator());
//		TreeMap<Double, Integer> testDistMap = new TreeMap<Double, Integer>();

		for(int i=0;i<testDist.length;i++){
			testDistMap.put(testDist[i],i);
		}
		
		Iterator<Entry<Double, Integer>> testDistMapIt = testDistMap.entrySet().iterator();
		
		int r=0;
		while(testDistMapIt.hasNext()&&r<rank){
			if(testDistMapIt.next().getValue()==solutionChoice){
				correct+=1;
			}
			r++;
		}
		precision = ((double)correct)/((double)rank);		
		return precision;
	}
	
}

class DataComparator implements java.util.Comparator<Double> {
    public int compare(Double o1, Double o2) {
        return o2.compareTo(o1);
//        return o1.compareTo(o2);
    }
}


