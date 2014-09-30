/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.cluster.clustering_scorer;

import cc.mallet.cluster.Clustering;

/**
 * Assign a score to a Clustering. Higher is better.
 * @author culotta
 *
 */
public interface ClusteringScorer {

	public double score(Clustering clustering);
}
