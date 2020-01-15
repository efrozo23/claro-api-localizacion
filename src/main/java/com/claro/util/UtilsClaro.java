package com.claro.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilsClaro {

	private static final String START_NUMBER = "3";
	
	private static final String OLD_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private UtilsClaro() {

	}

	public static final String formatDate(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String dayMonth = date.substring(6, 8);
		String hour = date.substring(8, 10) + ":" + date.subSequence(10, 12) + ":" + date.substring(12, 14);
		return year + "-" + month + "-" + dayMonth + " " + hour;
	}

	public static final String formatDateT(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String dayMonth = date.substring(6, 8);
		String hour = date.substring(8, 10) + ":" + date.subSequence(10, 12) + ":" + date.substring(12, 14);
		return year + "-" + month + "-" + dayMonth + "T" + hour;
	}

	public static final boolean startNumberValid(String min) {

		return min.startsWith(START_NUMBER);

	}

	public static final String convertBody(String body) {
		body = body.replaceAll("\"", "\\\"");
		return body;

	}
	
	public static final String reverseFormat(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = sdf.parse(date);
		sdf.applyPattern(NEW_FORMAT);
		return sdf.format(d);
	}

}
