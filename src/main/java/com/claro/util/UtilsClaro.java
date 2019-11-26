package com.claro.util;

public class UtilsClaro {
	
	private static final String START_NUMBER = "3";

	private UtilsClaro() {

	}

	public static final String formatDate(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String dayMonth = date.substring(6, 8);
		String hour = date.substring(8, 10) + ":" + date.subSequence(10, 12) + ":" + date.substring(12, 14);
		return year + "-" + month + "-" + dayMonth + "T" + hour;
	}

	public static final boolean startNumberValid(String min) {

		return min.startsWith(START_NUMBER);
	}
	
	
	
	
}
