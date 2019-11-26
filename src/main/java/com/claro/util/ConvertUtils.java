package com.claro.util;

/*
 * Claro
 */
public final class ConvertUtils {

	private static final String SOUTH = "s";
	private static final String WHEST = "w";
	private static final long CONSTANT_MINUTES = 60;
	private static final long NEGATIVE = -1;

	private ConvertUtils() {

	}

	public static double convertDecimal(long degrees, long minutes, double seconds, String direction) {
		if (direction.equalsIgnoreCase(SOUTH) || direction.equalsIgnoreCase(WHEST)) {
			return (degrees + ((minutes + (seconds / CONSTANT_MINUTES)) / CONSTANT_MINUTES)) * NEGATIVE;
		} else {
			return (degrees + ((minutes + (seconds / CONSTANT_MINUTES)) / CONSTANT_MINUTES));
		}
	}
}
