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
 * Interface for functions that are used to clean up field values after
 *  extraction has been performed.
 *
 * Created: Nov 25, 2004
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: FieldCleaner.java,v 1.1 2007/10/22 21:37:44 mccallum Exp $
 */
public interface FieldCleaner {

  /**
   * Returns a post-processed version of a field.
   * @param rawFieldValue
   * @return A processed string
   */
  String cleanFieldValue(String rawFieldValue);

}
