/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 9:45
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 18 오후 9:05
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.textcontrol;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.SelectiveFileLineIterator;
import cc.mallet.types.*;
import cc.mallet.util.CommandOption;
import cc.mallet.util.MalletLogger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

/**
 * Command line import tool for loading a sequence of 
 *  instances from an SVMLight feature-value pair file, with one instance 
 *  per line of the input file. 
 *  <p>
 *  
 * The expected format is
 * 
 * target feature:value feature:value ...
 * 
 * targets and features can be indices, as in 
 * SVMLight, or Strings.
 * 
 * Note that if targets and features are indices,
 * their indices in the data and target Alphabets
 * may be different, though the data will be
 * equivalent.  
 * 
 * Note that the input and output args can take multiple files.
 * 
 *  @author Gregory Druck
 *  @author Seonggyu Lee
 */


public class SvmLights2SvmLight {

	private static Logger logger = MalletLogger.getLogger(SvmLights2SvmLight.class.getName());

	static CommandOption.SpacedStrings inputFiles =	new CommandOption.SpacedStrings
		(SvmLights2SvmLight.class, "input", "FILE", true, null,
		 "The files containing data to be classified, one instance per line", null);

    static CommandOption.SpacedStrings inputFilesFeatureNums =	new CommandOption.SpacedStrings
            (SvmLights2SvmLight.class, "inputfnum", "integer", true, null,
              " align with inputFile. How many features per each input File?", null);


	static CommandOption.File outputFile = new CommandOption.File
		(SvmLights2SvmLight.class, "output", "FILE", true, new File("text.vectors"),
		 "Write the instance list to this file; Using - indicates stdout.", null);

    static CommandOption.File statsFile = new CommandOption.File
            (SvmLights2SvmLight.class, "statsoutput", "FILE", true, new File("text.vectors"),
                    "Write the statistics for this instance lists to this file; Using - indicates stdout.", null);
	
	static CommandOption.File usePipeFromVectorsFile = new CommandOption.File
		(SvmLights2SvmLight.class, "use-pipe-from", "FILE", true, new File("text.vectors"),
		 "Use the pipe and alphabets from a previously created vectors file.\n" +
		 "   Allows the creation, for example, of a test set of vectors that are\n" +
		 "   compatible with a previously created set of training vectors", null);

	static CommandOption.Boolean printOutput = new CommandOption.Boolean
		(SvmLights2SvmLight.class, "print-output", "[TRUE|FALSE]", false, false,
		 "If true, print a representation of the processed data\n" +
		 "   to standard output. This option is intended for debugging.", null);

    static CommandOption.Boolean printFileNames = new CommandOption.Boolean
            (SvmLights2SvmLight.class, "print-FileNames", "[TRUE|FALSE]", false, true,
                    "Print file name with start character # after feature list.", null);

    static CommandOption.Boolean print2ndOnly = new CommandOption.Boolean
            (SvmLights2SvmLight.class, "print2ndOnly", "[TRUE|FALSE]", true, false,
                    "Print 2nd Features Only (use for lda feature only).", null);

	static CommandOption.String encoding = new CommandOption.String
	  (SvmLights2SvmLight.class, "encoding", "STRING", true, Charset.defaultCharset().displayName(),
	 "Character encoding for input file", null);

    static CommandOption.Integer trainingDocNum = new CommandOption.Integer
            (SvmLights2SvmLight.class, "training-doc-num", "integer", true, 0,
                    "Training Doucment Number; Using for ratio about topic model feature and word feature", null);

