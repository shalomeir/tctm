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
import edu.kaist.irlab.topics.tui.Text2VariedSvmLightFeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;


/**
* Command at once.
*
* @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
* @author Seonggyu Lee <a href="mailto:inflexer@gmail.com">inflexer@gmail.com</a>
*/

public class CommandAll_Text2VariedSvmFeatures {

	private static Logger logger = MalletLogger.getLogger(CommandAll_Text2VariedSvmFeatures.class.getName());

    //Main input Text directories.
    static CommandOption.SpacedStrings inputDirs =	new CommandOption.SpacedStrings
            (CommandAll_Text2VariedSvmFeatures.class, "input-output-dirs", "DIR...", true, null,
                    "The directories containing train/test, one directory per tr/ts ratio directory"
                    +"Also this directory will be used for output directory.", null);


    //Main input Topic directories.
    static CommandOption.String topicDir =	new CommandOption.String
            (CommandAll_Text2VariedSvmFeatures.class, "input-topic-dir", "DIR...", true, null,
                    "The directoriy containing topic model files. " +
                            "This scheme is followed by Text2VariedTopicModels Class output.", null);

    //Output directory.
    static CommandOption.String outputDirName = new CommandOption.String
            (CommandAll_Text2VariedSvmFeatures.class, "output-dir-name", "DIR...", false, null,
                    "Write the results to this location.", null);

    //Output directory.
    static CommandOption.String outputDir = new CommandOption.String
            (CommandAll_Text2VariedSvmFeatures.class, "output-dir", "DIR...", false, null,
                    "Write the results to this location.", null);




	public static void main (String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// Process the command-line options
		CommandOption.setSummary (CommandAll_Text2VariedSvmFeatures.class,
								  "A tool for creating varied feature vectors by training and test documents.\n");
		CommandOption.process (CommandAll_Text2VariedSvmFeatures.class, args);


        //Time print
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("Start time : "+dateFormat.format(calendar.getTime()));


		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(CommandAll_Text2VariedSvmFeatures.class).printUsage(false);
			System.exit (-1);
		}
		if (topicDir.value == null||inputDirs.value.length == 0) {
			throw new IllegalArgumentException ("You must include --input-ouput-dir DIR1 DIR2 ...' in order to specify a " +
								"list of directories containing the documents for each class.");
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

        System.out.println("Printing All SvmLightFeature extraction start at " + dateFormat.format(Calendar.getInstance().getTime()));
        boolean pass = true;

        for(String ratioDir:inputExDirectories){
//            if(ratioDir.substring(ratioDir.length()-4).equals("tr20")) pass=false;
//            if(pass) continue;

            String ratioTrainFiles = ratioDir+"/train";
            String ratioTestFiles = ratioDir+"/test";
            if(!(new File(ratioTrainFiles).isDirectory())||!(new File(ratioTestFiles).isDirectory()) ) continue;
            File[] trainFilesList = new File(ratioTrainFiles).listFiles();
            File[] testFilesList = new File(ratioTestFiles).listFiles();


            int inputArgsNum = 6+trainFilesList.length+testFilesList.length;
            int inputSeq =0;
            String[] exArgs = new String[inputArgsNum];
            exArgs[inputSeq]="--input-train-dir";
            inputSeq++;
            for (int i = 0; i < trainFilesList.length; i++) {
                exArgs[inputSeq]=trainFilesList[i].getPath();
                inputSeq++;
            }

            exArgs[inputSeq]="--input-test-dir";
            inputSeq++;
            for (int i = 0; i < testFilesList.length; i++) {
                exArgs[inputSeq]=testFilesList[i].getPath();
                inputSeq++;
            }

            exArgs[inputSeq]="--input-topic-dir";
            inputSeq++;
            exArgs[inputSeq]= topicDir.value;
            inputSeq++;

            exArgs[inputSeq]="--output-dir";
            inputSeq++;
            exArgs[inputSeq]= ratioDir+"/"+outputDirName.value;

            Text2VariedSvmLightFeatures.main(exArgs);

        }


        System.out.println("All Experiments Command All finished at "+dateFormat.format(Calendar.getInstance().getTime()));

	}

}
