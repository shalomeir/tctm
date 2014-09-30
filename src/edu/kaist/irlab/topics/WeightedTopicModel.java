/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.topics;

import cc.mallet.types.*;
import edu.kaist.irlab.textcontrol.TermWeight;
import gnu.trove.TIntIntHashMap;

import java.io.*;
import java.util.*;

/**
 * WeightedTopicModel based on Mallet Source 'DMRTopicModel'.
 *
 * @author Seonggyu Lee
 */

public class WeightedTopicModel extends WeightedLDAHyper {

    int numFeatures;
    int defaultFeatureIndex;

    public static int numTopics = 50;
    public static double initAlphaSum = 50;
    public static int burnOverIteration = 100; // Variance Term Weighting start over than this iteration
    private static String filePreName; //pre name of output file name
    public static int burnInPeriod =  40;// This number used for async Dirichlet alpha prior
    private static String idfWeight; //pre name of output file name
    private static String testFileNameForEmpiricalLikelihood; //This file would be used for Empirical Likelihood

    public static String likelihoodLog = "";

//    Pipe parameterPipe = null;
//    Pipe invParameterPipe = null;


    double[][] alphaCache;
    double[] alphaSumCache;

    protected double[] balancePerTopic; // indexed by <topic index>
    protected double[] balancePerTopicOneDoc; // indexed by <topic index>

    public WeightedTopicModel(int numberOfTopics, double initAlphaSum) {
		super(numberOfTopics, initAlphaSum);
	}

