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

package cc.mallet.types;

import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;

import cc.mallet.types.Alphabet;
import cc.mallet.types.Matrix;
import cc.mallet.util.PropertyList;

// Could also be called by convention "Matrix1"

@Deprecated  // Rarely used, and should be removed -akm 1/2008
public interface Vector extends ConstantMatrix
{
	public double value(int index);
	//public void setValue (int index, double value);
}
