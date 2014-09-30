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

import java.util.ArrayList;

import cc.mallet.fst.*;
import cc.mallet.types.*;

/**
 *
 * Interface for transducerCorrectors, which correct a subset of the
 * {@link Segment}s produced by a {@link Transducer}. It's primary
 * purpose is to find the {@link Segment}s that the {@link Transducer}
 * is least confident in and correct those using the true {@link
 * Labeling} (<code>correctLeastConfidenceSegments</code>).
 */
public interface TransducerCorrector 
{
		
	public ArrayList correctLeastConfidentSegments(InstanceList ilist, Object[] startTags,
                                                   Object[] continueTags);
}
