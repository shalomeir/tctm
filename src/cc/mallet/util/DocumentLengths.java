/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.util;

import cc.mallet.types.*;
import java.util.logging.*;
import java.io.*;

public class DocumentLengths {

	protected static Logger logger = MalletLogger.getLogger(DocumentLengths.class.getName());

	static CommandOption.String inputFile = new CommandOption.String
		(DocumentLengths.class, "input", "FILENAME", true, null,
		 "Filename for the input instance list", null);
		
	public static void main(String[] args) throws Exception {

		CommandOption.setSummary (DocumentLengths.class,
								  "Print the length of FeatureSequences in an instance list");
		CommandOption.process (DocumentLengths.class, args);

		InstanceList instances = InstanceList.load (new File(inputFile.value));
		for (Instance instance: instances) {
			if (! (instance.getData() instanceof FeatureSequence)) {
				System.err.println("DocumentLengths is only applicable to FeatureSequence objects (use --keep-sequence when importing)");
				System.exit(1);
			}
			
			FeatureSequence words = (FeatureSequence) instance.getData();
			System.out.println(words.size());
		}
	}
}