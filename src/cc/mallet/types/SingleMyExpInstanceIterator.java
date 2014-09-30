/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 5. 15 오후 7:27
 * Created Date : $today.year.month.day
 * Last Modified : 14. 5. 15 오후 7:27
 * User email: shalomeir@gmail.com
 */

package cc.mallet.types;

import java.util.Iterator;


public class SingleMyExpInstanceIterator implements Iterator<MyExpInstance> {

    MyExpInstance nextInstance;
	boolean doesHaveNext;

	public SingleMyExpInstanceIterator(MyExpInstance inst) {
		nextInstance = inst;
		doesHaveNext = true;
	}

	public boolean hasNext() {
		return doesHaveNext;
	}

	public MyExpInstance next() {
		doesHaveNext = false;
		return nextInstance;
	}
	
	public void remove () { throw new IllegalStateException ("This iterator does not support remove().");	}

}
