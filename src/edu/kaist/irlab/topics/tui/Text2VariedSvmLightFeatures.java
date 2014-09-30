/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: inflexer@gmail.com
 */

package edu.kaist.irlab.topics.tui;

import cc.mallet.classify.tui.SvmLight2Vectors;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.InstanceList;
import cc.mallet.util.CharSequenceLexer;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.Strings;
import edu.kaist.irlab.sglee.util.MyFileWriter;
import edu.kaist.irlab.textcontrol.SvmLights2SvmLight;
import edu.kaist.irlab.textcontrol.Vectors2SvmStyleBow;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
* Convert text document files into vectors (a persistent instance list) based on Text2Vector Mallet Source.
* With this, transform text corpus to varied Bag of Word Features, Topic Features and Mixed Features.
* If you set up training and test ratio, this process divide by text to training and test based on that ratio.
*
* @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
* @author Seonggyu Lee <a href="mailto:inflexer@gmail.com">inflexer@gmail.com</a>
*/

public class Text2VariedSvmLightFeatures {

	private static Logger logger = MalletLogger.getLogger(Text2VariedSvmLightFeatures.class.getName());

    //Main input Text directories.
    static CommandOption.SpacedStrings trainDirs =	new CommandOption.SpacedStrings
            (Text2VariedSvmLightFeatures.class, "input-train-dir", "DIR...", true, null,
                    "The directories containing training text files, one directory per class", null);

    //Main input Text directories.
    static CommandOption.SpacedStrings testDirs =	new CommandOption.SpacedStrings
            (Text2VariedSvmLightFeatures.class, "input-test-dir", "DIR...", true, null,
                    "The directories containing training text files, one directory per class", null);

    //Main input Text directories.
    static CommandOption.String topicDir =	new CommandOption.String
            (Text2VariedSvmLightFeatures.class, "input-topic-dir", "DIR...", true, null,
                    "The directoriy containing topic model files. " +
                            "This scheme is followed by Text2VariedTopicModels Class output.", null);

    //Output directory.
    static CommandOption.String outputDir = new CommandOption.String
            (Text2VariedSvmLightFeatures.class, "output-dir", "DIR...", true, null,
                    "Write the topic model results to this location.", null);


