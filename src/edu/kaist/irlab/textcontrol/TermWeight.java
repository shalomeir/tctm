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
import cc.mallet.util.MalletLogger;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * A TermWeight class used for Term weight object.
 * <p/>
 * <h4>This class for term weighting. Similar purpose like stopwords</h4>

 *
 * @param
 * @author Seonggyu Lee (inflexer@gmail.com)
 * @version 0.0.1 9월 16 2014
 * @since First Created
 */
public class TermWeight implements Serializable, Cloneable {
    private static Logger logger = MalletLogger.getLogger(FeaturesTuple.class.getName());

    public double [] typeWeight;
    public Alphabet typeAlphabet;

    public TermWeight (double[] typeWeight, Alphabet typeAlphabet)
    {
        this.typeWeight = typeWeight;
        this.typeAlphabet = typeAlphabet;
    }

    public int getTypeSize() {
        return typeWeight.length;
    }

    // Serialization of Instance
    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;

}
