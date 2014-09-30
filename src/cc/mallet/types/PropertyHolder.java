/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:22
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:22
 * User email: shalomeir@gmail.com
 */

package cc.mallet.types;

import cc.mallet.util.PropertyList;

public interface PropertyHolder {
	public void setProperty(String key, Object value);

	public Object getProperty(String key);

	public void setNumericProperty(String key, double value);

	public double getNumericProperty(String key);

	public PropertyList getProperties();

	public void setProperties(PropertyList newProperties);

	public boolean hasProperty(String key);

	public void setFeatureValue(String key, double value);

	public double getFeatureValue(String key);

	public PropertyList getFeatures();

	public void setFeatures(PropertyList pl);
	
	public FeatureVector toFeatureVector(Alphabet dict, boolean binary);
}