    //Bog of Words Features
    //BOW.1. Term Frequency
    static CommandOption.Boolean printTfBow1 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-tf-bow", "true|false", true, true,
                    "Print Basic Term Frequency Bag of Words Features.", null);

    //BOW.2. TFIDF
    static CommandOption.Boolean printTfIdfBow2 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-tfidf-bow", "true|false", true, true,
                    "Print Basic TF*IDF Bag of Words Features.", null);

    //BOW.3. Basic LDA BOW
    static CommandOption.Boolean printBasicLdaBow3 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-basiclda-bow", "true|false", true, false,
                    "Print Feature that weighted by Term Weight which extracted by Basic LDA Last variance.", null);

    //BOW.4. WTM BOW
    static CommandOption.Boolean printWtmBow4 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-wtm-bow", "true|false", true, false,
                    "Print Feature that weighted by Term Weight which extracted by weighted topic model.", null);

    //BOW.5. BWTM BOW
    static CommandOption.Boolean printBwtmBow5 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-bwtm-bow", "true|false", true, false,
                    "Print Feature that weighted by Term Weight which extracted by weighted topic model.", null);

    //Topic Features
    //Topic.1. Basic LDA topic feature
    static CommandOption.Boolean printBasicLdaFeature1 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-basiclda-feature", "true|false", true, true,
                    "Print Basic LDA Topic Feature.", null);

    //Topic.2. WTM topic feature
    static CommandOption.Boolean printWtmFeature2 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-wtm-feature", "true|false", true, false,
                    "Print Weighted Topic Feature.", null);

    //Topic.3. BWTM topic feature
    static CommandOption.Boolean printBwtmFeature3 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-bwtm-feature", "true|false", true, false,
                    "Print Balance Weighted Topic Feature.", null);

    //Topic.4. IDF-WTM topic feature
    static CommandOption.Boolean printIdfWtmFeature4 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idfwtm-feature", "true|false", true, false,
                    "Print IDF Weighted Topic Feature.", null);

    //Topic.5. WTM topic feature
    static CommandOption.Boolean printIdfBwtmFeature5 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idfbwtm-feature", "true|false", true, false,
                    "Print IDF Balance Weighted Topic Feature.", null);

    //Topic and BOW Mixed Features
    //Mix.1. Basic TF + Basic LDA
    static CommandOption.Boolean printTfBasicldaMix1 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-tf-basiclda-btmix", "true|false", true, false,
                    "Print Basic TF + Basic LDA Mixed Feature.", null);

    //Mix.2. tf*idf + Basic LDA
    static CommandOption.Boolean printIdfBasicldaMix2 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idf-basiclda-btmix", "true|false", true, true,
                    "Print tf*idf + Basic LDA Mixed Feature.", null);

    //Mix.3. wtm + wtm
    static CommandOption.Boolean printWtmWtmMix3 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-wtm-wtm-btmix", "true|false", true, false,
                    "Print wtm + wtm Mixed Feature.", null);

    //Mix.4. bwtm + bwtm
    static CommandOption.Boolean printBwtmBwtmMix4 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-bwtm-bwtm-btmix", "true|false", true, false,
                    "Print bwtm + bwtm Mixed Feature.", null);

    //Mix.5. wtm + bwtm
    static CommandOption.Boolean printWtmBwtmMix5 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-wtm-bwtm-btmix", "true|false", true, false,
                    "Print wtm + bwtm Mixed Feature.", null);

    //Mix.6. bwtm + wtm
    static CommandOption.Boolean printBwtmWtmMix6 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-bwtm-wtm-btmix", "true|false", true, false,
                    "Print bwtm + wtm Mixed Feature.", null);

    //Mix.7. idf + idfwtm
    static CommandOption.Boolean printIdfIdfWtmMix7 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idf-idfwtm-btmix", "true|false", true, false,
                    "Print idf + idfwtm Mixed Feature.", null);

    //Mix.8. idf + idfbwtm
    static CommandOption.Boolean printIdfIdfBwtmMix8 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idf-idfbwtm-btmix", "true|false", true, false,
                    "Print idf + idfwtm Mixed Feature.", null);

    //Mix.9. idf + wtm
    static CommandOption.Boolean printIdfWtmMix9 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idf-wtm-btmix", "true|false", true, false,
                    "Print idf + idfwtm Mixed Feature.", null);

    //Mix.10. idf + bwtm
    static CommandOption.Boolean printIdfBwtmMix10 = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "print-idf-bwtm-btmix", "true|false", true, false,
                    "Print idf + idfwtm Mixed Feature.", null);


    //Option For Text2VariedSvmLightFeatures
    //Remover Stopwords. This is experiment for Basic LDA
    static CommandOption.Boolean removeStopWords = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "remove-stopwords", "[TRUE|FALSE]", true, false,
                    "If true, remove a default list of common English \"stop words\" from the text.", null);

    //Check data by filename only
    static CommandOption.Boolean checkOnlyFilename = new CommandOption.Boolean
            (Text2VariedSvmLightFeatures.class, "check-only-filename", "[TRUE|FALSE]", true, false,
                    "If true, compare with topic feature and bow feature that file is same or not based on only filename", null);


    //Mallet default option
	static CommandOption.File usePipeFromVectorsFile = new CommandOption.File
		(Text2VariedSvmLightFeatures.class, "use-pipe-from", "FILE", true, new File("text.vectors"),
		 "Use the pipe and alphabets from a previously created vectors file. " +
		 "Allows the creation, for example, of a test set of vectors that are " +
		 "compatible with a previously created set of training vectors", null);

	static CommandOption.Boolean preserveCase = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "preserve-case", "[TRUE|FALSE]", false, false,
		 "If true, do not force all strings to lowercase.", null);

	static CommandOption.File stoplistFile = new CommandOption.File
		(Text2VariedSvmLightFeatures.class, "stoplist-file", "FILE", true, null,
		 "Instead of the default list, read stop words from a file, one per line. Implies --remove-stopwords", null);

	static CommandOption.File extraStopwordsFile = new CommandOption.File
		(Text2VariedSvmLightFeatures.class, "extra-stopwords", "FILE", true, null,
		 "Read whitespace-separated words from this file, and add them to either\n" +
		 "   the default English stoplist or the list specified by --stoplist-file.", null);

	static CommandOption.Boolean skipHeader = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "skip-header", "[TRUE|FALSE]", false, false,
		 "If true, in each document, remove text occurring before a blank line."+
		 "  This is useful for removing email or UseNet headers", null);

	static CommandOption.Boolean skipHtml = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "skip-html", "[TRUE|FALSE]", false, false,
		 "If true, remove text occurring inside <...>, as in HTML or SGML.", null);

	static CommandOption.Boolean binaryFeatures = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "binary-features", "[TRUE|FALSE]", false, false,
		 "If true, features will be binary.", null);

	static CommandOption.IntegerArray gramSizes = new CommandOption.IntegerArray
		(Text2VariedSvmLightFeatures.class, "gram-sizes", "INTEGER,[INTEGER,...]", true, new int[] {1},
		 "Include among the features all n-grams of sizes specified.  "+
		 "For example, to get all unigrams and bigrams, use --gram-sizes 1,2.  "+
		 "This option occurs after the removal of stop words, if removed.", null);

	static CommandOption.Boolean keepSequenceBigrams = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "keep-sequence-bigrams", "[TRUE|FALSE]", false, false,
		 "If true, final data will be a FeatureSequenceWithBigrams rather than a FeatureVector.", null);

	static CommandOption.Boolean saveTextInSource = new CommandOption.Boolean
		(Text2VariedSvmLightFeatures.class, "save-text-in-source", "[TRUE|FALSE]", false, false,
		 "If true, save original text of document in source.", null);

	static CommandOption.ObjectFromBean stringPipe = new CommandOption.ObjectFromBean
		(Text2VariedSvmLightFeatures.class, "string-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a CharSequence", null);

	static CommandOption.ObjectFromBean tokenPipe = new CommandOption.ObjectFromBean
		(Text2VariedSvmLightFeatures.class, "token-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a TokenSequence", null);

	static CommandOption.ObjectFromBean featureVectorPipe = new CommandOption.ObjectFromBean
		(Text2VariedSvmLightFeatures.class, "fv-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a FeatureVector", null);

	static CommandOption.String encoding = new CommandOption.String
		(Text2VariedSvmLightFeatures.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
		 "Character encoding for input file", null);

	static CommandOption.String tokenRegex = new CommandOption.String
		(Text2VariedSvmLightFeatures.class, "token-regex", "REGEX", true, CharSequenceLexer.LEX_ALPHA.toString(),
		 "Regular expression used for tokenization.\n" +
		 "   Example: \"[\\p{L}\\p{N}_]+|[\\p{P}]+\" (unicode letters, numbers and underscore OR all punctuation) ", null);



	public static void main (String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// Process the command-line options
		CommandOption.setSummary (Text2VariedSvmLightFeatures.class,
								  "A tool for creating varied feature vectors by training and test documents.\n");
		CommandOption.process (Text2VariedSvmLightFeatures.class, args);

        // All Output File Name is here.
        // String argument name is fixed by below.
        String outputRealDir = outputDir.value;
        if(removeStopWords.value()) outputRealDir = outputRealDir + "_RemoveStopwords";
        if(outputRealDir!=null) MyFileWriter.directoryConfirmAndMake(outputRealDir);
        String malletTrainInstanceName = outputRealDir+"/train.mallet";
        String malletTestInstanceName = outputRealDir+"/test.mallet";

        //additional Common File Name
        String strTrain = "train";
        String strTest = "test";
        String[] strTrTs = {"train","test"};
        String suffixSvmTxt = "_Svmlight.txt";
        String suffixStatTxt = "_stat.txt";
        String suffixPipe = "_pipe.mallet";


        //TermWeight File Location
        String ldaTermWeight = topicDir.value+"/BasicLDA/lda_TermWeightObject.ser";
        if(!new File(ldaTermWeight).isFile()) ldaTermWeight=null;

        String wtmTermWeight = topicDir.value+"/WTM/wtm_TermWeightObject.ser";
        if(!new File(wtmTermWeight).isFile()) wtmTermWeight=null;

        String bwtmTermWeight = topicDir.value+"/BWTM/bwtm_TermWeightObject.ser";
        if(!new File(bwtmTermWeight).isFile()) bwtmTermWeight=null;

        String idfWtmTermWeight = topicDir.value+"/IdfWTM/idfWtm_TermWeightObject.ser";
        if(!new File(idfWtmTermWeight).isFile()) idfWtmTermWeight=null;

        String idfBwtmTermWeight = topicDir.value+"/IdfBWTM/idfBwtm_TermWeightObject.ser";
        if(!new File(idfBwtmTermWeight).isFile()) idfBwtmTermWeight=null;

        String idfTermWeight = topicDir.value+"/Idf_TermWeightObject.ser";
        if(!new File(idfTermWeight).isFile()) {
            idfTermWeight = topicDir.value+"/../Idf_TermWeightObject.ser";
            if(!new File(idfTermWeight).isFile()) idfTermWeight=null;
        }

        //Topic Model feature per each document from Topic Dir
        String ldaTopicmodel = topicDir.value+"/BasicLDA/lda_TopicFeaturesPerDoc_Svmlight.txt";
        if(!new File(ldaTopicmodel).isFile()) ldaTopicmodel=null;

        String wtmTopicmodel = topicDir.value+"/WTM/wtm_TopicFeaturesPerDoc_Svmlight.txt";
        if(!new File(wtmTopicmodel).isFile()) wtmTopicmodel=null;

        String bwtmTopicmodel = topicDir.value+"/BWTM/bwtm_TopicFeaturesPerDoc_Svmlight.txt";
        if(!new File(bwtmTopicmodel).isFile()) bwtmTopicmodel=null;

        String idfWtmTopicmodel = topicDir.value+"/IdfWTM/idfWtm_TopicFeaturesPerDoc_Svmlight.txt";
        if(!new File(idfWtmTopicmodel).isFile()) idfWtmTopicmodel=null;

        String idfBwtmTopicmodel = topicDir.value+"/IdfBWTM/idfBwtm_TopicFeaturesPerDoc_Svmlight.txt";
        if(!new File(idfBwtmTopicmodel).isFile()) idfBwtmTopicmodel=null;


        //Bag of Words Output Directories
        String dirTfBow1=null;
        if(printTfBow1.value) {
            String featureName = "TfBow1";
            dirTfBow1 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirTfBow1);
        }

        String dirTfIdfBow2=null;
        if(printTfIdfBow2.value) {
            String featureName = "TfIdfBow2";
            dirTfIdfBow2 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirTfIdfBow2);
        }

        String dirBasicLdaBow3=null;
        if(printBasicLdaBow3.value) {
            String featureName = "BasicLdaBow3";
            dirBasicLdaBow3 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBasicLdaBow3);
        }

        String dirWtmBow4=null;
        if(printWtmBow4.value) {
            String featureName = "WtmBow4";
            dirWtmBow4 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirWtmBow4);
        }

        String dirBwtmBow5=null;
        if(printBwtmBow5.value) {
            String featureName = "BwtmBow5";
            dirBwtmBow5 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBwtmBow5);
        }


        //Topic Features Output Directories
        String dirBasicLdaFeature1=null;
        if(printBasicLdaFeature1.value) {
            String featureName = "BasicLdaFeature1";
            dirBasicLdaFeature1 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBasicLdaFeature1);
        }

        String dirWtmFeature2=null;
        if(printWtmFeature2.value) {
            String featureName = "WtmFeature2";
            dirWtmFeature2 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirWtmFeature2);
        }

        String dirBwtmFeature3=null;
        if(printBwtmFeature3.value) {
            String featureName = "BwtmFeature3";
            dirBwtmFeature3 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBwtmFeature3);
        }

        String dirIdfWtmFeature4=null;
        if(printIdfWtmFeature4.value) {
            String featureName = "IdfWtmFeature4";
            dirIdfWtmFeature4 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfWtmFeature4);
        }

        String dirIdfBwtmFeature5=null;
        if(printIdfBwtmFeature5.value) {
            String featureName = "IdfBwtmFeature5";
            dirIdfBwtmFeature5 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfBwtmFeature5);
        }

        //Topic and BOW Mixed Features Output Directories
        String dirTfBasicldaMix1=null;
        if(printTfBasicldaMix1.value) {
            String featureName = "TfBasicldaMix1";
            dirTfBasicldaMix1 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirTfBasicldaMix1);
        }

        String dirIdfBasicldaMix2=null;
        if(printIdfBasicldaMix2.value) {
            String featureName = "IdfBasicldaMix2";
            dirIdfBasicldaMix2 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfBasicldaMix2);
        }

        String dirWtmWtmMix3=null;
        if(printWtmWtmMix3.value) {
            String featureName = "WtmWtmMix3";
            dirWtmWtmMix3 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirWtmWtmMix3);
        }

        String dirBwtmBwtmMix4=null;
        if(printBwtmBwtmMix4.value) {
            String featureName = "BwtmBwtmMix4";
            dirBwtmBwtmMix4 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBwtmBwtmMix4);
        }

        String dirWtmBwtmMix5=null;
        if(printWtmBwtmMix5.value) {
            String featureName = "WtmBwtmMix5";
            dirWtmBwtmMix5 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirWtmBwtmMix5);
        }

        String dirBwtmWtmMix6=null;
        if(printBwtmWtmMix6.value) {
            String featureName = "BwtmWtmMix6";
            dirBwtmWtmMix6 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirBwtmWtmMix6);
        }

        String dirIdfIdfWtmMix7=null;
        if(printIdfIdfWtmMix7.value) {
            String featureName = "IdfIdfWtmMix7";
            dirIdfIdfWtmMix7 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfIdfWtmMix7);
        }

        String dirIdfIdfBwtmMix8=null;
        if(printIdfIdfBwtmMix8.value) {
            String featureName = "IdfIdfBwtmMix8";
            dirIdfIdfBwtmMix8 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfIdfBwtmMix8);
        }

        String dirIdfWtmMix9=null;
        if(printIdfWtmMix9.value) {
            String featureName = "IdfWtmMix9";
            dirIdfWtmMix9 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfWtmMix9);
        }

        String dirIdfBwtmMix10=null;
        if(printIdfBwtmMix10.value) {
            String featureName = "IdfBwtmMix10";
            dirIdfBwtmMix10 = outputRealDir+"/"+featureName+"/";
            MyFileWriter.directoryConfirmAndMake(dirIdfBwtmMix10);
        }


        //Time print
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("Start time : "+dateFormat.format(calendar.getTime()));


		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(Text2VariedSvmLightFeatures.class).printUsage(false);
			System.exit (-1);
		}
		if (testDirs.value.length == 0||trainDirs.value.length == 0) {
			throw new IllegalArgumentException ("You must include --input-test-dir DIR1 DIR2 ...' in order to specify a " +
								"list of directories containing the documents for each class.");
		}

		// Remove common prefix from all the input class directories
		int commonTrainPrefixIndex = Strings.commonPrefixIndex (trainDirs.value);
        int commonTestPrefixIndex = Strings.commonPrefixIndex (testDirs.value);

        //Train Labeling
		logger.info ("Train Labels = ");
		File[] trainDirectories = new File[trainDirs.value.length];
		for (int i = 0; i < trainDirs.value.length; i++) {
            trainDirectories[i] = new File (trainDirs.value[i]);
			if (commonTrainPrefixIndex < trainDirs.value.length) {
				logger.info ("   "+trainDirs.value[i].substring(commonTrainPrefixIndex));
			}
			else {
				logger.info ("   "+trainDirs.value[i]);
			}
		}

        //Test Labeling
        logger.info ("Test Labels = ");
        File[] testDirectories = new File[testDirs.value.length];
        for (int i = 0; i < testDirs.value.length; i++) {
            testDirectories[i] = new File (testDirs.value[i]);
            if (commonTestPrefixIndex < testDirs.value.length) {
                logger.info ("   "+testDirs.value[i].substring(commonTestPrefixIndex));
            }
            else {
                logger.info ("   "+testDirs.value[i]);
            }
        }


        Pipe instancePipe;
        InstanceList previousInstanceList = null;

		if (usePipeFromVectorsFile.wasInvoked()) {
			previousInstanceList = InstanceList.load (usePipeFromVectorsFile.value);
			instancePipe = previousInstanceList.getPipe();
		}
		else {

			// Build a new pipe

			// Create a list of pipes that will be added to a SerialPipes object later
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

			// Convert the "target" object into a numeric index
			//  into a LabelAlphabet.
			pipeList.add(new Target2Label());

			// The "data" field is currently a filename. Save it as "source".
			pipeList.add( new SaveDataInSource() );

			// Set "data" to the file's contents. "data" is now a String.
			pipeList.add( new Input2CharSequence(encoding.value) );

			// Optionally save the text to "source" -- not recommended if memory is scarce.
			if (saveTextInSource.wasInvoked()) {
				pipeList.add( new SaveDataInSource() );
			}

			// Allow the user to specify an arbitrary Pipe object
			//  that operates on Strings
			if (stringPipe.wasInvoked()) {
				pipeList.add( (Pipe) stringPipe.value );
			}

			// Remove all content before the first empty line.
			//  Useful for email and usenet news posts.
			if (skipHeader.value) {
				pipeList.add( new CharSubsequence(CharSubsequence.SKIP_HEADER) );
			}

			// Remove HTML tags. Suitable for SGML and XML.
			if (skipHtml.value) {
				pipeList.add( new CharSequenceRemoveHTML() );
			}


			//
			// Tokenize the input: first compile the tokenization pattern
			//

			Pattern tokenPattern = null;

			if (keepSequenceBigrams.value) {
				// We do not want to record bigrams across punctuation,
				//  so we need to keep non-word tokens.
				tokenPattern = CharSequenceLexer.LEX_NONWHITESPACE_CLASSES;
			}
			else {
				// Otherwise, try to compile the regular expression pattern.

				try {
					tokenPattern = Pattern.compile(tokenRegex.value);
				} catch (PatternSyntaxException pse) {
					throw new IllegalArgumentException("The token regular expression (" + tokenRegex.value +
									   ") was invalid: " + pse.getMessage());
				}
			}

			// Add the tokenizer
			pipeList.add(new CharSequence2TokenSequence(tokenPattern));

			// Allow user to specify an arbitrary Pipe object
			//  that operates on TokenSequence objects.
			if (tokenPipe.wasInvoked()) {
				pipeList.add( (Pipe) tokenPipe.value );
			}

			if (! preserveCase.value()) {
				pipeList.add(new TokenSequenceLowercase());
			}

			if (keepSequenceBigrams.value) {
				// Remove non-word tokens, but record the fact that they
				//  were there.
				pipeList.add(new TokenSequenceRemoveNonAlpha(true));
			}

			// Stopword removal.
			if (stoplistFile.wasInvoked()) {

				// The user specified a new list

				TokenSequenceRemoveStopwords stopwordFilter =
					new TokenSequenceRemoveStopwords(stoplistFile.value,
													 encoding.value,
													 false, // don't include default list
													 false,
													 keepSequenceBigrams.value);

				if (extraStopwordsFile.wasInvoked()) {
					stopwordFilter.addStopWords(extraStopwordsFile.value);
				}

				pipeList.add(stopwordFilter);
			}
			else if (removeStopWords.value) {

				// The user did not specify a new list, so use the default
				//  built-in English list, possibly adding extra words.

				TokenSequenceRemoveStopwords stopwordFilter =
					new TokenSequenceRemoveStopwords(false, keepSequenceBigrams.value);

				if (extraStopwordsFile.wasInvoked()) {
					stopwordFilter.addStopWords(extraStopwordsFile.value);
				}

				pipeList.add(stopwordFilter);

			}

			// gramSizes is an integer array, with default value [1].
			//  Check if we have a non-default value.
			if (! (gramSizes.value.length == 1 && gramSizes.value[0] == 1)) {
				pipeList.add( new TokenSequenceNGrams(gramSizes.value) );
			}

			// So far we have a sequence of Token objects that contain
			//  String values. Look these up in an alphabet and store integer IDs
			//  ("features") instead of Strings.
			if (keepSequenceBigrams.value) {
				pipeList.add( new TokenSequence2FeatureSequenceWithBigrams() );
			}
			else {
				pipeList.add( new TokenSequence2FeatureSequence() );
			}

			// For many applications, we do not need to preserve the sequence of features,
			//  only the number of times times a feature occurs.
//			if (! (keepSequence.value || keepSequenceBigrams.value)) {
				pipeList.add( new FeatureSequence2AugmentableFeatureVector(binaryFeatures.value) );
//			}

			// Allow users to specify an arbitrary Pipe object that operates on
			//  feature vectors.
			if (featureVectorPipe.wasInvoked()) {
				pipeList.add( (Pipe) featureVectorPipe.value );
			}

			instancePipe = new SerialPipes(pipeList);
		}//Pipe loaded.


        //Real Start.
        InstanceList trainInstances = new InstanceList (instancePipe);
        InstanceList testInstances = new InstanceList (instancePipe);

        boolean removeCommonPrefix = true;
        trainInstances.addThruPipe (new FileIterator(trainDirectories, FileIterator.STARTING_DIRECTORIES, removeCommonPrefix));
        testInstances.addThruPipe (new FileIterator(testDirectories, FileIterator.STARTING_DIRECTORIES, removeCommonPrefix));


        // write train vector file
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(new FileOutputStream(new File(malletTrainInstanceName)));
		oos.writeObject(trainInstances);
		oos.close();
        System.out.println("Train instances file is written. Number of instances : " + trainInstances.size());

        // write test vector file
        oos = new ObjectOutputStream(new FileOutputStream(new File(malletTestInstanceName)));
        oos.writeObject(testInstances);
        oos.close();
        System.out.println("Test instances file is written. Number of instances : " + testInstances.size());



        //Print Bag of Words Features
        //args 0: "--input"
        //args 1: input file directory
        //args 2: "--output"
        //args 3: output PreName
        //args 4: "--training-doc-num"
        //args 5: training document number for hurdle
        //args 6: "--weightedinput"
        //args 7: For Term Weight


        if(printTfBow1.value) {
            System.out.println("Printing TfBow1 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                int inputArgsNum = 4;
                String[] bowArgs = new String[inputArgsNum];
                bowArgs[0]="--input";

                if(tr=="train") bowArgs[1]=malletTrainInstanceName;
                if(tr=="test") bowArgs[1]=malletTestInstanceName;

                bowArgs[2]="--output";
                bowArgs[3]=dirTfBow1+tr;

                Vectors2SvmStyleBow.main(bowArgs);
            }
            String[] pipeArgs = new String[4]; // for Pipe generation.
            pipeArgs[0]="--input";
            pipeArgs[1]=dirTfBow1+strTrain+"_Svmlight.txt";
            pipeArgs[2]="--output";
            pipeArgs[3]=dirTfBow1+strTrain+suffixPipe;
            SvmLight2Vectors.main(pipeArgs);
        }

        if(printTfIdfBow2.value) {
            System.out.println("Printing TfIdfBow2 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                int inputArgsNum = 8;
                String[] bowArgs = new String[inputArgsNum];
                bowArgs[0]="--input";
                if(tr=="train") bowArgs[1]=malletTrainInstanceName;
                if(tr=="test") bowArgs[1]=malletTestInstanceName;

                bowArgs[2]="--output";
                bowArgs[3]=dirTfIdfBow2+tr;

                bowArgs[4]="--training-doc-num";
                bowArgs[5]=Integer.toString(trainInstances.size());

                bowArgs[6]="--weightedinput";
                bowArgs[7]=idfTermWeight;

                Vectors2SvmStyleBow.main(bowArgs);
            }
            String[] pipeArgs = new String[4]; // for Pipe generation.
            pipeArgs[0]="--input";
            pipeArgs[1]=dirTfIdfBow2+strTrain+"_Svmlight.txt";
            pipeArgs[2]="--output";
            pipeArgs[3]=dirTfIdfBow2+strTrain+"_pipe.mallet";
            SvmLight2Vectors.main(pipeArgs);
        }

        if(printBasicLdaBow3.value) {
            System.out.println("Printing BasicLdaBow3 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                int inputArgsNum = 8;
                String[] bowArgs = new String[inputArgsNum];
                bowArgs[0]="--input";
                if(tr=="train") bowArgs[1]=malletTrainInstanceName;
                if(tr=="test") bowArgs[1]=malletTestInstanceName;

                bowArgs[2]="--output";
                bowArgs[3]=dirBasicLdaBow3+tr;

                bowArgs[4]="--training-doc-num";
                bowArgs[5]=Integer.toString(trainInstances.size());

                bowArgs[6]="--weightedinput";
                bowArgs[7]=ldaTermWeight;

                Vectors2SvmStyleBow.main(bowArgs);
            }
            String[] pipeArgs = new String[4]; // for Pipe generation.
            pipeArgs[0]="--input";
            pipeArgs[1]=dirBasicLdaBow3+strTrain+"_Svmlight.txt";
            pipeArgs[2]="--output";
            pipeArgs[3]=dirBasicLdaBow3+strTrain+"_pipe.mallet";
            SvmLight2Vectors.main(pipeArgs);
        }

        if(printWtmBow4.value) {
            System.out.println("Printing WtmBow4 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                int inputArgsNum = 8;
                String[] bowArgs = new String[inputArgsNum];
                bowArgs[0]="--input";
                if(tr=="train") bowArgs[1]=malletTrainInstanceName;
                if(tr=="test") bowArgs[1]=malletTestInstanceName;

                bowArgs[2]="--output";
                bowArgs[3]=dirWtmBow4+tr;

                bowArgs[4]="--training-doc-num";
                bowArgs[5]=Integer.toString(trainInstances.size());

                bowArgs[6]="--weightedinput";
                bowArgs[7]=wtmTermWeight;

                Vectors2SvmStyleBow.main(bowArgs);
            }
            String[] pipeArgs = new String[4]; // for Pipe generation.
            pipeArgs[0]="--input";
            pipeArgs[1]=dirWtmBow4+strTrain+"_Svmlight.txt";
            pipeArgs[2]="--output";
            pipeArgs[3]=dirWtmBow4+strTrain+"_pipe.mallet";
            SvmLight2Vectors.main(pipeArgs);
        }

        if(printBwtmBow5.value) {
            System.out.println("Printing BwtmBow5 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                int inputArgsNum = 8;
                String[] bowArgs = new String[inputArgsNum];
                bowArgs[0]="--input";
                if(tr=="train") bowArgs[1]=malletTrainInstanceName;
                if(tr=="test") bowArgs[1]=malletTestInstanceName;

                bowArgs[2]="--output";
                bowArgs[3]=dirBwtmBow5+tr;

                bowArgs[4]="--training-doc-num";
                bowArgs[5]=Integer.toString(trainInstances.size());

                bowArgs[6]="--weightedinput";
                bowArgs[7]=bwtmTermWeight;

                Vectors2SvmStyleBow.main(bowArgs);
            }
            String[] pipeArgs = new String[4]; // for Pipe generation.
            pipeArgs[0]="--input";
            pipeArgs[1]=dirBwtmBow5+strTrain+"_Svmlight.txt";
            pipeArgs[2]="--output";
            pipeArgs[3]=dirBwtmBow5+strTrain+"_pipe.mallet";
            SvmLight2Vectors.main(pipeArgs);
        }


        //Print Topic Features
        //args 0: "--input"
        //args 1: input file1 (BOW) Always you have to load this file if you just use topic feature. (tr/ts)
        //args 2: input file2 (Topic Model)
        //args 3: "--inputfnum"
        //args 4: alphabet max size
        //args 5: topic number
        String strTopicNum = "50";
        //args 6: "--output"
        //args 7: always training vector
        //args 8: "--statsoutput"
        //args 9: always training vector
        //args 10: "--training-doc-num"
        //args 11: always training vector
        //args 12: "--print2ndOnly"
        //args 13: if true, print only topic feature. So this is mandatory for topic feature.
        int inputArgsNum = 14;

        if(printBasicLdaFeature1.value) {
            System.out.println("Printing BasicLdaFeature1 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] featureArgs = new String[inputArgsNum];
                featureArgs[0]="--input";
                featureArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                featureArgs[2]=ldaTopicmodel; //topic feature

                featureArgs[3]="--inputfnum";
                featureArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                featureArgs[5]=strTopicNum; //topic number. defualt is 50.

                featureArgs[6]="--output";
                featureArgs[7]= dirBasicLdaFeature1+tr+suffixSvmTxt; //training and test switched
                featureArgs[8]="--statsoutput";
                featureArgs[9]= dirBasicLdaFeature1+tr+suffixStatTxt; //training and test switched

                featureArgs[10]="--training-doc-num";
                featureArgs[11]=Integer.toString(trainInstances.size());

                featureArgs[12]="--print2ndOnly";
                featureArgs[13]="true";

                SvmLights2SvmLight.main(featureArgs);
            }
        }

        if(printWtmFeature2.value) {
            System.out.println("Printing WtmFeature2 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] featureArgs = new String[inputArgsNum];
                featureArgs[0]="--input";
                featureArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                featureArgs[2]=wtmTopicmodel; //topic feature

                featureArgs[3]="--inputfnum";
                featureArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                featureArgs[5]=strTopicNum; //topic number. defualt is 50.

                featureArgs[6]="--output";
                featureArgs[7]= dirWtmFeature2+tr+suffixSvmTxt; //training and test switched
                featureArgs[8]="--statsoutput";
                featureArgs[9]= dirWtmFeature2+tr+suffixStatTxt; //training and test switched

                featureArgs[10]="--training-doc-num";
                featureArgs[11]=Integer.toString(trainInstances.size());

                featureArgs[12]="--print2ndOnly";
                featureArgs[13]="true";

                SvmLights2SvmLight.main(featureArgs);
            }
        }

        if(printBwtmFeature3.value) {
            System.out.println("Printing BwtmFeature3 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] featureArgs = new String[inputArgsNum];
                featureArgs[0]="--input";
                featureArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                featureArgs[2]=bwtmTopicmodel; //topic feature **

                featureArgs[3]="--inputfnum";
                featureArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                featureArgs[5]=strTopicNum; //topic number. defualt is 50.

                featureArgs[6]="--output";
                featureArgs[7]= dirBwtmFeature3+tr+suffixSvmTxt; //training and test switched **
                featureArgs[8]="--statsoutput";
                featureArgs[9]= dirBwtmFeature3+tr+suffixStatTxt; //training and test switched **

                featureArgs[10]="--training-doc-num";
                featureArgs[11]=Integer.toString(trainInstances.size());

                featureArgs[12]="--print2ndOnly";
                featureArgs[13]="true";

                SvmLights2SvmLight.main(featureArgs);
            }
        }

        if(printIdfWtmFeature4.value) {
            System.out.println("Printing IdfWtmFeature4 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] featureArgs = new String[inputArgsNum];
                featureArgs[0]="--input";
                featureArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                featureArgs[2]=idfWtmTopicmodel; //topic feature **

                featureArgs[3]="--inputfnum";
                featureArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                featureArgs[5]=strTopicNum; //topic number. defualt is 50.

                featureArgs[6]="--output";
                featureArgs[7]= dirIdfWtmFeature4+tr+suffixSvmTxt; //training and test switched **
                featureArgs[8]="--statsoutput";
                featureArgs[9]= dirIdfWtmFeature4+tr+suffixStatTxt; //training and test switched **

                featureArgs[10]="--training-doc-num";
                featureArgs[11]=Integer.toString(trainInstances.size());

                featureArgs[12]="--print2ndOnly";
                featureArgs[13]="true";

                SvmLights2SvmLight.main(featureArgs);
            }
        }

        if(printIdfBwtmFeature5.value) {
            System.out.println("Printing IdfBwtmFeature5 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] featureArgs = new String[inputArgsNum];
                featureArgs[0]="--input";
                featureArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                featureArgs[2]=idfBwtmTopicmodel; //topic feature **

                featureArgs[3]="--inputfnum";
                featureArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                featureArgs[5]=strTopicNum; //topic number. defualt is 50.

                featureArgs[6]="--output";
                featureArgs[7]= dirIdfBwtmFeature5+tr+suffixSvmTxt; //training and test switched **
                featureArgs[8]="--statsoutput";
                featureArgs[9]= dirIdfBwtmFeature5+tr+suffixStatTxt; //training and test switched **

                featureArgs[10]="--training-doc-num";
                featureArgs[11]=Integer.toString(trainInstances.size());

                featureArgs[12]="--print2ndOnly";
                featureArgs[13]="true";

                SvmLights2SvmLight.main(featureArgs);
            }
        }


        // Topic and BOW Mixed Features
        //Mix.1. Basic TF + Basic LDA
