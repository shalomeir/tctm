/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.grmm.inference;


import java.util.List;

import cc.mallet.grmm.types.Assignment;
import cc.mallet.grmm.types.FactorGraph;
import cc.mallet.util.Randoms;

/**
 * Interface for methods from sampling the distribution given by a graphical
 *  model.
 *
 * Created: Mar 28, 2005
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: Sampler.java,v 1.1 2007/10/22 21:37:49 mccallum Exp $
 */
public interface Sampler {

  /**
   * Samples from the distribution of a given undirected model.
   * @param mdl Model to sample from
   * @param N Number of samples to generate
   * @return A list of assignments to the model.
   */
  public Assignment sample(FactorGraph mdl, int N);

  /**
   * Sets the random seed used by this sampler.
   * @param r Random object to be used by this sampler.
   */
  public void setRandom(Randoms r);
  
}
