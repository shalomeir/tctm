/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */





package cc.mallet.pipe;

import java.io.*;

import cc.mallet.types.Instance;
import cc.mallet.util.CharSequenceLexer;
/**
 * Given a filename contained in a string, read in contents of file into a CharSequence.
   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class Filename2CharSequence extends Pipe implements Serializable
{
	public Filename2CharSequence ()
	{
	}

	public Instance pipe (Instance carrier)
	{
		String filename = (String)carrier.getData();
		try {
			carrier.setData(pipe (new BufferedReader (new FileReader (filename))));
		} catch (IOException e) {
			throw new IllegalArgumentException ("IOException");
		}
		return carrier;
	}

	public CharSequence pipe (Reader reader)
		throws IOException
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

	// Serialization 
	
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;
	
	private void writeObject (ObjectOutputStream out) throws IOException {
		out.writeInt (CURRENT_SERIAL_VERSION);
	}
	
	private void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt ();
	}

}