//        --use-pipe-from
//        data/corpus/20news-forMyExp/tr20/train_svmlight_forpipe.mallet

        inputArgsNum = 16;
        if(printTfBasicldaMix1.value) {
            System.out.println("Printing TfBasicldaMix1 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfBow1+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=ldaTopicmodel; //topic feature

                mixArgs[3]="--inputfnum";
                mixArgs[4]= String.valueOf(trainInstances.getAlphabet().size());
                mixArgs[5]= strTopicNum; //topic number. defualt is 50.

                mixArgs[6]="--output";
                mixArgs[7]= dirTfBasicldaMix1+tr+suffixSvmTxt; //training and test switched
                mixArgs[8]="--statsoutput";
                mixArgs[9]= dirTfBasicldaMix1+tr+suffixStatTxt; //training and test switched

                mixArgs[10]="--training-doc-num";
                mixArgs[11]=Integer.toString(trainInstances.size());

                mixArgs[12]="--print2ndOnly";
                mixArgs[13]="false";

                mixArgs[14]="--use-pipe-from";
                mixArgs[15]=dirTfBow1+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);


            }
        }

        inputArgsNum = 9;
        //Mix.2. tf*idf + Basic LDA
        if(printIdfBasicldaMix2.value) {
            System.out.println("Printing IdfBasicldaMix2 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfIdfBow2+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=ldaTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirIdfBasicldaMix2+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirIdfBasicldaMix2+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirTfIdfBow2+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.3. wtm + wtm
        if(printWtmWtmMix3.value) {
            System.out.println("Printing WtmWtmMix3 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirWtmBow4+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=wtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirWtmWtmMix3+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirWtmWtmMix3+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirWtmBow4+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.4. bwtm + bwtm
        if(printBwtmBwtmMix4.value) {
            System.out.println("Printing BwtmBwtmMix4 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirBwtmBow5+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=bwtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirBwtmBwtmMix4+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirBwtmBwtmMix4+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirBwtmBow5+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.5. wtm + bwtm
        if(printWtmBwtmMix5.value) {
            System.out.println("Printing WtmBwtmMix5 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirWtmBow4+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=bwtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirWtmBwtmMix5+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirWtmBwtmMix5+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirWtmBow4+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.6. bwtm + wtm
        if(printBwtmWtmMix6.value) {
            System.out.println("Printing BwtmWtmMix6 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirBwtmBow5+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=wtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirBwtmWtmMix6+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirBwtmWtmMix6+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirBwtmBow5+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.7. idf + idfwtm
        if(printIdfIdfWtmMix7.value) {
            System.out.println("Printing IdfIdfWtmMix7 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfIdfBow2+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=idfWtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirIdfIdfWtmMix7+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirIdfIdfWtmMix7+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirTfIdfBow2+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.8. idf + idfbwtm
        if(printIdfIdfBwtmMix8.value) {
            System.out.println("Printing IdfIdfBwtmMix8 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfIdfBow2+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=idfBwtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirIdfIdfBwtmMix8+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirIdfIdfBwtmMix8+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirTfIdfBow2+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.9. idf + wtm
        if(printIdfWtmMix9.value) {
            System.out.println("Printing IdfWtmMix9 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfIdfBow2+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=wtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirIdfWtmMix9+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirIdfWtmMix9+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirTfIdfBow2+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        //Mix.10. idf + bwtm
        if(printIdfBwtmMix10.value) {
            System.out.println("Printing IdfBwtmMix10 start at " + dateFormat.format(Calendar.getInstance().getTime()));

            for(String tr:strTrTs){
                String[] mixArgs = new String[inputArgsNum];
                mixArgs[0]="--input";
                mixArgs[1]=dirTfIdfBow2+tr+suffixSvmTxt; //training and test switched
                mixArgs[2]=bwtmTopicmodel; //topic feature

                mixArgs[3]="--output";
                mixArgs[4]= dirIdfBwtmMix10+tr+suffixSvmTxt; //training and test switched
                mixArgs[5]="--statsoutput";
                mixArgs[6]= dirIdfBwtmMix10+tr+suffixStatTxt; //training and test switched

                mixArgs[7]="--use-pipe-from";
                mixArgs[8]=dirTfIdfBow2+strTrain+suffixPipe;

                SvmLights2SvmLight.main(mixArgs);
            }
        }

        System.out.println("All Jobs finished at "+dateFormat.format(Calendar.getInstance().getTime()));

	}

}
