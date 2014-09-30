/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 6. 12 오전 10:00
 * Created Date : $today.year.month.day
 * Last Modified : 14. 6. 12 오전 10:00
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;

/**
 * A <code>This Method Name</code> definition.
 * <p/>
 * <h4>Detail</h4>
 * <p/>
 * <p>Detail Description.
 *
 * @param
 * @author user (shalomeir@gmail.com)
 * @version 0.0.1 6월 12 2014
 * @since First Created
 */
public class TopPropComparator implements java.util.Comparator<String> {
    public int compare(String o1, String o2) {
        return o2.compareTo(o1);
    }

}
