package com.rdk.telemetrycollector.service;

public interface ITelemetryCollectorService {
	
	
	/**
	 * Process telemetry data in the form that is accepted at the Elastic search index
	 * @param telemetryData
	 * @return Parsed JSON data
	 */
	String processTelmetryData(String telemetryData);
	
	/**
	 * Send the response to Elastic search index
	 * @param telemetryData
	 * @return
	 */
	String sendJSONReport(String telemetryData, String platform);



}
