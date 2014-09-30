/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */



package edu.kaist.irlab.classify.tui;

import cc.mallet.types.*;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Writing SVM Light style text.
   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class Vectors2TermContributedSvmStyleInfo
{
	private static Logger logger = MalletLogger.getLogger(Vectors2TermContributedSvmStyleInfo.class.getName());

	static CommandOption.File inputFile = new CommandOption.File
	(Vectors2TermContributedSvmStyleInfo.class, "input", "FILE", true, new File("-"),
	 "Read the instance list from this file; Using - indicates stdin.", null);

    static CommandOption.File weightedinputFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "weightedinput", "FILE", true, new File("-"),
                    "Read the serialized typetopicweight double[] array from this file; Using - indicates stdin.", null);

    static CommandOption.File weightedinputalphabetFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "weightedinputalphabet", "FILE", true, new File("-"),
                    "Read the serialized alphabet Alphabet object from this file; Using - indicates stdin.", null);

    static CommandOption.File outputFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "output", "FILE", true, new File("text.vectors"),
                    "Write the SVM Style instance list to this file; Using - indicates stdout.", null);

    static CommandOption.File weightedoutputFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "weightedoutput", "FILE", true, new File("text.vectors"),
                    "Write the SVM Style instance list to this file, and this is weighted type; Using - indicates stdout.", null);

    static CommandOption.File featureFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "featureoutput", "FILE", true, new File("text.vectors"),
                    "Write the feature dictonary to this file; Using - indicates stdout.", null);

    static CommandOption.File statsFile = new CommandOption.File
            (Vectors2TermContributedSvmStyleInfo.class, "statsoutput", "FILE", true, new File("text.vectors"),
                    "Write the statistics for this instance lists to this file; Using - indicates stdout.", null);

	static CommandOption.Integer printInfogain = new CommandOption.Integer
	(Vectors2TermContributedSvmStyleInfo.class, "print-infogain", "N", false, 0,
	 "Print top N words by information gain, sorted.", null);

	static CommandOption.Boolean printLabels = new CommandOption.Boolean
	(Vectors2TermContributedSvmStyleInfo.class, "print-labels", "[TRUE|FALSE]", false, false,
	 "Print class labels known to instance list, one per line.", null);

    static CommandOption.Boolean printFileNames = new CommandOption.Boolean
            (Vectors2TermContributedSvmStyleInfo.class, "print-FileNames", "[TRUE|FALSE]", false, true,
                    "Print file name with start character # after feature list.", null);

	static CommandOption.String printMatrix = new CommandOption.String
	(Vectors2TermContributedSvmStyleInfo.class, "print-matrix", "STRING", false, "sic",
	 "Print word/document matrix in the specified format (a|s)(b|i)(n|w|c|e), for (all vs. sparse), (binary vs. integer), (number vs. word vs. combined vs. empty)", null)
	{
		public void parseArg(java.lang.String arg) {

			if (arg == null) arg = this.defaultValue;
			//System.out.println("pa arg=" + arg);

			// sanity check the raw printing options (a la Rainbow)
			char c0 = arg.charAt(0);
			char c1 = arg.charAt(1);
			char c2 = arg.charAt(2);

			if (arg.length() != 3 ||
			        (c0 != 's' && c0 != 'a') ||
			        (c1 != 'b' && c1 != 'i') ||
			        (c2 != 'n' && c2 != 'w' && c2 != 'c' && c2 != 'e')) {
				throw new IllegalArgumentException("Illegal argument = " + arg + " in --print-matrix=" +arg);
			}

			value = arg;
		}
	};

    static CommandOption.String encoding = new CommandOption.String
            (Vectors2TermContributedSvmStyleInfo.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
                    "Character encoding for input file", null);

    static CommandOption.Integer trainingDocNum = new CommandOption.Integer
            (Vectors2TermContributedSvmStyleInfo.class, "training-doc-num", "integer", true, 0,
                    "Training Doucment Number; Using for ratio about topic model feature and word feature", null);

	public static void main (String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		// Process the command-line options
		CommandOption.setSummary (Vectors2TermContributedSvmStyleInfo.class,
								  "A tool for printing information about instance lists of feature vectors.");
		CommandOption.process (Vectors2TermContributedSvmStyleInfo.class, args);

		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(Vectors2TermContributedSvmStyleInfo.class).printUsage(false);
			System.exit (-1);
		}
		if (false && !inputFile.wasInvoked()) {
			System.err.println ("You must specify an input instance list, with --input.");
			System.exit (-1);
		}

        // Write classifications to the output file
        PrintStream out = null;
        if (outputFile.value.toString().equals ("-")) {
            out = System.out;
        }
        else {
            out = new PrintStream(outputFile.value, encoding.value);
        }

        // Write classifications to the output file
        PrintStream weightedOut = null;
        if (weightedoutputFile.value.toString().equals ("-")) {
            weightedOut = System.out;
        }
        else {
            weightedOut = new PrintStream(weightedoutputFile.value, encoding.value);
        }

        // Write classifications to the output file
        PrintStream featureOut = null;
        if (featureFile.value.toString().equals ("-")) {
            featureOut = System.out;
        }
        else {
            featureOut = new PrintStream(featureFile.value, encoding.value);
        }

        // Write classifications to the output file
        PrintStream statsOut = null;
        if (statsFile.value.toString().equals ("-")) {
            statsOut = System.out;
        }
        else {
            statsOut = new PrintStream(statsFile.value, encoding.value);
        }

		// Read the InstanceList
		InstanceList instances = InstanceList.load (inputFile.value);

        //Read the searialized typetopicweight double[] array
        double[] typeTopicWeight = readStopwordsObject(weightedinputFile.value);

        //Read the searialized topic model dictionary alphabet Alphabet object
        Alphabet weightedTopicAlphabet = readAlphabetObject(weightedinputalphabetFile.value);


        if (printLabels.value) {
			Alphabet labelAlphabet = instances.getTargetAlphabet ();
			for (int i = 0; i < labelAlphabet.size(); i++) {
				System.out.println (labelAlphabet.lookupObject (i));
			}
			System.out.print ("\n");
		}

        if (statsFile.value.exists()) {
            StringBuilder output = new StringBuilder();
            output.append("#This is a statistics file for instance List file extrated same time. : "+outputFile.value.toString()+"\n");
            int numInstances = instances.size();
            Alphabet labelAlphabet = instances.getTargetAlphabet ();
            int numClasses = labelAlphabet.size();
            int numFeatures = instances.getDataAlphabet().size();

            output.append("numInstances : "+numInstances+"\n");
            output.append("numClasses : "+numClasses+"\n");
            output.append("numFeatures : "+numFeatures+"\n");
            output.append("#label statistics(Labelnum\tLabelname\tLabelInstancesNum)"+"\n");

            //label 별 instance number 계산하기
            Integer[] labelStats = new Integer[labelAlphabet.size()];
            Arrays.fill(labelStats, 0);
            for (int i = 0; i < instances.size(); i++) {
                Instance instance = instances.get(i);
                FeatureVector fv = (FeatureVector) instance.getData();

                Label target = (Label) instance.getTarget();
                labelStats[target.getIndex()]++;
            }

            int fullNumInstances = 0;
            for (int i = 0; i < labelAlphabet.size(); i++) {
                int labelInstancesNum = labelStats[i];
                output.append((Integer)(i+1)+"\t"+labelAlphabet.lookupObject (i)+"\t"+labelInstancesNum+"\n");
                fullNumInstances += labelInstancesNum;
            }

            output.append("*Full Label Instatnce List Number : "+fullNumInstances);
            statsOut.println(output);

            if (! statsFile.value.toString().equals ("-")) {
                statsOut.close();
            }
        }

		if (featureFile.value.exists()) {
            StringBuilder output = new StringBuilder();
            output.append("#This is feature List and Number of lines are same as feature word. So Next Line word's feature number is 1."); //dictionary[0]=1
            featureOut.println(output);

            Alphabet alphabet = instances.getDataAlphabet();
			for (int i = 0; i < alphabet.size(); i++) {
                output = new StringBuilder();
                output.append(alphabet.lookupObject(i));
                featureOut.println(output);
			}
            if (! featureFile.value.toString().equals ("-")) {
                featureOut.close();
            }
		}

		if (printInfogain.value > 0) {
			InfoGain ig = new InfoGain (instances);
			for (int i = 0; i < printInfogain.value; i++) {
				System.out.println (""+i+" "+ig.getObjectAtRank(i));
			}
			System.out.print ("\n");
		}

        if (outputFile.value.exists()) {
            printSVMStyleList(instances, out);
        }

        if (weightedoutputFile.value.exists()) {
            int trainingDocNum = 100;
            printWeightedSVMStyleList(instances, weightedOut, typeTopicWeight,weightedTopicAlphabet); // training document 수에 따라 confidence hurdle을 다르게 두어야 하기 에 training 문서수가 중요함
        }

		if (printMatrix.wasInvoked()) {
			printInstanceList(instances, printMatrix.value);

		}
	}


    /** print an instance list according to the SVM Style format */
    private static void printSVMStyleList(InstanceList instances, PrintStream out) {
        int numInstances = instances.size();
        Alphabet labelAlphabet = instances.getTargetAlphabet ();
        int numClasses = labelAlphabet.size();
        int numFeatures = instances.getDataAlphabet().size();
        Alphabet dataAlphabet = instances.getDataAlphabet();
        double[] counts = new double[numFeatures];
        double count;

        StringBuilder output = new StringBuilder();
        output.append("#This is svm light style Instance List. First column is a label(=target class, 0 means hidden.) Feature size : "+numFeatures+".  Number of instances : "+numInstances+"."); //dictionary[0]=1
        out.println(output);

        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);

            output = new StringBuilder();// line 출력용
            if (instance.getData() instanceof FeatureVector) {
                FeatureVector fv = (FeatureVector) instance.getData ();

//                output.append(instance.getTarget());//text 용
                Label target = (Label) instance.getTarget();
                output.append((Integer)(target.getIndex()+1));//number (+1 은 실제 label number는 1부터 시작하므로)

                  // Sparse: Print features with non-zero values only.
                for (int l = 0; l < fv.numLocations(); l++) {
                      int fvi = fv.indexAtLocation(l);
                      output.append(" "+(Integer)(fvi+1)+":"+(fv.valueAtLocation(l)));//Same +1 for vocabulary
                      //System.out.print(" " + dataAlphabet.lookupObject(j) + " " + ((int) fv.valueAtLocation(j)));
                }
            }
            else {
                throw new IllegalArgumentException ("Printing is supported for FeatureVector for SVM Style list, found " + instance.getData().getClass());
            }
            if(printFileNames.value) output.append(" #"+instance.getName());
            out.println(output);
        }

        if (! outputFile.value.toString().equals ("-")) {
            out.close();
        }
    }

    /** print an instance list according to the SVM Style format */
    private static void printWeightedSVMStyleList(InstanceList instances, PrintStream out, double[] typeTopicWeight, Alphabet alphabet ) {
//        double hurdle = 1/trainingDocNum;
//        double hurdle = 0.5;

        int numInstances = instances.size();
        Alphabet labelAlphabet = instances.getTargetAlphabet ();
        int numClasses = labelAlphabet.size();

        //hurdle 14.09.11 for weighting term contribution. Topic Model Word weighting 일정 이상인 것만 feature 로 사용함.
//        double hurdleRatio = 1000*numClasses;
        double hurdle = (double) numClasses/(numClasses+trainingDocNum.value());
//        double hurdle = hurdleRatio/(Math.pow(trainingDocNum.value(),2)+hurdleRatio); // 허들 계산방법은 다양한데. 일단 이렇게.. 만듬 2014.9.11

        int numFeatures = instances.getDataAlphabet().size();
        Alphabet dataAlphabet = instances.getDataAlphabet();
        double[] dataAlphabetScores = new double[numFeatures];  //data alphabet 에 index 별로 해당하는 term contribution을 alphabet (=Weighted words)에서 가져옴
        for (int i = 0; i < numFeatures; i++) {
            double termContribuionScore = 0.0;
            String word = (String) dataAlphabet.lookupObject(i);
            int weightedAlphabetIndex = alphabet.lookupIndex(word,false);
            if(weightedAlphabetIndex==-1){
                termContribuionScore = 1.0;
            }else{
                termContribuionScore = typeTopicWeight[weightedAlphabetIndex];
            }

            dataAlphabetScores[i]=termContribuionScore;
        }


        StringBuilder output = new StringBuilder();
        output.append("#This is svm light style weighted (Term Contributed) Instance List. First column is a label(=target class, 0 means hidden.) Feature size : "+numFeatures+".  Number of instances : "+numInstances+"."); //dictionary[0]=1
        out.println(output);

        for (int i = 0; i < instances.size(); i++) {
            Instance instance = instances.get(i);

            output = new StringBuilder();// line 출력용
            if (instance.getData() instanceof FeatureVector) {
                FeatureVector fv = (FeatureVector) instance.getData ();

//                output.append(instance.getTarget());//text 용
                Label target = (Label) instance.getTarget();
                output.append((Integer)(target.getIndex()+1));//number (+1 은 실제 label number는 1부터 시작하므로)

                // Sparse: Print features with non-zero values only.
                for (int l = 0; l < fv.numLocations(); l++) {
                    int fvi = fv.indexAtLocation(l);
                    if(dataAlphabetScores[fvi]>hurdle){
                        // term Contribution 반영은 pow 와 곱 두가지 방식을 모두 사용하여 반영함.
                        output.append(" "+(Integer)(fvi+1)+":"+(Math.pow(fv.valueAtLocation(l),dataAlphabetScores[fvi]))*dataAlphabetScores[fvi]);//Same +1 for vocabulary
//                      output.append(" "+(Integer)(fvi+1)+":"+(fv.valueAtLocation(l))*dataAlphabetScores[fvi]);//Same +1 for vocabulary

                    }

                    //System.out.print(" " + dataAlphabet.lookupObject(j) + " " + ((int) fv.valueAtLocation(j)));
                }
            }
            else {
                throw new IllegalArgumentException ("Printing is supported for FeatureVector for SVM Style list, found " + instance.getData().getClass());
            }
            if(printFileNames.value) output.append(" #"+instance.getName());
            out.println(output);
        }

        if (! outputFile.value.toString().equals ("-")) {
            out.close();
        }
    }


    /** print an instance list according to the format string */
	private static void printInstanceList(InstanceList instances, String formatString) {

		int numInstances = instances.size();
		int numClasses = instances.getTargetAlphabet().size();
		int numFeatures = instances.getDataAlphabet().size();

		Alphabet dataAlphabet = instances.getDataAlphabet();
		double[] counts = new double[numFeatures];
		double count;

		for (int i = 0; i < instances.size(); i++) {
			Instance instance = instances.get(i);

			if (instance.getData() instanceof FeatureVector) {
				FeatureVector fv = (FeatureVector) instance.getData ();
				
				System.out.print(instance.getName() + " " + instance.getTarget());
				
				if (formatString.charAt(0) == 'a') {
					// Dense: Print all features, even those with value 0.
					for (int fvi=0; fvi<numFeatures; fvi++){
						printFeature(dataAlphabet.lookupObject(fvi), fvi,  fv.value(fvi), formatString);
					}
				}
				else {
					// Sparse: Print features with non-zero values only.
					for (int l = 0; l < fv.numLocations(); l++) {
						int fvi = fv.indexAtLocation(l);
						printFeature(dataAlphabet.lookupObject(fvi), fvi, fv.valueAtLocation(l), formatString);
						//System.out.print(" " + dataAlphabet.lookupObject(j) + " " + ((int) fv.valueAtLocation(j)));
					}
				}
			}
			else if (instance.getData() instanceof FeatureSequence) {
				FeatureSequence featureSequence = (FeatureSequence) instance.getData();

				StringBuilder output = new StringBuilder();

				output.append(instance.getName() + " " + instance.getTarget());

				for (int position = 0; position < featureSequence.size(); position++) {
					int featureIndex = featureSequence.getIndexAtPosition(position);

					char featureFormat = formatString.charAt(2);
					if (featureFormat == 'w') {
						output.append(" " + dataAlphabet.lookupObject(featureIndex));
					}
					else if (featureFormat == 'n') {
						output.append(" " + featureIndex);
					}
					else if (featureFormat == 'c') {
						output.append(" " + dataAlphabet.lookupObject(featureIndex) + ":" + featureIndex);
					}
				}

				System.out.println(output);
			}
			else {
				throw new IllegalArgumentException ("Printing is supported for FeatureVector and FeatureSequence data, found " + instance.getData().getClass());
			}

			System.out.println();
		}

		System.out.println();

		return; // counts;
	}

	/* helper for printInstanceList. prints a single feature within an instance */
	private static void printFeature(Object o, int fvi, double featureValue, String formatString) {
		// print object  n,w,c,e
		char c1 = formatString.charAt(2);
		if (c1 == 'w') {    // word
			System.out.print("  " + o);
		} else if (c1 == 'n') {   // index of word
			System.out.print("  " + fvi);
		} else if (c1 == 'c') { //word and index
			System.out.print("  " + o + ":" + fvi);
		} else if (c1 == 'e'){ //no word identity
		}

		char c2 = formatString.charAt(1);
		if (c2 == 'i') {    // integer count
			System.out.print(" " + ((int)(featureValue + .5)));
		} else if (c2 == 'b') {   // boolean present/not present
			System.out.print(" " + ((featureValue>0.5) ? "1" : "0"));
		}

	}

    public static double[] readStopwordsObject(File parameterFile) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(parameterFile)); // object 쓰기위한 스트림
        double[] typeTopicWeightObject = (double[]) in.readObject();
        in.close();

        return typeTopicWeightObject;
    }

    public static Alphabet readAlphabetObject(File parameterFile) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(parameterFile)); // object 쓰기위한 스트림
        Alphabet weightedAlphabet = (Alphabet) in.readObject();
        in.close();

        return weightedAlphabet;
    }

}
