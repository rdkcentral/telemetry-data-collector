package com.rdk.telemetrycollector.util;

public class Utility {

	/**
	 * Method to check whether a given string value is a valid number or not.
	 * 
	 * @param stringValue
	 * @return isValidNumber
	 */
	public static boolean isValidNumber(String stringValue) {

		boolean isValidNumber = true;

		try {
			Float.parseFloat(stringValue);
		} catch (NumberFormatException numberFormatException) {
			
			isValidNumber = false;
		}

		return isValidNumber;
	}
	
	
	/**
	 * Method to check whether a given string value is a valid number or not.
	 * 
	 * @param stringValue
	 * @return isValidNumber
	 */
	public static float convertStringToNumber(String stringValue) {

		float isValidNumber = 0;

		try {
			isValidNumber =Float.parseFloat(stringValue.trim());
		} catch (NumberFormatException numberFormatException) {
			
			//Do nothing
		}

		return isValidNumber;
	}

	/**
	 * Method to check whether a given string value is a valid decimal number or
	 * not.
	 * 
	 * @param stringValue
	 * @return isValidDecimalNumber
	 */
	public static boolean isValidDecimalNumber(String stringValue) {

		boolean isValidDecimalNumber = true;

		try {
			Float.parseFloat(stringValue);
		} catch (NumberFormatException numberFormatException) {
			isValidDecimalNumber = false;
		}

		return isValidDecimalNumber;
	}

   

	/**
	 * Method to check if the given value is null or not
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNull(String value) {

		return ((value == null) || (value.trim().length() == 0));
	}
}
