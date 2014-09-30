/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.cluster.evaluate;

import cc.mallet.cluster.Clustering;

/**
 * A list of {@link cc.mallet.cluster.evaluate.ClusteringEvaluators}.
 *
 * @author "Aron Culotta" <culotta@degas.cs.umass.edu>
 * @version 1.0
 * @since 1.0
 */
public class ClusteringEvaluators extends ClusteringEvaluator {

	ClusteringEvaluator[] evaluators;
	
	public ClusteringEvaluators (ClusteringEvaluator[] evaluators) {
		this.evaluators = evaluators;
	}
	
	/**
	 *
	 * @param truth
	 * @param predicted
	 * @return A String summarizing the evaluation metric.
	 */
	public String evaluate (Clustering truth, Clustering predicted) {
		String results = "";
		for (int i = 0; i < evaluators.length; i++) {
			String name = evaluators[i].getClass().getName();
			results += name.substring(name.lastIndexOf('.') + 1) + ": " +
								 evaluators[i].evaluate(truth, predicted) + "\n";
		}
		return results;
	}

	/**
	 *
	 * @return If the ClusteringEvaluator maintains state between calls
	 * to evaluate, this method will return the total evaluation metric
	 * since the first evaluation.
	 */
	public String evaluateTotals () {
		String results = "";
		for (int i = 0; i < evaluators.length; i++) {
			String name = evaluators[i].getClass().getName();
			results += name.substring(name.lastIndexOf('.') + 1) + ": " +
								 evaluators[i].evaluateTotals() + "\n";
		}
		return results;

	}

	public int size () { return evaluators.length; }

	@Override
	public double[] getEvaluationScores(Clustering truth, Clustering predicted) {
		throw new UnsupportedOperationException("Not yet implemented");
	}	
}
