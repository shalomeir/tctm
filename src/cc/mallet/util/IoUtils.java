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

import java.io.*;

public class IoUtils
{

	public static CharSequence contentsAsCharSequence (Reader reader) throws IOException
	{
		final int BUFSIZE = 2048;
		char[] buf = new char[BUFSIZE];
		int count;
		StringBuffer sb = new StringBuffer (BUFSIZE);
		do {
			count = reader.read (buf, 0, BUFSIZE);
			if (count == -1)
				break;
			//System.out.println ("count="+count);
			sb.append (buf, 0, count);
		} while (count == BUFSIZE);
		return sb;
	}
	
	public static String contentsAsString (File f) throws IOException
	{
		assert (f != null);
		return contentsAsCharSequence (new BufferedReader (new FileReader (f))).toString();
	}

}
