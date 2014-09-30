/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

/** 
		@author Aron Culotta <a href="mailto:culotta@cs.umass.edu">culotta@cs.umass.edu</a>
*/

package cc.mallet.fst.confidence;

import java.util.*;

import cc.mallet.fst.*;
import cc.mallet.types.*;

/** Randomly assigns values between 0-1 to the confidence of a {@link
 * Segment}. Used as baseline to compare with other methods.
 */
public class RandomConfidenceEstimator extends TransducerConfidenceEstimator
{
	Random generator;
	
	public RandomConfidenceEstimator (int seed, Transducer model) {
		super(model);
		generator = new Random (seed);
	}

	public RandomConfidenceEstimator (Transducer model) {
		this (1, model);
	}
	
	/**
		 Randomly generate the confidence in the tagging of a {@link Segment}.
	 */
	public double estimateConfidenceFor (Segment segment, SumLatticeDefault cachedLattice) {
		return generator.nextDouble();
	}
}
