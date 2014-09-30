/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.optimize;

/**
 * Callback interface that allows optimizer clients to perform some operation after every iteration.
 * 
 * Created: Sep 28, 2005
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: OptimizerEvaluator.java,v 1.1 2007/10/22 21:37:39 mccallum Exp $
 */
public interface OptimizerEvaluator {

  public interface ByGradient {
    /**
     * Performs some operation at the end of each iteration of a maximizer.
     *
     * @param maxable Function that's being optimized.
     * @param iter    Number of just-finished iteration.
     * @return true if optimization should continue.
     */
    boolean evaluate(Optimizable.ByGradientValue maxable, int iter);
  }

  public interface ByBatchGradient {
    /**
     * Performs some operation at the end of every batch.
     *
     * @param maxable Function that's being optimized.
     * @param iter    Number of just-finished iteration.
     * @param sampleId    Number of just-finished sample.
     * @param numSamples    Number of samples total.
     * @param sampleAssns    Assignments of instances to samples
     * @return true if optimization should continue.
     */
    boolean evaluate(Optimizable.ByBatchGradient maxable, int iter, int sampleId, int numSamples, int[] sampleAssns);

  }
  
}
