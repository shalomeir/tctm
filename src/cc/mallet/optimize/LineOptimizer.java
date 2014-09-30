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

	 Maximize a function projected along a line.
 */

package cc.mallet.optimize;

/** Optimize, constrained to move parameters along the direction of a specified line.
 * The Optimizable object would be either Optimizable.ByValue or Optimizable.ByGradient. */
public interface LineOptimizer
{
	
	/** Returns the last step size used. */
	public double optimize(double[] line, double initialStep);

	public interface ByGradient	{
		/** Returns the last step size used. */
		public double optimize(double[] line, double initialStep);
	}
	
}
