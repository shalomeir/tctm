/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.textcontrol;

import cc.mallet.util.MalletLogger;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * A <code>This Method Name</code> definition.
 * <p/>
 * <h4>Detail</h4>
 * <p/>
 * <p>Detail Description.
 *
 * @param
 * @author user (shalomeir@gmail.com)
 * @version 0.0.1 5월 16 2014
 * @since First Created
 */
public class FeaturesTuple implements Serializable, Cloneable {
    private static Logger logger = MalletLogger.getLogger(FeaturesTuple.class.getName());

    protected List<int[]> indicesXs;
    protected List<double[]> valuesXs;
    protected Integer[] capacities;                  // 개별 features 의 size 들의 배열

    protected FeaturesTuple (List<int[]> indicesXs, List<double[]> valuesXs,
                             Integer[] capacities)
    {
        this.indicesXs = indicesXs;
        this.valuesXs = valuesXs;
        this.capacities = capacities;
    }

    public List<int[]> getIndicesXs() {
        return indicesXs;
    }

    public void setIndicesXs(List<int[]> indicesXs) {
        this.indicesXs = indicesXs;
    }

    public List<double[]> getValuesXs() {
        return valuesXs;
    }

    public void setValuesXs(List<double[]> valuesXs) {
        this.valuesXs = valuesXs;
    }

    public Integer[] getCapacities() {
        return capacities;
    }

    public void setCapacities(Integer[] capacities) {
        this.capacities = capacities;
    }

    public int getSize() {
        int sumCapa = 0;
        for (int e : capacities) sumCapa += e;
        return sumCapa;
    }


    // Serialization of Instance

    private static final long serialVersionUID = 1;
    private static final int CURRENT_SERIAL_VERSION = 0;


    public void addIndicesXs(int[] indices) {
        indicesXs.add(indices);
    }

    public void addValuesXs(double[] values) {
        valuesXs.add(values);
    }

}
