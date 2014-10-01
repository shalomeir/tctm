Text Classification using Topic Model (TCTM)
============================================

Text Clasification using Topic Model (TCTM) is a Java-based package for text classification tool using topic model.

TCTM is made based on MALLET toolkit. This use a SVM classifier from SVM multiclass provided by Thorsten Joachims.

This application's input is a text documents set divided by training/test, and directory should be exist per class.
First output is a topic model. Second one is bag of words(BOW) features. Third is joint with topic and BOW.
Lastly, we provide a result by accuracy. 

 Sample input directory scheme : (see the data\sampleforTutorial\ )
   - train\class1\documents (Each file are handled one data)
          \class2\documents
          \class3\documents
          - and so on ...
   - test \class1\documents
          \class2\documents
          \class3\documents
          - and so on ...
   

Quick Start Guide
-----------------

Once you have downloaded and installed TCTM, the easiest way to get started is follow below step.

0. Check directory architecture that we want to classify.
 -input
 \data\sampleforTutorial\train\* (each directory should be a class(=label) name)
 \data\sampleforTutorial\test\*  
  
1. Topic Modeling
 Make a model from a whole text corpus.
 edu.kaist.irlab.topics.tui.Text2VariedTopicModels
 --input data/sampleforTutorial/total/*
 --output-dir data/sampleforTutorial/topicmodel
 
2. Feature Set Generation
 Make a varied feature set from train and test data and topic models.
 edu.kaist.irlab.topics.tui.Text2VariedSvmLightFeatures
 --input-train-dir data/sampleforTutorial/train/*
 --input-test-dir data/sampleforTutorial/test/*
 --input-topic-dir data/sampleforTutorial/topicmodel/VTopicModel_Wi100_Di200
 --output-dir
 data/sampleforTutorial/FeatureSet_Wi100_Di200
 
3. SVM Classification per feature set
 Excute SVM Multiclass Classification
 edu.kaist.irlab.classify.tui.ExecuteSvmMulticlass
 --input-dir data/sampleforTutorial/FeatureSet_Wi100_Di200/* 
 
See more : 
https://docs.google.com/presentation/d/1emBVYeGFYF4F2Nbp9quiSrksXE-M-iNL8_-bCoiQzgo/edit#slide=id.p
 
 
