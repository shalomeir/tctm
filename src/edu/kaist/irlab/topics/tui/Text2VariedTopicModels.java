/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.topics.tui;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.InstanceList;
import cc.mallet.util.CharSequenceLexer;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;
import cc.mallet.util.Strings;
import edu.kaist.irlab.sglee.util.MyFileWriter;
import edu.kaist.irlab.textcontrol.TermWeight;
import edu.kaist.irlab.textcontrol.Vectors2IdfWeight;
import edu.kaist.irlab.topics.WeightedTopicModel;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
* Convert document files into vectors (a persistent instance list) based on Text2Vector Mallet Source.
* With this, transform text corpus to varied Topic Model and Term Weight Object at once.
* This topic model support Weighted Topic Model, Balance Weighted Topic Model and IDF Weighted Topic Model and General LDA.
* There are many option such as using Asymmetric Dirichlet alpha prior.
*
* @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
* @author Seonggyu Lee <a href="mailto:inflexer@gmail.com">inflexer@gmail.com</a>
*/

public class Text2VariedTopicModels {

	private static Logger logger = MalletLogger.getLogger(Text2VariedTopicModels.class.getName());

    //Main input Text directories.
    static CommandOption.SpacedStrings classDirs =	new CommandOption.SpacedStrings
            (Text2VariedTopicModels.class, "input", "DIR...", true, null,
                    "The directories containing text files to be classified, one directory per class", null);

    //Output directory.
    static CommandOption.String outputDir = new CommandOption.String
            (Text2VariedTopicModels.class, "output-dir", "FILE", true, null,
                    "Write the topic model results to this location.", null);

