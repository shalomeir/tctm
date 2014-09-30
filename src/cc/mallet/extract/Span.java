/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */




/** 
   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

// Rename to Segment, (then also Segmentation, SegmentSequence, SegmentList)
// Alternatively, think about names: Annotation, AnnotationList, 

package cc.mallet.extract;

/** A sub-section of a document, either linear or two-dimensional.
 *   Spans are immutable. */

public interface Span
{

  /** Returns a textual representatio of the span, suitable for XML output, e.g. */
  String getText();

  /** Returns a new span that is the intersection of this span and another. */
  Span intersection(Span r);

  Object getDocument();

	boolean intersects(Span r);

  boolean isSubspan(Span r);

  /**
   *  Returns an integer index identifying the start of this span.
   *   Beware that in some cases (e.g., for images), this may not
   *   correspond directly to a sequence index.
   */
  int getStartIdx();


  /**
   *  Returns an integer index identifying the end of this span.
   *   Beware that in some cases (e.g., for images), this may not
   *   correspond directly to a sequence index.
   */
  int getEndIdx();

}
