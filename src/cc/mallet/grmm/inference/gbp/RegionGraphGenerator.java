/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.grmm.inference.gbp;

import cc.mallet.grmm.types.FactorGraph;

/**
 * Interface for strategies that construct region graphs from arbitrary graphical models.
 *  They choose both which factors should be grouped into a region, and what the connectivity
 *  between regions should be.
 *
 * Created: May 27, 2005
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: RegionGraphGenerator.java,v 1.1 2007/10/22 21:37:58 mccallum Exp $
 */
public interface RegionGraphGenerator {

  /**
   * Construct a region graph from an artbitrary model. 
   * @param mdl Undirected Model to construct region graph from.
   */
  RegionGraph constructRegionGraph(FactorGraph mdl);
}
