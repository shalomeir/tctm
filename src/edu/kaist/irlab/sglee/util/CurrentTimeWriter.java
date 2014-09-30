/*
 * Copyright (c) 2014. Seonggyu Lee. All Rights Reserved.
 * User: Seonggyu Lee
 * Date: 14. 9. 30 오후 6:24
 * Created Date : $today.year.month.day
 * Last Modified : 14. 9. 30 오후 6:24
 * User email: shalomeir@gmail.com
 */

package edu.kaist.irlab.sglee.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentTimeWriter {
	public static String CurrentTimeWriter() {

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		return mTime;
	}
	public static String CurrentTimeNameWriter() {

		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "MM.dd_HHmmss", Locale.KOREA );
		Date currentTime = new Date ( );
		String mTime = mSimpleDateFormat.format ( currentTime );
		return mTime;
	}
	
}