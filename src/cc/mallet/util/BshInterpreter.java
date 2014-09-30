/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */


package cc.mallet.util;

import java.io.*;
import java.util.*;
import bsh.Interpreter;

public class BshInterpreter extends Interpreter
{
	Interpreter interpreter;

	public BshInterpreter (String prefixCommands)
	{
		try {
			eval (
				"import java.util.*;"+
				"import java.util.regex.*;"+
				"import java.io.*;"+
				"import cc.mallet.types.*;"+
				"import cc.mallet.pipe.*;"+
				"import cc.mallet.pipe.iterator.*;"+
				"import cc.mallet.pipe.tsf.*;"+
				"import cc.mallet.classify.*;"+
				"import cc.mallet.extract.*;"+
				"import cc.mallet.fst.*;"+
				"import cc.mallet.optimize.*;");
			if (prefixCommands != null)
				eval (prefixCommands);
		} catch (bsh.EvalError e) {
			throw new IllegalArgumentException ("bsh Interpreter error: "+e);
		}
	}

	public BshInterpreter ()
	{
		this (null);
	}
	
}
