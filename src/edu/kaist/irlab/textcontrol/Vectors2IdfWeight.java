/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.textcontrol;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

import java.io.*;
import java.util.Iterator;
import java.util.TreeSet;

public class Vectors2IdfWeight {


    public static void writeTermWeightList(TermWeight termWeight,String outputFileName) throws IOException {

        PrintStream out = new PrintStream(new File(outputFileName));
        Alphabet typeAlphabet = termWeight.typeAlphabet;
        double[] typeTermWeight = termWeight.typeWeight;
        int numTypes = typeAlphabet.size();

        TreeSet<IDSorter> sortedWords = new TreeSet<IDSorter>();
        for (int type = 0; type < numTypes; type++) {
                sortedWords.add(new IDSorter(type, typeTermWeight[type]));
        }

        int word = 1;
        Iterator<IDSorter> iterator = sortedWords.descendingIterator();
        while (iterator.hasNext()) {
            IDSorter info = iterator.next();
            out.println(word+","+typeAlphabet.lookupObject(info.getID()) + "," +
                    info.getWeight());
            word++;
        }
        out.close();
    }

    public static TermWeight getIdfTermWeight(InstanceList instances) {

        Alphabet typeAlphabet = instances.getAlphabet();
        int numTypes = typeAlphabet.size();
        double[] typeIdfWeight = new double[numTypes];
            for (int type=0; type < numTypes; type++){
            typeIdfWeight[type] = getIdfFromType(type,instances);
        }

        TermWeight idfTermWeight = new TermWeight(typeIdfWeight,typeAlphabet);
        return idfTermWeight;
    }

    //idf per type
    public static double getIdfFromType(int type,InstanceList instances) {
        double idf = 0.0;
        int df = 0;

        for (int i = 0; i < instances.size(); i++) {
            FeatureSequence docFeatures = (FeatureSequence) instances.get(i).getData();
            int[] features = docFeatures.getFeatures();
            for(int f:features) {
                if(type==f){
                    df++;
                    break;
                }
            }
        }
        if(!(df==0)&&!(df==instances.size())){
            idf=Math.log((double) (instances.size())/df);
            idf=idf/Math.log(instances.size());
        }
        return idf;
    }

    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;
    private static final int NULL_INTEGER = -1;

	public static void main (String[] args) throws IOException, ClassNotFoundException {
        //args 설명 0: 입력하는 전체 corpus 에 대한 mallet vector
        //args 설명 1: output object location and name

        InstanceList allfiles = InstanceList.load (new File(args[0]));
        String outputfileName = args.length > 1 ? args[1] : null;;

        TermWeight termWeightObject = getIdfTermWeight(allfiles);

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(outputfileName)));
        out.writeObject(termWeightObject);

        out.close();

    }

}