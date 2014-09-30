/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

/** 
   @author Fernando Pereira <a href="mailto:pereira@cis.upenn.edu">pereira@cis.upenn.edu</a>
 */

package cc.mallet.pipe.iterator;

import java.util.Iterator;

import cc.mallet.pipe.*;
import cc.mallet.types.Instance;

/**
 * Provides a {@link cc.mallet.pipe.iterator.PipeExtendedIterator} that applies a {@link Pipe} to
 * the {@link Instance}s returned by a given {@link cc.mallet.pipe.iterator.PipeExtendedIterator},
 * It is intended to encapsulate preprocessing that should not belong to the
 * input {@link Pipe} of a {@link Classifier} or {@link Transducer}.
 *
 * @author <a href="mailto:pereira@cis.upenn.edu">Fernando Pereira</a>
 * @version 1.0
 */
@Deprecated // Now that Pipe's support iteration directly, this should no longer be necessary? -AKM 9/2007
public class PipeExtendedIterator implements Iterator<Instance>
{
	private Iterator<Instance> iterator;
  private Pipe pipe;

	/**
   * Creates a new <code>PipeExtendedIterator</code> instance.
   *
   * @param iterator the base <code>PipeExtendedIterator</code>
   * @param pipe The <code>Pipe</code> to postprocess the iterator output
   */
  public PipeExtendedIterator (Iterator<Instance> iterator, Pipe pipe)
	{
		this.iterator = iterator;
    this.pipe = pipe;
	}

	//public PipeExtendedIterator(ArrayDataAndTargetIterator iterator2, CharSequenceArray2TokenSequence sequence) {
		// TODO Auto-generated constructor stub
	//}

	public boolean hasNext ()
	{
		return iterator.hasNext();
	}

	public Instance next ()
	{
    return pipe.pipe(iterator.next());
	}

	public void remove () {
		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}


}