	public void estimate (int iterationsThisRound) throws IOException {
        likelihoodLog = "";

        numFeatures = data.get(0).instance.getTargetAlphabet().size() + 1;
        defaultFeatureIndex = numFeatures - 1;

		int numDocs = data.size(); // TODO consider beginning by sub-sampling?

		alphaCache = new double[numDocs][numTopics];
        alphaSumCache = new double[numDocs];

		long startTime = System.currentTimeMillis();
        int maxIteration = iterationsSoFar + iterationsThisRound;

        balancePerTopic = new double[numTopics]; //For Balance Weighting
        Arrays.fill(balancePerTopic, 0.0);
        balancePerTopicOneDoc = new double[numTopics];
        Arrays.fill(balancePerTopicOneDoc, 0.0);

        totalTokens = 0;
        for (int topic=0; topic < numTopics; topic++) {
            totalTokens+=tokensPerTopic[topic];
        }

        // IDF Weight load
        if(idfWeight!=null){
            try {
                idfTermWeight = readTermWeightObject(new File(idfWeight)).typeWeight;
            }catch (Exception exp){
                System.err.println(exp);
            }
        }


        // Main Iteration Start
        for ( ; iterationsSoFar <= maxIteration; iterationsSoFar++) {
            long iterationStart = System.currentTimeMillis();
            boolean useVarianceTopicModel = (burnOverIteration<iterationsSoFar);
            useBetaWeightedModel = useVarianceTopicModel;

            //Print likelihood
            if (showTopicsInterval != 0 && iterationsSoFar != 0 && iterationsSoFar % 50 == 0) { // 50단위로 likelihood 계산
                System.out.println();
                // evaluation likelihood
                if (testing != null) {
                    double el = empiricalLikelihood(1000, testing);
                    double ll = modelLogLikelihood();
                    System.out.println("Empirical Likelihood: "+el+"\t"+ "model log likelihood: "+ll + "\t");
                    likelihoodLog+="Iteration Num:\t"+iterationsSoFar+"\t"+"Empirical Likelihood:\t"+el+"\t"+ "model log likelihood:\t"+ll + "\t"+"\n";
                }else{
                    double ll = modelLogLikelihood();
                    System.out.println("model log likelihood : "+ll);
                    likelihoodLog+="Iteration Num:\t"+iterationsSoFar+"\t"+ "model log likelihood:\t"+ll + "\t"+"\n";
                }
            }

            if (showTopicsInterval != 0 && iterationsSoFar != 0 && iterationsSoFar % showTopicsInterval == 0) {
                System.out.println();
                printTopWords (System.out, wordsPerTopic, false);
			}

			if (saveStateInterval != 0 && iterationsSoFar % saveStateInterval == 0) {
                this.printState(new File(stateFilename + '.' + iterationsSoFar + ".gz"));
            }

			if (iterationsSoFar > burninPeriod && optimizeInterval != 0 &&
				iterationsSoFar % optimizeInterval == 0) {


			}
            // TODO this condition should also check that we have more than one sample to work with here
            // (The number of samples actually obtained is not yet tracked.)
            if (iterationsSoFar > burninPeriod && optimizeInterval != 0 &&
                    iterationsSoFar % optimizeInterval == 0) {

                alphaSum = Dirichlet.learnParameters(alpha, topicDocCounts, docLengthCounts);

                smoothingOnlyMass = 0.0;
                for (int topic = 0; topic < numTopics; topic++) {
                    smoothingOnlyMass += alpha[topic] * beta / (tokensPerTopic[topic] + betaSum);
                    if(useBetaWeightedModel){
                        cachedCoefficients[topic] =  alpha[topic] / (weightSumPerTopic[topic] + betaSum);
                    }else{
                    cachedCoefficients[topic] =  alpha[topic] / (tokensPerTopic[topic] + betaSum);
                    }
                }
                clearHistograms();
            }


            // For Weighted Topic Model
            typeTopicWeight = new double[numTypes];
            if(idfWeight!=null){
                typeTopicWeight=idfTermWeight;
            }if(useVarianceTopicModel){
                for (int type=0; type < numTypes; type++) {
                    TIntIntHashMap currentTypeTopicCounts = typeTopicCounts[type];  // this is important value similar with beta
                    typeTopicWeight[type] = getVarianceFromMap(currentTypeTopicCounts, numTopics);
                }
            }else{
                Arrays.fill(typeTopicWeight, 1.0);
            }


            // Term Weight normalization
            Arrays.fill(weightSumPerTopic,0.0);
            totalWeights = 0.0;

            for (int type=0; type < numTypes; type++) {
                TIntIntHashMap currentTypeTopicCounts = typeTopicCounts[type];
                int[] topicTermIndices = currentTypeTopicCounts.keys();
                int[] topicTermValues = currentTypeTopicCounts.getValues();
                for (int i = 0; i < topicTermIndices.length; i++) {
                    double currentWeight = topicTermValues[i]*typeTopicWeight[type];
                    weightSumPerTopic[topicTermIndices[i]]+=currentWeight;
                    totalWeights+=currentWeight;
                }
            }

            double weightRate = totalTokens/totalWeights;
            for (int topic = 0; topic < numTopics; topic++) {
                weightSumPerTopic[topic]*=weightRate;
            }
            for (int type=0; type < numTypes; type++) {
                typeTopicWeight[type]*=weightRate;
            }
            totalWeights*=weightRate;

            // Balancing Term
            for (int topic=0; topic < numTopics; topic++) {
                if(useBalancedModel&&useVarianceTopicModel) balancePerTopic[topic]=(double) totalTokens/tokensPerTopic[topic]/numTopics;
                else balancePerTopic[topic]=1.0;
            }


            // Loop over every document in the corpus
            // sampling step
            for (int doc = 0; doc < numDocs; doc++) {
                FeatureSequence tokenSequence = (FeatureSequence) data.get(doc).instance.getData();
                LabelSequence topicSequence = data.get(doc).topicSequence;
                balancePerTopicOneDoc = balancePerTopic.clone();

                sampleBalancedTopicsForOneDoc(tokenSequence, topicSequence,
                        true, false, balancePerTopicOneDoc, useVarianceTopicModel); //shouldSaveState For Dirichlet Estimation
            } // Sampling Complete


			long ms = System.currentTimeMillis() - iterationStart;
			if (ms > 1000) {
				System.out.print(Math.round(ms / 1000) + "s ");
			}
			else {
				System.out.print(ms + "ms ");
			}

            if (iterationsSoFar % 10 == 0) {
                System.out.println ("<" + iterationsSoFar + "> ");
                if (printLogLikelihood) System.out.println (modelLogLikelihood());
            }
            System.out.flush();
		} //Iteration Complete

		long seconds = Math.round((System.currentTimeMillis() - startTime)/1000.0);
        long minutes = seconds / 60;    seconds %= 60;
        long hours = minutes / 60;  minutes %= 60;
        long days = hours / 24; hours %= 24;
        System.out.print ("\nTotal time: ");
        if (days != 0) { System.out.print(days); System.out.print(" days "); }
        if (hours != 0) { System.out.print(hours); System.out.print(" hours "); }
        if (minutes != 0) { System.out.print(minutes); System.out.print(" minutes "); }
        System.out.print(seconds); System.out.println(" seconds");
	}

