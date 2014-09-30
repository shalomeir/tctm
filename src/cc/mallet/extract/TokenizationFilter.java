/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */
package cc.mallet.extract;

import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.Sequence;

/**
 * Created: Nov 12, 2004
 *
 * @author <A HREF="mailto:casutton@cs.umass.edu>casutton@cs.umass.edu</A>
 * @version $Id: TokenizationFilter.java,v 1.1 2007/10/22 21:37:44 mccallum Exp $
 */
public interface TokenizationFilter {

  /**
   * Converts a the sequence of labels into a set of labeled spans.  Essentially, this converts the
   *  output of sequence labeling into an extraction output.
   * @param dict
   * @param document
   * @param backgroundTag
   * @param input
   * @param seq
   * @return
   */
  LabeledSpans constructLabeledSpans(LabelAlphabet dict, Object document, Label backgroundTag,
                                     Tokenization input, Sequence seq);
}
