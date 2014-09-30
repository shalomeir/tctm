/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

/*
 * This class use a svm multiclass windows exe file for svm classifier.
 *
 */

package edu.kaist.irlab.classify.tui;


import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import edu.kaist.irlab.sglee.util.MyFileWriter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;


/**
*
*/

public class ExecuteSvmMulticlass {

	private static Logger logger = MalletLogger.getLogger(ExecuteSvmMulticlass.class.getName());

	static CommandOption.SpacedStrings featureSetDirs =	new CommandOption.SpacedStrings
		(ExecuteSvmMulticlass.class, "input-dir", "DIR...", true, null,
		 "The directories containing text files to be classified, one directory feature set", null);

    static CommandOption.SpacedStrings outputDirs =	new CommandOption.SpacedStrings
            (ExecuteSvmMulticlass.class, "output-dir", "DIR...", true, null,
                    "The directories would be save output file, one directory feature set", null);

    static CommandOption.String trainfileName =	new CommandOption.String
            (ExecuteSvmMulticlass.class, "train", "DIR...", true, "train_Svmlight.txt",
                    "Training file name", null);

    static CommandOption.String testfileName =	new CommandOption.String
            (ExecuteSvmMulticlass.class, "test", "DIR...", true, "test_Svmlight.txt",
                    "Training file name", null);

    static CommandOption.String svmMultiExeFile =	new CommandOption.String
            (ExecuteSvmMulticlass.class, "svm-multiclass", "DIR...", true, "exprogram/svm_multiclass_windows",
                    "SVM_Multiclass file location which this program should execute.", null);


	public static void main (String[] args) throws FileNotFoundException, IOException {
        // Process the command-line options
        CommandOption.setSummary(ExecuteSvmMulticlass.class,
                "A tool for creating instance lists of FeatureVectors or FeatureSequences from text documents.\n");
        CommandOption.process(ExecuteSvmMulticlass.class, args);
        //String[] classDirs = CommandOption.process (Text2Vectors.class, args);

        // Print some helpful messages for error cases
        if (args.length == 0) {
            CommandOption.getList(ExecuteSvmMulticlass.class).printUsage(false);
            System.exit(-1);
        }
        if (featureSetDirs.value.length == 0) {
            throw new IllegalArgumentException("You must include --input DIR1 DIR2 ...' in order to specify a " +
                    "list of directories containing svmlight style train and test text.");
        }

        ArrayList<File> featureDirectories = new ArrayList<File>();
        for (int i = 0; i < featureSetDirs.value.length; i++) {
            File tempDir = new File(featureSetDirs.value[i]);
            if (tempDir.isDirectory()) {
                featureDirectories.add(new File(featureSetDirs.value[i]));
                logger.info("   " + featureSetDirs.value[i]);
            }else{
                logger.info("   " + featureSetDirs.value[i] + " is not Directory.");
            }
        }

//        if (outputDirs.value == null) outputDirs.value = featureSetDirs.value;
        outputDirs.value = featureSetDirs.value;
        ArrayList<File> outputDirectories = new ArrayList<File>();
        for (int i = 0; i < outputDirs.value.length; i++) {
            File tempDir = new File(outputDirs.value[i]);
            if (tempDir.isDirectory()) {
                outputDirectories.add(new File(outputDirs.value[i] + "/svmmodel"));
                MyFileWriter.directoryConfirmAndMake(outputDirs.value[i] + "/svmmodel");
                logger.info("   " + outputDirs.value[i]);
            }else{
                logger.info("   " + outputDirs.value[i] + " is not Directory.");
            }
        }


        //Time print
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("Start time : " + dateFormat.format(calendar.getTime()));

        //Start External Program in svmMultiExeFile Learn
        Runtime rt = Runtime.getRuntime();
        String exeLearnFile = "svm_multiclass_learn";
        System.out.println("exeFile: " + exeLearnFile);


        for (int i = 0; i < featureDirectories.size(); i++) {
            System.out.println("\n\nLearning : " + featureDirectories.get(i).toString());

            String option = " -c 1000";
            option = option + " ../../" + featureDirectories.get(i).toString() + "/" + trainfileName.value;
            option = option + " ../../" + outputDirectories.get(i).toString() + "/" + "train_svmmodel.txt";

            try {
                Process p = Runtime.getRuntime().exec("cmd /c " + exeLearnFile + option, null, new File(svmMultiExeFile.value));
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                String fullLog = "";

                while ((line = br.readLine()) != null) {
//                    System.out.println(line);
                    fullLog = fullLog + line + "\n";
                }
                MyFileWriter.MyFileWriter(new File(outputDirectories.get(i).toString() + "/learn.log"), fullLog, "SVM Multiclass Learn");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Start External Program in svmMultiExeFile Classify
        String exeClassifyFile = "svm_multiclass_classify";
        String classifyLines = "";
        String delims = "[ ,%(]+";
        System.out.println("exeFile: " + exeLearnFile);


        for (int i = 0; i < featureDirectories.size(); i++) {
            System.out.println("\n\nClassify : " + featureDirectories.get(i).toString());

            String option = " ../../" + featureDirectories.get(i).toString() + "/" + testfileName.value;
            option = option + " ../../" + outputDirectories.get(i).toString() + "/" + "train_svmmodel.txt";
            option = option + " ../../" + outputDirectories.get(i).toString() + "/" + "test_classify_result.txt";

            try {
                Process p = Runtime.getRuntime().exec("cmd /c " + exeClassifyFile + option, null, new File(svmMultiExeFile.value));
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                String lastLine = null;
                String fullLog = "";

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    fullLog = fullLog + line + "\n";
                    lastLine = line;
                }

                MyFileWriter.MyFileWriter(new File(outputDirectories.get(i).toString() + "/classify.log"), fullLog, "SVM Multiclass Classify");
                String[] lineArr = lastLine.split(delims);

                classifyLines = classifyLines + featureDirectories.get(i).toString() + "," + lineArr[4] + "," + lineArr[5] + "," + lineArr[7] + "," + lineArr[9] + "\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MyFileWriter.MyFileWriter(new File(featureSetDirs.value[0] + "/../svm_classify_all_result.csv"), classifyLines, "All Result in one file.");

        System.out.println("\n\nStart time : " + dateFormat.format(calendar.getTime()));
        System.out.println("All Jobs finished at " + dateFormat.format(Calendar.getInstance().getTime()));

    } //main
}