	public void learnParameters() {

        // Create a "fake" pipe with the features in the data and 
        //  a trove int-int hashmap of topic counts in the target.
        

        for (int doc=0; doc < data.size(); doc++) {
            
            if (data.get(doc).instance.getTarget() == null) {
                continue;
            }

			FeatureCounter counter = new FeatureCounter(topicAlphabet);

			for (int topic : data.get(doc).topicSequence.getFeatures()) {
				counter.increment(topic);
            }

        }

        for (int doc=0; doc < data.size(); doc++) {
            Instance instance = data.get(doc).instance;
            FeatureSequence tokens = (FeatureSequence) instance.getData();
            if (instance.getTarget() == null) { continue; }
            int numTokens = tokens.getLength();

            // This sets alpha[] and alphaSum
//            if(useDmrModel) setAlphas(instance);

            // Now cache alpha values
            for (int topic=0; topic < numTopics; topic++) {
                alphaCache[doc][topic] = alpha[topic];
            }
            alphaSumCache[doc] = alphaSum;
        }
    }


    public FeatureVector normalizeForProbDistVector(FeatureVector featureVector) {  // 확률값으로 topic feature를 만들어야쥐.
        int[] index = featureVector.getIndices();
        double[] values = featureVector.getValues();
        double sum = 0.0;
        for (int i = 0; i < index.length; i++) {
            sum+=values[i];
        }
        for (int i = 0; i < index.length; i++) {
            values[i]/=sum;
        }
        return new FeatureVector (topicAlphabet, index, values);
    }

    public double[] normalizeForProbDistArray(double[] values,int num) {  // 확률값으로 topic feature를 만들어야쥐. num 는 그 배수. (1로 하면 합이 1이됨.) value수와 num수를 같게 하면 평균이 1이 됨.
        double sum = 0.0;
        for (int i = 0; i < values.length; i++) {
            sum+=values[i];
        }
        for (int i = 0; i < values.length; i++) {
            values[i]=values[i]*num/sum;
        }
        return values;
    }

    public FeatureVector modifyFeatureArray(FeatureVector features) {  // 확률값으로 topic feature를 만들어야쥐.
        int[] indices = features.getIndices();
        double[] values = features.getValues();
        int[] newIndices = new int[1];
        double[] newValues = new double[]{0.0};
        double sum = 0.0;
        double summax = 0.0;

        TreeMap<Double,Integer> featureMap = new TreeMap<Double,Integer>();

        for (int i = 0; i < values.length; i++) {
            featureMap.put(values[i],indices[i]);
        }
        int i = 1;
        NavigableMap<Double, Integer> decMap = featureMap.descendingMap();
        NavigableSet<Double> keySet = decMap.navigableKeySet();
        newIndices[0] = decMap.firstEntry().getValue();
        Iterator<Double> keyIter = keySet.iterator();
        newValues[0] = keyIter.next().doubleValue();
        while(keyIter.hasNext()){
            sum+=(newValues[0]-keyIter.next().doubleValue())/((double)i);
            summax+=1.0/((double)i);
            i++;
        }
//        double lamdaDecay = (double)(numIterations-iterationsSoFar)/numIterations;
//        double hurdle = lamdaDecay*summax/Math.log((numFeatures)*(numFeatures)*2)+(1-lamdaDecay)*summax/(Math.log((numFeatures)*(numFeatures))*2);

        double hurdle =Math.min(summax*0.90, summax/(Math.log(numFeatures*secured/Math.min(numFeatures*50,numFeatures*secured+100)))); //
//        double hurdle =summax*0.9; //
//        if(sum>hurdle&&useSuperSemi)newValues[0]=1.0;

        return new FeatureVector (data.get(0).instance.getTargetAlphabet(), newIndices, newValues);
    }


    public void printTopWords (PrintStream out, int numWords, boolean usingNewLines) throws IOException {
//		if (sbParameters != null) { if(useDmrModel) setAlphas(); }
		super.printTopWords(out, numWords, usingNewLines);
	}


    public void writeLikelihood(File parameterFile) throws IOException {
        if (likelihoodLog != null) {
            PrintStream out = new PrintStream(parameterFile);
            out.println(likelihoodLog);
            out.close();
        }
    }

