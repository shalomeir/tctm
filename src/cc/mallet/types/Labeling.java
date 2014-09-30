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

import cc.mallet.types.Label;

/** A distribution over possible labels for an instance. */

public interface Labeling extends AlphabetCarrying
{
	public LabelAlphabet getLabelAlphabet();
	
	public Label getBestLabel();
	public double getBestValue();
	public int getBestIndex();

	public double value(Label label);
	public double value(int labelIndex);

	// Zero-based
	public int getRank(Label label);
	public int getRank(int labelIndex);
	public Label getLabelAtRank(int rank);
	public double getValueAtRank(int rank);

	public void addTo(double[] values);
	public void addTo(double[] values, double scale);

	// The number of non-zero-weight Labels in this Labeling, not total
	// number in the Alphabet
	public int numLocations();
	// xxx Use "get..."? 
	public int indexAtLocation(int pos);
	public Label labelAtLocation(int pos);
	public double valueAtLocation(int pos);

	public LabelVector toLabelVector();
	
}
