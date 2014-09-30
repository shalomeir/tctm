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
 * Abstract class that estimates the confidence of a {@link Sequence}
 * extracted by a {@link Transducer}.Note that this is different from
 * {@link cc.mallet.fst.confidence.TransducerConfidenceEstimator}, which estimates the
 * confidence for a single {@link Segment}.
 */
abstract public class TransducerSequenceConfidenceEstimator
{
	private static Logger logger = MalletLogger.getLogger(TransducerSequenceConfidenceEstimator.class.getName());

	protected Transducer model; // the trained Transducer which
															// performed the extractions.

	public TransducerSequenceConfidenceEstimator (Transducer model) {
		this.model = model;
	}
	
	/**
		 Calculates the confidence in the tagging of a {@link Sequence}.
	 */
	abstract public double estimateConfidenceFor (
		Instance instance, Object[] startTags, Object[] inTags);


	/**
		 Ranks all {@link Sequences}s in this {@link InstanceList} by
		 confidence estimate.
		 @param ilist list of segmentation instances
		 @param startTags represent the labels for the start states (B-)
		 of all segments
		 @param continueTags represent the labels for the continue state
		 (I-) of all segments
		 @return array of {@link cc.mallet.fst.confidence.InstanceWithConfidence}s ordered by
		 non-decreasing confidence scores, as calculated by
		 <code>estimateConfidenceFor</code>
	 */
	public InstanceWithConfidence[] rankInstancesByConfidence (InstanceList ilist,
																														 Object[] startTags,
																														 Object[] continueTags) {
		ArrayList confidenceList = new ArrayList ();
		for (int i=0; i < ilist.size(); i++) {
			Instance instance = ilist.get (i);
			Sequence predicted = new MaxLatticeDefault (model, (Sequence)instance.getData()).bestOutputSequence();
			double confidence = estimateConfidenceFor (instance, startTags, continueTags);
			confidenceList.add (new InstanceWithConfidence ( instance, confidence, predicted));
			logger.info ("instance#"+i+" confidence="+confidence);
		}
		Collections.sort (confidenceList);
		InstanceWithConfidence[] ret = new InstanceWithConfidence[1];
		ret = (InstanceWithConfidence[]) confidenceList.toArray (ret);
		return ret;
	}
}
