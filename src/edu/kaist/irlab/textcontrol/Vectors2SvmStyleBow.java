/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 18 오전 12:06
 * User email: shalomeir@gmail.com
 */



package edu.kaist.irlab.textcontrol;

import cc.mallet.types.*;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import static edu.kaist.irlab.topics.WeightedTopicModel.readTermWeightObject;

/**
 * Writing SVM Light style text.
   @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

public class Vectors2SvmStyleBow
{
	private static Logger logger = MalletLogger.getLogger(Vectors2SvmStyleBow.class.getName());

	static CommandOption.File inputFile = new CommandOption.File
	(Vectors2SvmStyleBow.class, "input", "FILE", true, new File("-"),
	 "Read the instance list from this file; Using - indicates stdin.", null);

    static CommandOption.File weightedinputFile = new CommandOption.File
            (Vectors2SvmStyleBow.class, "weightedinput", "FILE", true, null,
                    "Read the serialized term weight from this file; Using - indicates stdin.", null);


    static CommandOption.String outputFilePreName = new CommandOption.String
            (Vectors2SvmStyleBow.class, "output", "FILE", true, null,
                    "Write the SVM Style instance list to this name plus svmlight.txt; Using - indicates stdout.", null);


    static CommandOption.File featureFile = new CommandOption.File
            (Vectors2SvmStyleBow.class, "featureoutput", "FILE", false, new File("text.vectors"),
                    "Write the feature dictonary to this file; Using - indicates stdout.", null);

    static CommandOption.File statsFile = new CommandOption.File
            (Vectors2SvmStyleBow.class, "statsoutput", "FILE", true, new File("text.vectors"),
                    "Write the statistics for this instance lists to this file; Using - indicates stdout.", null);

	static CommandOption.Integer printInfogain = new CommandOption.Integer
	(Vectors2SvmStyleBow.class, "print-infogain", "N", false, 0,
	 "Print top N words by information gain, sorted.", null);

	static CommandOption.Boolean printLabels = new CommandOption.Boolean
	(Vectors2SvmStyleBow.class, "print-labels", "[TRUE|FALSE]", false, false,
	 "Print class labels known to instance list, one per line.", null);

    static CommandOption.Boolean printFileNames = new CommandOption.Boolean
            (Vectors2SvmStyleBow.class, "print-FileNames", "[TRUE|FALSE]", false, true,
                    "Print file name with start character # after feature list.", null);

	static CommandOption.String printMatrix = new CommandOption.String
	(Vectors2SvmStyleBow.class, "print-matrix", "STRING", false, "sic",
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
            (Vectors2SvmStyleBow.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
                    "Character encoding for input file", null);

    static CommandOption.Integer trainingDocNum = new CommandOption.Integer
            (Vectors2SvmStyleBow.class, "training-doc-num", "integer", true, 0,
                    "Training Doucment Number; Using for ratio about topic model feature and word feature", null);

	public static void main (String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

		// Process the command-line options
		CommandOption.setSummary (Vectors2SvmStyleBow.class,
								  "A tool for printing information about instance lists of feature vectors.");
		CommandOption.process (Vectors2SvmStyleBow.class, args);

		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(Vectors2SvmStyleBow.class).printUsage(false);
			System.exit (-1);
		}
		if (false && !inputFile.wasInvoked()) {
			System.err.println ("You must specify an input instance list, with --input.");
			System.exit (-1);
		}

        // Write classifications to the output file
        PrintStream outFeature = new PrintStream(new File(outputFilePreName.value+"_Svmlight.txt"), encoding.value);
        PrintStream outStat = new PrintStream(new File(outputFilePreName.value+"_stat.txt"), encoding.value);


		// Read the InstanceList
		InstanceList instances = InstanceList.load (inputFile.value);

        //Read the searialized typeTermWeight Term Weight
        double[] typeTermWeight = null;
        Alphabet weightAlphabet = null;

        if(weightedinputFile.value!=null){
            TermWeight termWeight = readTermWeightObject(weightedinputFile.value);
            typeTermWeight = termWeight.typeWeight;
            weightAlphabet = termWeight.typeAlphabet;
        }

        if (printLabels.value) {
			Alphabet labelAlphabet = instances.getTargetAlphabet ();
			for (int i = 0; i < labelAlphabet.size(); i++) {
				System.out.println (labelAlphabet.lookupObject (i));
			}
			System.out.print ("\n");
		}

        if (statsFile.value.exists()) {
            StringBuilder output = new StringBuilder();
            output.append("#This is a statistics file for instance List file extracted same time. : "+outputFilePreName.value+"\n");
            int numInstances = instances.size();
            Alphabet labelAlphabet = instances.getTargetAlphabet ();
            int numClasses = labelAlphabet.size();
            Alphabet dataAlphabet = instances.getDataAlphabet();
            int numFeatures = dataAlphabet.size();

            output.append("numInstances : "+numInstances+"\n");
            output.append("numClasses : "+numClasses+"\n");
            output.append("numFeatures : "+numFeatures+"\n");

            int numRealFeatures = 0;
            if (weightedinputFile.value!=null){
                double hurdle = (double) numClasses/(numClasses+trainingDocNum.value());
                for (int i = 0; i < numFeatures; i++) {
                    String word = (String) dataAlphabet.lookupObject(i);
                    int weightedAlphabetIndex = weightAlphabet.lookupIndex(word,false);
                    if(weightedAlphabetIndex==-1||(hurdle<=typeTermWeight[weightedAlphabetIndex])){
                        numRealFeatures++;
                    }
                }
                output.append("numRealFeatures : "+numRealFeatures+"\n");
            }
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
            outStat.println(output);

            if (! statsFile.value.toString().equals ("-")) {
                outStat.close();
            }
        }


		if (printInfogain.value > 0) {
			InfoGain ig = new InfoGain (instances);
			for (int i = 0; i < printInfogain.value; i++) {
				System.out.println (""+i+" "+ig.getObjectAtRank(i));
			}
			System.out.print ("\n");
		}

        if (weightedinputFile.value!=null) {
            printWeightedSVMStyleList(instances, outFeature, typeTermWeight, weightAlphabet); // training document 수에 따라 confidence hurdle을 다르게 두어야 하기 에 training 문서수가 중요함
        }else{
            printSVMStyleList(instances, outFeature);
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
        out.close();
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
                    if(dataAlphabetScores[fvi]>=hurdle){
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
        out.close();
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

}
