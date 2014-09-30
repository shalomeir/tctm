/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.types;

public interface Labeler {
	/** Given the (presumably unlabeled) instanceToLabel, set its target field to the true label.
	 * @return true if labeling occurred successfully, false if for some reason the instance could not be labeled. */
	public boolean label(Instance instanceToLabel);
}