    //Basic LDA.
    static CommandOption.Boolean doBasicLDA = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "do-lda", "true|false", true, true,
                    "Execute basic latent dirichlet allocation.", null);

    //Weighted Topic Model.
    static CommandOption.Boolean doVarianceWTM = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "do-wtm", "true|false", true, false,
                    "Execute Weighted Topic Model by Variance term weight.", null);

    //Balance Weighted Topic Model.
    static CommandOption.Boolean doVarianceBWTM = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "do-bwtm", "true|false", true, false,
                    "Execute Balance Weighted Topic Model by Variance term weight.", null);


    //Balance Weighted Topic Model by IDF Term Weight.
    static CommandOption.Boolean printIdfTermWeight = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "print-idf-weight", "true|false", true, true,
                    "Print IDF Term Weight object and list.", null);

    //Weighted Topic Model by IDF Term Weight.
    static CommandOption.Boolean doIdfWTM = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "do-idf-wtm", "true|false", true, false,
                    "Execute Weighted Topic Model by Variance term weight.", null);

    //Balance Weighted Topic Model by IDF Term Weight.
    static CommandOption.Boolean doIdfBWTM = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "do-idf-bwtm", "true|false", true, false,
                    "Execute Balance Weighted Topic Model by Variance term weight.", null);

    //IDF Term Weight Existing File Name
    static CommandOption.String exIdfTermWeightFile = new CommandOption.String
            (Text2VariedTopicModels.class, "idf-termweight-file", "FILE", true, null,
                    "Existing IDF Term Weight File Location.", null);

    //Option For Text2VariedTopicModels
    //For Weighting Start Iteration Number.
    static CommandOption.Integer weightingBurnIn = new CommandOption.Integer
            (Text2VariedTopicModels.class, "weighting-burn-in", "INTEGER", true, 100,
                    "The number of iterations to run before first Term Weighting. The default number is 100.", null);

    //For Dirichlet hyperparameters Alpha Prior.
    static CommandOption.Integer optimizeBurnIn = new CommandOption.Integer
            (Text2VariedTopicModels.class, "optimize-burn-in", "INTEGER", true, 200,
                    "The number of iterations to run before first estimating dirichlet hyperparameters.", null);

    //Remover Stopwords. This is experiment for Basic LDA
    static CommandOption.Boolean removeStopWords = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "remove-stopwords", "[TRUE|FALSE]", true, false,
                    "If true, remove a default list of common English \"stop words\" from the text.", null);


    static CommandOption.Integer numTopics = new CommandOption.Integer
            (Text2VariedTopicModels.class, "num-topics", "INTEGER", true, 50,
                    "The number of topics to fit.", null);

    static CommandOption.Double alpha = new CommandOption.Double
            (Text2VariedTopicModels.class, "alpha", "DECIMAL", true, 50.0,
                    "Alpha parameter: smoothing over topic distribution.",null);

    //Empirical Likelihood Test
    static CommandOption.String elTestingFile = new CommandOption.String
            (Text2VariedTopicModels.class, "el-testing-file", "FILE", false, null,
                    "This is used for Empirical Likelihood test.", null);


    //Not useful option for this class.
    static CommandOption.Boolean keepSequence = new CommandOption.Boolean
            (Text2VariedTopicModels.class, "keep-sequence", "[TRUE|FALSE]", true, true,
                    "If true, final data will be a FeatureSequence rather than a FeatureVector.", null);


	static CommandOption.File usePipeFromVectorsFile = new CommandOption.File
		(Text2VariedTopicModels.class, "use-pipe-from", "FILE", true, new File("text.vectors"),
		 "Use the pipe and alphabets from a previously created vectors file. " +
		 "Allows the creation, for example, of a test set of vectors that are " +
		 "compatible with a previously created set of training vectors", null);

	static CommandOption.Boolean preserveCase = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "preserve-case", "[TRUE|FALSE]", false, false,
		 "If true, do not force all strings to lowercase.", null);

	static CommandOption.File stoplistFile = new CommandOption.File
		(Text2VariedTopicModels.class, "stoplist-file", "FILE", true, null,
		 "Instead of the default list, read stop words from a file, one per line. Implies --remove-stopwords", null);

	static CommandOption.File extraStopwordsFile = new CommandOption.File
		(Text2VariedTopicModels.class, "extra-stopwords", "FILE", true, null,
		 "Read whitespace-separated words from this file, and add them to either\n" +
		 "   the default English stoplist or the list specified by --stoplist-file.", null);

	static CommandOption.Boolean skipHeader = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "skip-header", "[TRUE|FALSE]", false, false,
		 "If true, in each document, remove text occurring before a blank line."+
		 "  This is useful for removing email or UseNet headers", null);

	static CommandOption.Boolean skipHtml = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "skip-html", "[TRUE|FALSE]", false, false,
		 "If true, remove text occurring inside <...>, as in HTML or SGML.", null);

	static CommandOption.Boolean binaryFeatures = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "binary-features", "[TRUE|FALSE]", false, false,
		 "If true, features will be binary.", null);

	static CommandOption.IntegerArray gramSizes = new CommandOption.IntegerArray
		(Text2VariedTopicModels.class, "gram-sizes", "INTEGER,[INTEGER,...]", true, new int[] {1},
		 "Include among the features all n-grams of sizes specified.  "+
		 "For example, to get all unigrams and bigrams, use --gram-sizes 1,2.  "+
		 "This option occurs after the removal of stop words, if removed.", null);

	static CommandOption.Boolean keepSequenceBigrams = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "keep-sequence-bigrams", "[TRUE|FALSE]", false, false,
		 "If true, final data will be a FeatureSequenceWithBigrams rather than a FeatureVector.", null);

	static CommandOption.Boolean saveTextInSource = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "save-text-in-source", "[TRUE|FALSE]", false, false,
		 "If true, save original text of document in source.", null);

	static CommandOption.ObjectFromBean stringPipe = new CommandOption.ObjectFromBean
		(Text2VariedTopicModels.class, "string-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a CharSequence", null);

	static CommandOption.ObjectFromBean tokenPipe = new CommandOption.ObjectFromBean
		(Text2VariedTopicModels.class, "token-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a TokenSequence", null);

	static CommandOption.ObjectFromBean featureVectorPipe = new CommandOption.ObjectFromBean
		(Text2VariedTopicModels.class, "fv-pipe", "Pipe constructor",	true, null,
		 "Java code for the constructor of a Pipe to be run as soon as input becomes a FeatureVector", null);

	static CommandOption.String encoding = new CommandOption.String
		(Text2VariedTopicModels.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
		 "Character encoding for input file", null);

	static CommandOption.String tokenRegex = new CommandOption.String
		(Text2VariedTopicModels.class, "token-regex", "REGEX", true, CharSequenceLexer.LEX_ALPHA.toString(),
		 "Regular expression used for tokenization.\n" +
		 "   Example: \"[\\p{L}\\p{N}_]+|[\\p{P}]+\" (unicode letters, numbers and underscore OR all punctuation) ", null);

	static CommandOption.Boolean printOutput = new CommandOption.Boolean
		(Text2VariedTopicModels.class, "print-output", "[TRUE|FALSE]", false, false,
		 "If true, print a representation of the processed data\n" +
		 "   to standard output. This option is intended for debugging.", null);


	public static void main (String[] args) throws IOException, ClassNotFoundException {
		// Process the command-line options
		CommandOption.setSummary (Text2VariedTopicModels.class,
								  "A tool for creating instance lists of Topic Features and Term Weight from text documents.\n");
		CommandOption.process (Text2VariedTopicModels.class, args);

        // All Output File Name is here.
        // String argument name is fixed by below.
        String outputRealDir = outputDir.value+"/"+"VTopicModel_Wi"+weightingBurnIn.value()+"_Di"+optimizeBurnIn.value();
        if(removeStopWords.value()) outputRealDir = outputRealDir + "_RemoveStopwords";
        String textInputInstanceName = outputRealDir+"/TextFeatureVector.mallet";

        //Idf File Name.
        String idfTermWeightObjectFileName = outputRealDir+"/Idf_TermWeightObject.ser";
        String idfTermWeightListFileName = outputRealDir+"/Idf_TermWeightList.txt";
        if(exIdfTermWeightFile.value!=null) idfTermWeightObjectFileName=exIdfTermWeightFile.value;

        //Basic LDA Output PreFile Name.
        String basicLdaPreName = outputRealDir + "/BasicLDA";
        if(doBasicLDA.value) {
            MyFileWriter.directoryConfirmAndMake(basicLdaPreName);
            basicLdaPreName = basicLdaPreName + "/lda";
        }

        //Weighted Topic Model Output PreFile Name.
        String varianceWtmPreName = outputRealDir + "/WTM";
        if(doVarianceWTM.value) {
            MyFileWriter.directoryConfirmAndMake(varianceWtmPreName);
            varianceWtmPreName = varianceWtmPreName + "/wtm";
        }

        //Weighted Topic Model Output PreFile Name.
        String varianceBwtmPreName = outputRealDir + "/BWTM";
        if(doVarianceBWTM.value) {
            MyFileWriter.directoryConfirmAndMake(varianceBwtmPreName);
            varianceBwtmPreName = varianceBwtmPreName + "/bwtm";
        }

        //IDF Weighted Topic Model Output PreFile Name.
        String idfWtmPreName = outputRealDir + "/IdfWTM";
        if(doIdfWTM.value) {
            MyFileWriter.directoryConfirmAndMake(idfWtmPreName);
            idfWtmPreName = idfWtmPreName + "/idfWtm";
        }

        //Weighted Topic Model Output PreFile Name.
        String idfBwtmPreName = outputRealDir + "/IdfBWTM";
        if(doIdfBWTM.value) {
            MyFileWriter.directoryConfirmAndMake(idfBwtmPreName);
            idfBwtmPreName = idfBwtmPreName + "/idfBwtm";
        }

        //Time print
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("Start time : "+dateFormat.format(calendar.getTime()));

        //String[] classDirs = CommandOption.process (Text2Vectors.class, args);

		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(Text2VariedTopicModels.class).printUsage(false);
			System.exit (-1);
		}
		if (classDirs.value.length == 0) {
			throw new IllegalArgumentException ("You must include --input DIR1 DIR2 ...' in order to specify a " +
								"list of directories containing the documents for each class.");
		}

		// Remove common prefix from all the input class directories
		int commonPrefixIndex = Strings.commonPrefixIndex (classDirs.value);

		logger.info ("Labels = ");
		File[] directories = new File[classDirs.value.length];
		for (int i = 0; i < classDirs.value.length; i++) {
			directories[i] = new File (classDirs.value[i]);
			if (commonPrefixIndex < classDirs.value.length) {
				logger.info ("   "+classDirs.value[i].substring(commonPrefixIndex));
			}
			else {
				logger.info ("   "+classDirs.value[i]);
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
			if (! (keepSequence.value || keepSequenceBigrams.value)) {
				pipeList.add( new FeatureSequence2AugmentableFeatureVector(binaryFeatures.value) );
			}

			// Allow users to specify an arbitrary Pipe object that operates on
			//  feature vectors.
			if (featureVectorPipe.wasInvoked()) {
				pipeList.add( (Pipe) featureVectorPipe.value );
			}

			if (printOutput.value) {
				pipeList.add(new PrintInputAndTarget());
			}

			instancePipe = new SerialPipes(pipeList);

		}

        InstanceList instances = new InstanceList (instancePipe);

		boolean removeCommonPrefix = true;
		instances.addThruPipe (new FileIterator(directories, FileIterator.STARTING_DIRECTORIES, removeCommonPrefix));

        //Making directory for all output files.
        MyFileWriter.directoryConfirmAndMake(outputRealDir);


        // write vector file
		ObjectOutputStream oos;
		oos = new ObjectOutputStream(new FileOutputStream(new File(textInputInstanceName)));
		oos.writeObject(instances);
		oos.close();
        System.out.println("Input instances file is written. Number of instances : " + instances.size());


        // Rewrite vector file used as source of pipe in case we changed the alphabet. (Option)
		if (usePipeFromVectorsFile.wasInvoked()) {
			logger.info(" rewriting previous instance list, with ID = " + previousInstanceList.getPipe().getInstanceId());
			oos = new ObjectOutputStream(new FileOutputStream(usePipeFromVectorsFile.value));
			oos.writeObject(previousInstanceList);
			oos.close();
		}

        //If excute IDF Weighting, first of all making IDF Term Weight. This output used for Topic Model and also Weighted BOW Features.
        if(printIdfTermWeight.value||(exIdfTermWeightFile.value==null&&(doIdfWTM.value||doIdfBWTM.value))){
            calendar = Calendar.getInstance();
            System.out.println("Idf Term Weight Printing start at "+dateFormat.format(calendar.getTime()));
            System.out.println("Calculating Inverse Document Frequency per each dictionary type ...");
            TermWeight idfTermWeight = Vectors2IdfWeight.getIdfTermWeight(instances);
            MyFileWriter.writeObject(idfTermWeight,idfTermWeightObjectFileName);
            Vectors2IdfWeight.writeTermWeightList(idfTermWeight,idfTermWeightListFileName);
            System.out.println("Idf Term Weight files are written. Type number : "+ idfTermWeight.typeAlphabet.size());
        }

        //All Topic Modeling Start.

        //args 0: all files vector. This vector is maded by Text2Vectors or Text2WeightedTopicModel
        //args 1: number of topic
        //args 2: alpha Sum
        //args 3: Variance Term Weighting start over than this iteration
        //args 4: pre name of output file name
        //args 5: This number used for Asymmetric Dirichlet alpha prior
        //args 6: True/False for Balance Weighted Topic Model
        //args 7: Test file name and location for Empirical Likelihood.
        //args 8: IDF Term Weight File Location

        //Execution Basic Latent Dirichlet Allocation.
        if(doBasicLDA.value){
            calendar = Calendar.getInstance();
            System.out.println("Basic LDA start at "+dateFormat.format(calendar.getTime()));

            int inputArgsNum = 9;
            String[] tmArgs = new String[inputArgsNum];
            tmArgs[0]=textInputInstanceName; //Instances
            tmArgs[1]=Integer.toString(numTopics.value); //Topic K Number.
            tmArgs[2]=Double.toString(alpha.value);; //AlphaSum value. Usually same as numTopic.
            tmArgs[3]="9999"; // Basic LDA must not use weighting.
            tmArgs[4]=basicLdaPreName; //This is not final output name.
            tmArgs[5]=Integer.toString(optimizeBurnIn.value); //Real Basic must not use Asymmetric. So Real Basic LDA should set up this 9999.
            tmArgs[6]="false"; //for Balance.
            tmArgs[7]=elTestingFile.value; //For Empirical Likelihood. If 'null', no empirical likelihood.
            tmArgs[8]=null; //This is used for Idf Weighting.

            WeightedTopicModel.execution(tmArgs);
        }

        //Execution Basic Latent Dirichlet Allocation.
        if(doVarianceWTM.value){
            calendar = Calendar.getInstance();
            System.out.println("Weighted Topic Modeling start at "+dateFormat.format(calendar.getTime()));

            int inputArgsNum = 9;
            String[] tmArgs = new String[inputArgsNum];
            tmArgs[0]=textInputInstanceName; //Instances
            tmArgs[1]=Integer.toString(numTopics.value); //Topic K Number.
            tmArgs[2]=Double.toString(alpha.value);; //AlphaSum value. Usually same as numTopic.
            tmArgs[3]=Integer.toString(weightingBurnIn.value); // Weighting Start since this Iteration number.
            tmArgs[4]=varianceWtmPreName; //This is not final output name.
            tmArgs[5]=Integer.toString(optimizeBurnIn.value); //Real Basic must not use Asymmetric. So Real Basic LDA should set up this 9999.
            tmArgs[6]="false"; //for Balance.
            tmArgs[7]=elTestingFile.value; //For Empirical Likelihood. If 'null', no empirical likelihood.
            tmArgs[8]=null; //This is used for Idf Weighting.

            WeightedTopicModel.execution(tmArgs);
        }


        //Execution Basic Latent Dirichlet Allocation.
        if(doVarianceBWTM.value){
            calendar = Calendar.getInstance();
            System.out.println("Balance Weighted Topic Modeling start at "+dateFormat.format(calendar.getTime()));

            int inputArgsNum = 9;
            String[] tmArgs = new String[inputArgsNum];
            tmArgs[0]=textInputInstanceName; //Instances
            tmArgs[1]=Integer.toString(numTopics.value); //Topic K Number.
            tmArgs[2]=Double.toString(alpha.value);; //AlphaSum value. Usually same as numTopic.
            tmArgs[3]=Integer.toString(weightingBurnIn.value); // Weighting Start since this Iteration number.
            tmArgs[4]=varianceBwtmPreName; //This is not final output name.
            tmArgs[5]=Integer.toString(optimizeBurnIn.value); //Real Basic must not use Asymmetric. So Real Basic LDA should set up this 9999.
            tmArgs[6]="true"; //for Balance.
            tmArgs[7]=elTestingFile.value; //For Empirical Likelihood. If 'null', no empirical likelihood.
            tmArgs[8]=null; //This is used for Idf Weighting.

            WeightedTopicModel.execution(tmArgs);
        }


        //Execution Basic Latent Dirichlet Allocation.
        if(doIdfWTM.value){
            calendar = Calendar.getInstance();
            System.out.println("IDF Term Weighted Topic Modeling start at "+dateFormat.format(calendar.getTime()));

            int inputArgsNum = 9;
            String[] tmArgs = new String[inputArgsNum];
            tmArgs[0]=textInputInstanceName; //Instances
            tmArgs[1]=Integer.toString(numTopics.value); //Topic K Number.
            tmArgs[2]=Double.toString(alpha.value);; //AlphaSum value. Usually same as numTopic.
            tmArgs[3]=Integer.toString(weightingBurnIn.value); // Weighting Start since this Iteration number.
            tmArgs[4]=idfWtmPreName; //This is not final output name.
            tmArgs[5]=Integer.toString(optimizeBurnIn.value); //Real Basic must not use Asymmetric. So Real Basic LDA should set up this 9999.
            tmArgs[6]="false"; //for Balance.
            tmArgs[7]=elTestingFile.value; //For Empirical Likelihood. If 'null', no empirical likelihood.
            tmArgs[8]=idfTermWeightObjectFileName; //This is used for Idf Weighting.

            WeightedTopicModel.execution(tmArgs);
        }


        //Execution Basic Latent Dirichlet Allocation.
        if(doIdfBWTM.value){
            calendar = Calendar.getInstance();
            System.out.println("IDF Term Balance Weighted Topic Modeling start at "+dateFormat.format(calendar.getTime()));

            int inputArgsNum = 9;
            String[] tmArgs = new String[inputArgsNum];
            tmArgs[0]=textInputInstanceName; //Instances
            tmArgs[1]=Integer.toString(numTopics.value); //Topic K Number.
            tmArgs[2]=Double.toString(alpha.value);; //AlphaSum value. Usually same as numTopic.
            tmArgs[3]=Integer.toString(weightingBurnIn.value); // Weighting Start since this Iteration number.
            tmArgs[4]=idfBwtmPreName; //This is not final output name.
            tmArgs[5]=Integer.toString(optimizeBurnIn.value); //Real Basic must not use Asymmetric. So Real Basic LDA should set up this 9999.
            tmArgs[6]="true"; //for Balance.
            tmArgs[7]=elTestingFile.value; //For Empirical Likelihood. If 'null', no empirical likelihood.
            tmArgs[8]=idfTermWeightObjectFileName; //This is used for Idf Weighting.

            WeightedTopicModel.execution(tmArgs);
        }

        calendar = Calendar.getInstance();
        System.out.println("All Jobs finished at "+dateFormat.format(calendar.getTime()));

	}

}
