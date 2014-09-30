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

public interface ConstantMatrix
{
	public int getNumDimensions();
	public int getDimensions(int[] sizes);
	public double value(int[] indices);

	// Access using a single index, efficient for dense matrices, but not sparse
	// Move to DenseMatrix?
	public int singleIndex(int[] indices);
	public void singleToIndices(int i, int[] indices);
	public double singleValue(int i);
	public int singleSize();

	// Access by index into sparse array, efficient for sparse and dense matrices
	public int numLocations();
	public int location(int index);
	public double valueAtLocation(int location);
	// Returns a "singleIndex"
	public int indexAtLocation(int location);
	
	public double dotProduct(ConstantMatrix m);
	public double absNorm();
	public double oneNorm();
	public double twoNorm();
	public double infinityNorm();

	public void print();
	public boolean isNaN();

	public ConstantMatrix cloneMatrix();

}
