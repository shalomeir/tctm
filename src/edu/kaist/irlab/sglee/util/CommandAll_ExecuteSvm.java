/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;


import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.Strings;
import edu.kaist.irlab.classify.tui.ExecuteSvmMulticlass;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;


/**
* Command at once.
*
* @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
* @author Seonggyu Lee <a href="mailto:inflexer@gmail.com">inflexer@gmail.com</a>
*/

public class CommandAll_ExecuteSvm {

	private static Logger logger = MalletLogger.getLogger(CommandAll_ExecuteSvm.class.getName());

    //Main input Text directories.
    static CommandOption.SpacedStrings inputDirs =	new CommandOption.SpacedStrings
            (CommandAll_ExecuteSvm.class, "input-output-dirs", "DIR...", true, null,
                    "The directories containing train/test, one directory per tr/ts ratio directory"
                    +"Also this directory will be used for output directory.", null);


    //Output directory.
    static CommandOption.String outputDirName = new CommandOption.String
            (CommandAll_ExecuteSvm.class, "output-dir-name", "DIR...", true, null,
                    "Write the results to this location.", null);

    //Output directory.
    static CommandOption.String outputDir = new CommandOption.String
            (CommandAll_ExecuteSvm.class, "output-dir", "DIR...", false, null,
                    "Write the results to this location.", null);




	public static void main (String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// Process the command-line options
		CommandOption.setSummary (CommandAll_ExecuteSvm.class,
								  "A tool for creating varied feature vectors by training and test documents.\n");
		CommandOption.process (CommandAll_ExecuteSvm.class, args);


        //additional Common File Name


        //Time print
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("Start time : "+dateFormat.format(calendar.getTime()));


		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(CommandAll_ExecuteSvm.class).printUsage(false);
			System.exit (-1);
		}

		// Remove common prefix from all the input class directories
		int commonTrainPrefixIndex = Strings.commonPrefixIndex (inputDirs.value);

        //Input Directory
		logger.info ("Experiments All will be execute :");
        String[] inputExDirectories = new String[inputDirs.value.length];
		for (int i = 0; i < inputDirs.value.length; i++) {
            inputExDirectories[i] = inputDirs.value[i];
			if (commonTrainPrefixIndex < inputDirs.value.length) {
				logger.info ("   "+inputDirs.value[i].substring(commonTrainPrefixIndex));
			}
			else {
				logger.info ("   "+inputDirs.value[i]);
			}
		}

        if(outputDir.value==null) {
            outputDir.value = inputDirs.value[0].substring(0,commonTrainPrefixIndex-1);
        }


        //Do it ALL
//        --input-dir
//        data\corpus\4news-forMyExp\tr70\VariedFeatureSet/*

        System.out.println("Printing All SvmLightFeature extraction start at " + dateFormat.format(Calendar.getInstance().getTime()));

        ArrayList<String> featureDirsArray = null;
        for(String ratioDir:inputExDirectories){
            String ratioFeatureFiles = ratioDir+"/"+outputDirName.value;
            if(!(new File(ratioFeatureFiles).isDirectory())) continue;
            File[] featureFilesList = new File(ratioFeatureFiles).listFiles();
            featureDirsArray = new ArrayList<String>();
            for(File featureFile:featureFilesList){
                if(featureFile.isDirectory()){
                    featureDirsArray.add(featureFile.getPath());
                }
            }

            int inputArgsNum = 1+featureDirsArray.size();
            int inputSeq =0;
            String[] exArgs = new String[inputArgsNum];
            exArgs[inputSeq]="--input-dir";
            inputSeq++;
            for (int i = 0; i < featureDirsArray.size(); i++) {
                exArgs[inputSeq]=featureDirsArray.get(i);
                inputSeq++;
            }

            ExecuteSvmMulticlass.main(exArgs);
        }


        //ReadAll Svm Result and Write to one file.
        String allResult = outputDirName.value+" Result,"+"Ratio,";
        for (int i = 0; i < featureDirsArray.size(); i++) {
            allResult=allResult+new File(featureDirsArray.get(i)).getName()+",";
        }
        allResult=allResult+"Test instances\n";

        for(String ratioDir:inputExDirectories){
            String ratioFeatureFiles = ratioDir+"/"+outputDirName.value;
            if(!(new File(ratioFeatureFiles).isDirectory())) continue;

            try {

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(ratioFeatureFiles+"/svm_classify_all_result.csv"))));
                String line = null;
                String testInstance = null;

                boolean firstTime = true;
                while ((line = br.readLine()) != null) {
                    if(line.equals("")) continue;
                    String[] lineArr = line.split(",");
                    String[] lineFirst = lineArr[0].split("\\\\");

                    if(firstTime) allResult=allResult+lineFirst[4]+","+lineFirst[3]+",";
                    firstTime = false;

                    allResult=allResult+lineArr[1]+",";
                    testInstance = lineArr[4];
                }
                allResult=allResult+testInstance+"\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MyFileWriter.MyFileWriter(new File(inputDirs.value[0].substring(0,commonTrainPrefixIndex-1)+ "/AllRatio_"+outputDirName.value+"_result.csv"), allResult, "SVM Multiclass All Result");

        System.out.println("All Experiments Command All finished at "+dateFormat.format(Calendar.getInstance().getTime()));

	}

}
