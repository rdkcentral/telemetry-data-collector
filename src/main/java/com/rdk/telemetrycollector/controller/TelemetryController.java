package com.rdk.telemetrycollector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.rdk.telemetrycollector.service.ITelemetryCollectorService;
import com.rdk.telemetrycollector.util.Constants;
import com.rdk.telemetrycollector.util.Utility;

@RestController
public class TelemetryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryController.class);


	@Autowired
	ITelemetryCollectorService telemetryCollectorService;

	/**
	 * Controller that accepts Telemetry data for rdkv
	 * 
	 * @param telemetryData
	 * @return response
	 */
	@PostMapping("/rdkv-collector")
	public ResponseEntity<String> addTelemetryDataForRDKV(@RequestBody String telemetryData) {
		LOGGER.info("Raw incoming request JSON for RDKV" + telemetryData);
		String finalTelemetryData = telemetryCollectorService.processTelmetryData(telemetryData);
		if (!Utility.isNull(finalTelemetryData)) {
			String response = telemetryCollectorService.sendJSONReport(finalTelemetryData, Constants.RDKV);
			if (!Utility.isNull(response)) {
				LOGGER.info("Upload for RDKV is successful" + telemetryData);
				return new ResponseEntity<String>(Constants.UPLOAD_SUCCESS_MSG, HttpStatus.OK);
			} else {
				LOGGER.error("Upload for RDKV is failed");
				return new ResponseEntity<String>(Constants.UPLOAD_FAILED_MSG, HttpStatus.BAD_REQUEST);
			}
		} else {
			LOGGER.error("Processing for RDKB is failed, final Telmetry data is null");
		}
		return new ResponseEntity<String>(Constants.PROCESSING_FAILED_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Controller that accepts Telemetry data for rdkb
	 * 
	 * @param telemetryData
	 * @return
	 */
	@PostMapping("/rdkb-collector")
	public ResponseEntity<String> addTelemetryDataForRDKB(@RequestBody String telemetryData) {
		LOGGER.info("Raw incoming request JSON for RDKB" + telemetryData);

		String finalTelemetryData = telemetryCollectorService.processTelmetryData(telemetryData);
		if (!Utility.isNull(finalTelemetryData)) {
			String response = telemetryCollectorService.sendJSONReport(finalTelemetryData, Constants.RDKB);
			if (!Utility.isNull(response)) {
				LOGGER.info("Upload for RDKB is successful" + finalTelemetryData);
				return new ResponseEntity<String>(Constants.UPLOAD_SUCCESS_MSG, HttpStatus.OK);
			} else {
				LOGGER.error("Upload for RDKB is failed");
				return new ResponseEntity<String>(Constants.UPLOAD_FAILED_MSG, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOGGER.error("Processing for RDKB is failed, final Telmetry data is null");
		}
		return new ResponseEntity<String>(Constants.PROCESSING_FAILED_MSG, HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
