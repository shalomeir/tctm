/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

/** Interface for a measure of distance between two <CODE>ConstantVector</CODE>s
    @author Jerod Weinman <A HREF="mailto:weinman@cs.umass.edu">weinman@cs.umass.edu</A>
*/

package cc.mallet.types;

import cc.mallet.types.SparseVector;

/**
	 Stores a hash for each object being compared for efficient
	 computation.
*/

public interface CachedMetric extends Metric {

	public double distance(SparseVector a, int hashCodeA,
                           SparseVector b, int hashCodeB);

}