	public static void main (String[] args) throws IOException
	{
		// Process the command-line options
		CommandOption.setSummary (SvmLights2SvmLight.class,
								  "A tool for creating instance lists of feature vectors from comma-separated-values");
		CommandOption.process (SvmLights2SvmLight.class, args);

		// Print some helpful messages for error cases
		if (args.length == 0) {
			CommandOption.getList(SvmLights2SvmLight.class).printUsage(false);
			System.exit (-1);
		}
		if (inputFiles == null) {
			throw new IllegalArgumentException ("You must include `--input FILE FILE ...' in order to specify "+
								"files containing the instances, one per line.");
		}

        if (inputFilesFeatureNums == null) {
            throw new IllegalArgumentException ("You must include `--inputfnum integer integer ...' in order to specify "+
                    "files containing the instances, one per line.");
        }
		
		Pipe instancePipe;
        Pipe instancePipe2; // 항상 갱신되는 용도

        InstanceList previousInstanceList = null;
		
		if (usePipeFromVectorsFile.wasInvoked()) {

			// Ignore all options, use a previously created pipe

			previousInstanceList = InstanceList.load (usePipeFromVectorsFile.value);
			instancePipe = previousInstanceList.getPipe();			
		}
		else {
			// Build a new pipe
			ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
			pipeList.add(new SvmLight2FeatureVectorAndLabel());
			if (printOutput.value) {
				pipeList.add(new PrintInputAndTarget());
			}
			instancePipe = new SerialPipes(pipeList);
		}

		InstanceList[] instances = new InstanceList[inputFiles.value.length];
		for (int fileIndex = 0; fileIndex < inputFiles.value.length; fileIndex++) {
			// Create the instance list and open the input file (but each instance have unique pipe for MyExp sglee)
            // Build a new pipe
            ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
            pipeList.add(new SvmLight2FeatureVectorAndLabel());
            if (printOutput.value) {
                pipeList.add(new PrintInputAndTarget());
            }
            instancePipe2 = new SerialPipes(pipeList);

			if (fileIndex==0) instances[fileIndex] = new InstanceList (instancePipe);
            else instances[fileIndex] = new InstanceList (instancePipe2);
			Reader fileReader;
			if (inputFiles.value[fileIndex].equals ("-")) {
				fileReader = new InputStreamReader (System.in);
			}
			else {
				fileReader = new InputStreamReader(new FileInputStream(inputFiles.value[fileIndex]), encoding.value);
			}
			
			// Read instances from the file
			instances[fileIndex].addThruPipe (new SelectiveFileLineIterator (fileReader, "^\\s*#.+"));
		}

        Integer[] featureNums = new Integer[inputFilesFeatureNums.value.length];
        String[] sfn = inputFilesFeatureNums.value();
        for (int fnIndex = 0; fnIndex < inputFilesFeatureNums.value.length; fnIndex++) {
            // Create the instance list and open the input file
            featureNums[fnIndex] = Integer.parseInt(sfn[fnIndex]);
        }

        // Write merged svmlight vectors to the output one file
        PrintStream out = null;
        if (outputFile.value.toString().equals ("-")) {
            out = System.out;
        }
        else {
            out = new PrintStream(outputFile.value, encoding.value);
        }

        // Write classifications to the output file
        PrintStream statsOut = null;
        if (statsFile.value.toString().equals ("-")) {
            statsOut = System.out;
        }
        else {
            statsOut = new PrintStream(statsFile.value, encoding.value);
        }

        if (statsFile.value.exists()) {
            StringBuilder output = new StringBuilder();
            output.append("#This is a statistics file for Merged instance List file extrated same time. : "+outputFile.value.toString()+"\n");

            int numFeatures = 0;
            for(int nf :featureNums) numFeatures += nf;
            int numInstances = instances[0].size();
            Alphabet labelAlphabet = instances[0].getTargetAlphabet();

            output.append("numInstances : "+numInstances+"\n");
            output.append("numClasses : "+labelAlphabet.size()+"\n");
            output.append("numFeatures : "+numFeatures+"\n");

            for(int vn =0 ;vn<featureNums.length;vn++){
                output.append("\tInput SVM Vector No."+vn+" numFeatures: "+featureNums[vn]+" from File: "+inputFiles.value()[vn]+"\n");
            }
            output.append("#label statistics(Labelnum\tLabelname\tLabelInstancesNum)"+"\n");

            //label 별 instance number 계산하기
            Integer[] labelStats = new Integer[labelAlphabet.size()];
            Arrays.fill(labelStats, 0);
            for (int i = 0; i < numInstances; i++) {
                Instance instance = instances[0].get(i);
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

        if (outputFile.value.exists()) {
            printSVMStyleOneList(instances, out, featureNums);
        }


		//  If we are reusing a pipe from an instance list 
		//  created earlier, we may have extended the label
		//  or feature alphabets. To maintain compatibility,
		//  we now save that original instance list back to disk
		//  with the new alphabet.
		if (usePipeFromVectorsFile.wasInvoked()) {
			logger.info(" Rewriting extended pipe from " + usePipeFromVectorsFile.value);
			logger.info("  Instance ID = " + previousInstanceList.getPipe().getInstanceId());
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(usePipeFromVectorsFile.value));
			oos.writeObject(previousInstanceList);
			oos.close();
		}
	}

    /** print an instance list according to the SVM Style format */
    private static void printSVMStyleOneList(InstanceList[] instanceList, PrintStream out, Integer[] featureNums) {
        //우선 instanceList 를 첫번째 instances 로 모두 통합 (후속 instances 들은 모두 첫번째 instance 를 포함하고 있어야 함, error 검출 불가.)

        Map<String,Instance> standardInstanceMap = new HashMap<String,Instance>();
        Integer[] capacities = featureNums;
        int listNum = instanceList.length;


        // instnace List 별로 size 비교, feature size 는 계속 더하기. dictionaries 만들기, capacities 만들기
        for (int fileIndex = 0; fileIndex < listNum; fileIndex++) {
            InstanceList instances = instanceList[fileIndex]; // instance list 가져옴.


            // 첫번째 list instances 를 기준으로 하기 위해 standardMap 만듬
            if(fileIndex==0) {
                Iterator<Instance> standardInstanceIter = instances.iterator();

                while(standardInstanceIter.hasNext()){

                    ArrayList<int[]> indicesXs = new ArrayList<int[]> ();
                    ArrayList<double[]> valuesXs = new ArrayList<double[]> ();
                    Instance standardInstance = standardInstanceIter.next();
                    standardInstance.unLock();
                    FeatureVector sData = (FeatureVector) standardInstance.getData();
                    indicesXs.add(sData.getIndices());
                    valuesXs.add(sData.getValues());

                    standardInstance.setData(new FeaturesTuple(indicesXs,valuesXs,capacities));

                    String sName = standardInstance.getName().toString();
                    int lastSlashPos = sName.lastIndexOf('/'); //subfolder name 까지 같아야 함
                    String abstractName = sName.substring(sName.lastIndexOf('/',lastSlashPos-1)+1);
                    standardInstanceMap.put(abstractName,standardInstance);
                }
            }else{  // 나머지 list 의 size 가 첫번째와 같은지 비교하고 같으면,, 문제없이. 계속 standardMap 에 data 추가.
                try{
                    if(standardInstanceMap.size()>instances.size())
                        throw new MyExpException();
                    else{
                        Iterator<Instance> instanceIter = instances.iterator();
                        while(instanceIter.hasNext()) {
                            Instance instance = instanceIter.next();

                            String iName = instance.getName().toString();
                            int lastSlashPos = iName.lastIndexOf('/'); //subfolder name 까지 같아야 함
                            String absiName = iName.substring(iName.lastIndexOf('/',lastSlashPos-1)+1);

                            Instance standardInstance = standardInstanceMap.get(absiName);
                            if(standardInstance==null) continue;
//                            if(!standardInstance.getTarget().equals(instance.getTarget()))
//                                throw new MyExpException();
                            if(false);
                            else{
                                FeatureVector iData = (FeatureVector) instance.getData();

                                FeaturesTuple sData = (FeaturesTuple) standardInstance.getData();
                                sData.addIndicesXs(iData.getIndices());

                                // topic 비중이 높게 하면 어떨까? 14.09.03
                                if(trainingDocNum.value()>0) {
                                    double[] iValues = iData.getValues();
                                    for (int ivl = 0; ivl < iValues.length; ivl++) {
//                                        iValues[ivl] = iValues[ivl] * 1000.0 / (trainingDocNum.value()); //Pumping 트레이닝 데이터가 충분하지 않을때 더 크게 펌핑하여 topic feature 가 중요하게 다뤄지도록 함.
//                                        iValues[ivl] = iValues[ivl] * 3; //Pumping 정수
                                        iValues[ivl] = iValues[ivl] *((trainingDocNum.value()+1000)/trainingDocNum.value()+1); //Pumping 2014.09.11
                                    }
                                    sData.addValuesXs(iValues);
                                }else{
                                    sData.addValuesXs(iData.getValues()); // original
                                }
                            }
                        }
                    }
                } catch ( MyExpException e ){
                    System.err.println(e.getMessage());
                }
            }
        }

        // 통합 완료
        // 통합된 결과는 standardInstanceMap 에 <name, instance > 형태로 존재
        int numFeatures = 0;
        if(!print2ndOnly.value) for(int nf :capacities) numFeatures += nf;
        else numFeatures = capacities[1];
        int numInstances = standardInstanceMap.size();

        StringBuilder output = new StringBuilder();
        if(!print2ndOnly.value) output.append("#This is a Merged svm light style Instances List. First column is a label number(=target class, 0 means hidden.) Feature size : "+numFeatures+",  Number of instances : "+numInstances+"."); //dictionary[0]=1
        else output.append("#This is a svm light style topic features. First column is a label number(=target class, 0 means hidden.) Feature size : "+numFeatures+",  Number of instances : "+numInstances+".");
        out.println(output);

        Iterator<Map.Entry<String, Instance>> instanceIter = standardInstanceMap.entrySet().iterator();
        while(instanceIter.hasNext()){
            Instance instance = instanceIter.next().getValue();

            output = new StringBuilder();// line 출력용
            if (instance.getData() instanceof FeaturesTuple) {
                FeaturesTuple ft = (FeaturesTuple) instance.getData();
                List<int[]> ftIndicesXs = ft.getIndicesXs();
                List<double[]> ftValuesXs = ft.getValuesXs();

                Label target = (Label) instance.getTarget();
                output.append((Integer)(target.getIndex()+1));//number (+1 은 실제 label number는 1부터 시작하므로)

                // 몇개의 featureVector가 합쳐졌는지는 capacities 의 size로 확인
                if(!print2ndOnly.value) {
                    int fvNum = capacities.length;
                    for (int fs = 0; fs < fvNum; fs++) { // 개별 featureVector 별로 붙여서 주욱 feature들을 출력
                        int[] featureVectorIndices = ftIndicesXs.get(fs);
                        double[] featureVectorValues = ftValuesXs.get(fs);

                        for (int l = 0; l < featureVectorIndices.length; l++) {
                            int fvi = featureVectorIndices[l];
                            double fvv = featureVectorValues[l];

                            if (fs == 0) output.append(" " + (Integer) (fvi + 1) + ":" + fvv);//Same +1 for vocabulary
                            else
                                output.append(" " + (Integer) (fvi + 1 + capacities[fs - 1]) + ":" + fvv);//featureNum 는 더해짐.
                            //System.out.print(" " + dataAlphabet.lookupObject(j) + " " + ((int) fv.valueAtLocation(j)));
                        }
                    }
                }else{
                    int[] featureVectorIndices = ftIndicesXs.get(1);
                    double[] featureVectorValues = ftValuesXs.get(1);
                    for (int l = 0; l < featureVectorIndices.length; l++) {
                        int fvi = featureVectorIndices[l];
                        double fvv = featureVectorValues[l];
                        output.append(" " + (Integer) (fvi + 1) + ":" + fvv);//featureNum 는 더해짐.
                        //System.out.print(" " + dataAlphabet.lookupObject(j) + " " + ((int) fv.valueAtLocation(j)));
                    }
                }
            }
            else {
                throw new IllegalArgumentException ("Printing is supported for FeaturesTuple for SVM Style list, found " + instance.getData().getClass());
            }
            if(printFileNames.value) output.append(" #"+instance.getName());
            out.println(output);
        }

        if (! outputFile.value.toString().equals ("-")) {
            out.close();
        }
    }
}





	

