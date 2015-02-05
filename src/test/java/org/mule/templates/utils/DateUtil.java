/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	
	public static String applyTimeZone(Date date, String format, String timeZoneOffset){
		DateFormat formatter = new SimpleDateFormat(format);
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneOffset);		
		formatter.setTimeZone(timeZone);
		return formatter.format(date);
	}
}
