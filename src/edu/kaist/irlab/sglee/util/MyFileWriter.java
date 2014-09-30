/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;

import java.io.*;

public class MyFileWriter {

		
	public static void MyFileWriter(String output_dir,File absolute_file, String text, String subject) throws FileNotFoundException {
		// TODO Auto-generated method stub
//		this(output_dr_file,text);
		File output_dr_file = new File(output_dir+absolute_file);
		MyFileWriter(output_dr_file,text,subject);
	}

	public static void MyFileWriter(File output_dr_file, String text, String subject) throws FileNotFoundException {
		File absoluteFile = output_dr_file.getAbsoluteFile();
		File parentDir = new File(absoluteFile.getParent());
		if (!parentDir.exists())
			parentDir.mkdirs();	

		PrintWriter out = new PrintWriter(absoluteFile);
//		out.println("*** Log time : "+CurrentTimeWriter.CurrentTimeWriter()+" ***");
//		out.println("[Subject] :"+subject+"\n");
		out.println(text);
		out.close();
		System.out.println("Writing Files completed :\t"+absoluteFile.toString());

	}
	
	public static void MyFileOngoingWriter(File output_dr_file, String text, String subject) throws IOException {
		File absoluteFile = output_dr_file.getAbsoluteFile();
		File parentDir = new File(absoluteFile.getParent());

        if (!parentDir.exists())
            System.out.println(parentDir.mkdirs());

		FileWriter writer = null;
		BufferedWriter bw = null;
		
		if(!absoluteFile.exists()){
			writer = new FileWriter(absoluteFile, false);
			bw = new BufferedWriter(writer);
//			bw.write("StartTime\tArgs0\tArgs1\tArgs2\tExperimentName\tmodelLogLikelihood\tempiricalLikelihood\t" +
//					"alphaSum\tbetaSum\tnumIterations\tnumTypes\toptimizeInterval\tnumTopics\t\tnumFeaturesfeatureParameterSquareWeight\tEndTime\tTimePeriod\n");
		}else{
			writer = new FileWriter(absoluteFile, true);
			bw = new BufferedWriter(writer);
		}

		bw.write(text);
		bw.close();
		System.out.println("Writing Files completed :\t"+absoluteFile.toString());

	}

    public static void directoryConfirmAndMake(String targetDir){
        File d = new File(targetDir);
        if(!d.isDirectory()){
            d.mkdirs();
        }
    }

    public static void writeObject(Object object,String filename) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(filename)));
        oos.writeObject(object);
        oos.close();
    }

}
