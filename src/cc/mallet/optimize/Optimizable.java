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

package cc.mallet.optimize;

import java.util.Collection;


public interface Optimizable
{
	public int getNumParameters();

	public void getParameters(double[] buffer);
	public double getParameter(int index);

	public void setParameters(double[] params);
	public void setParameter(int index, double value);


	public interface ByValue extends Optimizable
	{
		public double getValue();
	}

	public interface ByGradient extends Optimizable
	{
		public void getValueGradient(double[] buffer);
	}

	public interface ByGradientValue extends Optimizable
	{
		public void getValueGradient(double[] buffer);
		public double getValue();
	}

	public interface ByHessian extends ByGradientValue
	{
		public void getValueHessian(double[][] buffer);
	}

	public interface ByVotedPerceptron extends Optimizable
	{
		public int getNumInstances();
		public void getValueGradientForInstance(int instanceIndex, double[] bufffer);
	}

	public interface ByGISUpdate extends Optimizable
	{
		public double getValue();
		public void getGISUpdate(double[] buffer);
	}

	public interface ByBatchGradient extends Optimizable {
		public void getBatchValueGradient(double[] buffer, int batchIndex, int[] batchAssignments);
		public double getBatchValue(int batchIndex, int[] batchAssignments);
	}

	// gsc: for computing gradient from batches in multiple threads
	public interface ByCombiningBatchGradient extends Optimizable {
		public void getBatchValueGradient(double[] buffer, int batchIndex, int[] batchAssignments);
		public double getBatchValue(int batchIndex, int[] batchAssignments);
		public void combineGradients(Collection<double[]> batchGradients, double[] buffer);
		public int getNumBatches();
	}

}
