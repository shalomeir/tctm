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

package cc.mallet.pipe.iterator;

import java.io.*;
import java.util.Iterator;
import java.util.regex.*;
import java.net.URI;
import java.net.URISyntaxException;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.*;

public class LineIterator implements Iterator<Instance>
{
	LineNumberReader reader;
	Pattern lineRegex;
	int uriGroup, targetGroup, dataGroup;
	String currentLine;
	
	public LineIterator (Reader input, Pattern lineRegex, int dataGroup, int targetGroup, int uriGroup)
	{
		this.reader = new LineNumberReader (input);
		this.lineRegex = lineRegex;
		this.targetGroup = targetGroup;
		this.dataGroup = dataGroup;
		if (dataGroup < 0)
			throw new IllegalStateException ("You must extract a data field.");
		try {
			this.currentLine = reader.readLine();
		} catch (IOException e) {
			throw new IllegalStateException ();
		}
	}

	public LineIterator (Reader input, String lineRegex, int dataGroup, int targetGroup, int uriGroup)
	{
		this (input, Pattern.compile (lineRegex), dataGroup, targetGroup, uriGroup);
	}

	public LineIterator (String filename, String lineRegex, int dataGroup, int targetGroup, int uriGroup)
		throws FileNotFoundException
	{
		this (new FileReader (new File(filename)),
					Pattern.compile (lineRegex), dataGroup, targetGroup, uriGroup);
	}
	
	// The PipeInputIterator interface

	public Instance next ()
	{
		String uriStr = null;
		String data = null;
		String target = null;
		Matcher matcher = lineRegex.matcher(currentLine);
		if (matcher.find()) {
			if (uriGroup > -1)
				uriStr = matcher.group(uriGroup);
			if (targetGroup > -1)
				target = matcher.group(targetGroup);
			if (dataGroup > -1)
				data = matcher.group(dataGroup);
		} else
			throw new IllegalStateException ("Line #"+reader.getLineNumber()+" does not match regex");

		String uri;
		if (uriStr == null) {
			uri = "csvline:"+reader.getLineNumber();
		} else {
			uri = uriStr;
		}
		assert (data != null);
		Instance carrier = new Instance (data, target, uri, null);
		try {
			this.currentLine = reader.readLine();
		} catch (IOException e) {
			throw new IllegalStateException ();
		}
		return carrier;
	}

	public boolean hasNext ()	{	return currentLine != null;	}
	
	public void remove () {
		throw new IllegalStateException ("This Iterator<Instance> does not support remove().");
	}

}
