/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.extract;

/**
 * Interface for functions that compares extracted values of a field to see
 *  if they match.  These are used by the evaluation metrics (e.g.,
 *  @link{PerDocumentF1Evaluator}) to see if the extraction is correct.
 *
 * Created: Nov 23, 2004
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: FieldComparator.java,v 1.1 2007/10/22 21:37:44 mccallum Exp $
 */
public interface FieldComparator {

  /**
   * Returns true if the given two slot fillers match.
   */
  public boolean matches(String fieldVal1, String fieldVal2);

}
