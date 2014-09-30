/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.grmm.types;

import cc.mallet.util.Randoms;

/**
 * $Id: DiscreteFactor.java,v 1.1 2007/10/22 21:37:44 mccallum Exp $
 */
public interface DiscreteFactor extends Factor {
  
  int sampleLocation(Randoms r);

  double value(int index);

  int numLocations();

  double valueAtLocation(int loc);

  int indexAtLocation(int loc);

  double[] toValueArray();

  int singleIndex(int[] smallDims);
}
