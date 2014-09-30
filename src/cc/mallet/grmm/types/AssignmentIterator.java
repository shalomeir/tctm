/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.grmm.types;

import java.util.Iterator;

/**
 * Iterates over the assignments to a set of variables.
 *  This is never instantiated by user code; instead, use
 *  one of the many assignmentIterator() methods.
 *
 *   DOCTODO: Add note about difference between using this class and iterating
 *    over assignments.
 *   DOCTODO: Explain why advance() is useful instead of next.
 *
 * Created: Sun Nov  9 21:04:03 2003
 *
 * @author <a href="mailto:casutton@cs.umass.edu">Charles Sutton</a>
 * @version $Id: AssignmentIterator.java,v 1.1 2007/10/22 21:37:44 mccallum Exp $
 */public interface AssignmentIterator extends Iterator {

  void advance();

  int indexOfCurrentAssn();

  Assignment assignment();
}
