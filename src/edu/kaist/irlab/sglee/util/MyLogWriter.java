/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class MyLogWriter {

		
	public static void MyLogWriter(String output_dir,File absolute_file, String text, String subject) throws FileNotFoundException {
		// TODO Auto-generated method stub
//		this(output_dr_file,text);
		File output_dr_file = new File(output_dir+absolute_file);
		MyLogWriter(output_dr_file,text,subject);
	}

	public static void MyLogWriter(File output_dr_file, String text, String subject) throws FileNotFoundException {
		File absoluteFile = output_dr_file.getAbsoluteFile();
		File parentDir = new File(absoluteFile.getParent());
		if (!parentDir.exists())
			parentDir.mkdirs();	

		PrintWriter out = new PrintWriter(absoluteFile);
		out.println("*** Log time : "+CurrentTimeWriter.CurrentTimeWriter()+" ***");
		out.println("[Subject] :"+subject+"\n");
		out.println(text);
		out.close();
		System.out.println("Writing Log Files completed :\t"+absoluteFile.toString());

	}
}
