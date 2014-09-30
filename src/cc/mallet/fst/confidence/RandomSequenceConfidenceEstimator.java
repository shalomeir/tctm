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

import java.util.logging.*;
import java.util.*;

import cc.mallet.fst.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.types.*;
import cc.mallet.util.MalletLogger;

/**
	 Estimates the confidence of an entire sequence randomly.
 */
public class RandomSequenceConfidenceEstimator extends TransducerSequenceConfidenceEstimator
{
	
	Random generator;

	public RandomSequenceConfidenceEstimator (int seed, Transducer model) {
		super(model);
		generator = new Random (seed);
	}

	public RandomSequenceConfidenceEstimator (Transducer model) {
		this (1, model);
	}

	/**
		 Calculates the confidence in the tagging of an {@link Instance}.
	 */
	public double estimateConfidenceFor (Instance instance,
																			 Object[] startTags,
																			 Object[] inTags) {
		return generator.nextDouble();
	}
}