    public void writeStopwords(File parameterFile) throws IOException {

        PrintStream out = new PrintStream(parameterFile);

        TreeSet<IDSorter> sortedWords = new TreeSet<IDSorter>();
        for (int type = 0; type < numTypes; type++) {
                sortedWords.add(new IDSorter(type, typeTopicWeight[type]));
        }
        int[] typeCount = new int[numTypes];
        Arrays.fill(typeCount, 0);
        for (int type = 0; type < numTypes; type++) {
            TIntIntHashMap currentTypeTopicCounts = typeTopicCounts[type];
            int[] typeCounts = currentTypeTopicCounts.getValues();
            for (int t = 0; t < typeCounts.length; t++) {
                typeCount[type]+=typeCounts[t];
            }
        }

        int word = 1;
        Iterator<IDSorter> iterator = sortedWords.descendingIterator();
        while (iterator.hasNext()) {
            IDSorter info = iterator.next();
            out.println(word+","+alphabet.lookupObject(info.getID()) + "," +
                    info.getWeight()+","+ typeCount[info.getID()]);
            word++;
        }
        out.close();
    }

    public void writeTermWeightObject(File parameterFile) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(parameterFile)); // object 쓰기위한 스트림

        out.writeObject(new TermWeight(typeTopicWeight,alphabet));
        out.close();
    }


    public static TermWeight readTermWeightObject(File parameterFile) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(parameterFile));
        TermWeight termWeight = (TermWeight) in.readObject();
        in.close();
        return termWeight;
    }


    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;
    private static final int NULL_INTEGER = -1;

    public static void execution(String[] args) throws IOException, ClassCastException {
        //args 0: all files vector. This vector is maded by Text2Vectors or Text2WeightedTopicModel
        //args 1: number of topic
        //args 2: alpha Sum
        //args 3: Variance Term Weighting start over than this iteration
        //args 4: pre name of output file name
        //args 5: This number used for Asymmetric Dirichlet alpha prior
        //args 6: True/False for Balance Weighted Topic Model
        //args 7: Test file name and location for Empirical Likelihood.
        //args 8: IDF Term Weight File Location


        InstanceList allfiles = InstanceList.load (new File(args[0]));

        numTopics = args.length > 1 ? Integer.parseInt(args[1]) : 50;
        initAlphaSum = args.length > 2 ? Double.parseDouble(args[2]) : 50;
        burnOverIteration = args.length > 3 ? Integer.parseInt(args[3]) : 100;
        filePreName =  args.length > 4 ?  args[4] : "None";
        burnInPeriod =  args.length > 5 ? Integer.parseInt(args[5]) : 200;
        useBalancedModel =  args.length > 6 ? Boolean.parseBoolean(args[6]) : true;
        testFileNameForEmpiricalLikelihood =  args.length > 7 ?  args[7] : null ;
        idfWeight =  args.length > 8 ?  args[8] : null ;


        InstanceList testingForEL = null;
        if (testFileNameForEmpiricalLikelihood!=null) testingForEL = InstanceList.load (new File(testFileNameForEmpiricalLikelihood));


        WeightedTopicModel wtm = new WeightedTopicModel(numTopics,initAlphaSum);
        wtm.setOptimizeInterval(100);
        wtm.setTopicDisplay(100, 20);
        wtm.setTestingInstances(testingForEL);
        wtm.setBurninPeriod(burnInPeriod);
        wtm.setBurnOverIteration(burnOverIteration);
        wtm.addInstances(allfiles);


        // Core Step of Topic Model
        wtm.estimate(1000);


        // Print Output
        wtm.writeLikelihood(new File(filePreName+"_Likelihood.txt"));
        wtm.printTopWords(new File(filePreName + "_TopWordsPerTopic.txt"), 51, false);

        wtm.printDocumentTopicsSvmStyle(new File(filePreName + "_TopicFeaturesPerDoc_Svmlight.txt")); // Topic Features Per all documents
        wtm.writeStopwords(new File(filePreName+"_TermWeightList.txt")); // print term weight list

        wtm.writeTermWeightObject(new File(filePreName + "_TermWeightObject.ser"));

    }

	public static void main (String[] args) throws IOException, ClassNotFoundException {

        execution(args);

    }


}