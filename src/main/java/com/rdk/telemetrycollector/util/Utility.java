/* 
* If not stated otherwise in this file or this component's Licenses.txt file the 
* following copyright and licenses apply:
*
* Copyright 2024 RDK Management
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*
http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
