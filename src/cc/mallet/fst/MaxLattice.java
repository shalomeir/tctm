/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.fst;

import java.util.List;

import cc.mallet.types.Sequence;

import cc.mallet.fst.Transducer.State;

/** The interface to classes implementing the Viterbi algorithm, 
 * finding the best sequence of states for a given input sequence. */
public interface MaxLattice {
	public double getDelta(int inputPosition, int stateIndex);
	public Sequence<Object> bestOutputSequence();
	public List<Sequence<Object>> bestOutputSequences(int n);
	public Sequence<State> bestStateSequence();
	public List<Sequence<State>> bestStateSequences(int n);
	public Transducer getTransducer();
	public double elementwiseAccuracy(Sequence referenceOutput);
}
