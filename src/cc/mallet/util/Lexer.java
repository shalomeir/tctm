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

package cc.mallet.util;

import java.util.Iterator;

public interface Lexer extends Iterator
{
	public int getStartOffset();

	public int getEndOffset();

	public String getTokenString();


	// Iterator interface methods

	public boolean hasNext();

	// Returns token text as a String
	public Object next();

	public void remove();

}
