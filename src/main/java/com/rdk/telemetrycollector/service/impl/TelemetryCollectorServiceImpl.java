package com.rdk.telemetrycollector.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rdk.telemetrycollector.service.ITelemetryCollectorService;
import com.rdk.telemetrycollector.util.Constants;
import com.rdk.telemetrycollector.util.Utility;

@Service
public class TelemetryCollectorServiceImpl implements ITelemetryCollectorService {

	@Value("${rdkv.index}")
	private String rdkvIndex;

	@Value("${rdkb.index}")
	private String rdkbIndex;

	@Value("${elasticsearch.url}")
	private String elasticsearchURL;
	
	@Value("${elasticsearch.username}")
	private String elasticUsername;
	
	@Value("${elasticsearch.password}")
	private String elasticPassword;

	private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryCollectorServiceImpl.class);

	/**
	 * Process telemetry data in the form that is accepted at the Elastic search
	 * index
	 * 
	 * @param telemetryData
	 * @return Parsed JSON data
	 */
	@Override
	public String processTelmetryData(String telemetryData) {
		String result = null;
		JSONObject finalTelemetryData = new JSONObject();
		try {
			JSONObject telemetryJsonObject = new JSONObject(telemetryData);
			JSONArray telemetryDataArray = null;
			if (telemetryData.contains(Constants.SEARCH_RESULTS_KEYWORD)) {
				telemetryDataArray = telemetryJsonObject.getJSONArray(Constants.SEARCH_RESULTS);
			} else if (telemetryData.contains(Constants.REPORT_KEYWORD)) {
				telemetryDataArray = telemetryJsonObject.getJSONArray(Constants.REPORT);
			} else {
				LOGGER.info("Incoming JSON data is not in required format" + telemetryData);
				return result;
			}

			if (null != telemetryDataArray) {

				for (int i = 0; i < telemetryDataArray.length(); i++) {
					// Each telemetry data available as in individual JSON format
					JSONObject tmtryKeyValue = telemetryDataArray.getJSONObject(i);
					Iterator<String> keys = tmtryKeyValue.keys();
					while (keys.hasNext()) {
						// Loop to get the dynamic telemetry marker keys
						LOGGER.trace("Going to loop through the key value pairs");

						String markerKey = (String) keys.next();
						// Get the value of the dynamic key
						Object currentTelemetryValue = tmtryKeyValue.get(markerKey);
						if (currentTelemetryValue instanceof String
								&& Utility.isValidNumber(currentTelemetryValue.toString())) {
							LOGGER.trace(markerKey + "  : " + currentTelemetryValue.toString() + " is a number");
							finalTelemetryData.put(markerKey,
									Utility.convertStringToNumber(currentTelemetryValue.toString()));

						} else if (currentTelemetryValue instanceof JSONArray) {
							LOGGER.info("The JSON Key value is a json array" + currentTelemetryValue.toString());
							JSONArray jsonArray = (JSONArray) currentTelemetryValue;

							String jsonArrayKey = markerKey;
							for (int counter = 0; counter < jsonArray.length(); counter++) {
								JSONObject tmtrySubKeyValue = jsonArray.getJSONObject(counter);
								Iterator<String> subKeys = tmtrySubKeyValue.keys();

								while (subKeys.hasNext()) {
									String subOriginalMarkerKey = (String) subKeys.next();
									String subMarkerKey = jsonArrayKey + "_" + subOriginalMarkerKey;
									subMarkerKey = subMarkerKey.replace(".", "-");
									Object currentSubTelemetryValue = tmtrySubKeyValue.get(subOriginalMarkerKey);
									if (currentSubTelemetryValue instanceof String
											&& Utility.isValidNumber(currentSubTelemetryValue.toString())) {
										LOGGER.trace(
												markerKey + "  : " + currentTelemetryValue.toString() + " is a number");

										finalTelemetryData.put(subMarkerKey,
												Utility.convertStringToNumber(currentTelemetryValue.toString()));
									} else {
										finalTelemetryData.put(subMarkerKey, currentSubTelemetryValue);
									}

								}
							}
						} else {
							finalTelemetryData.put(markerKey, currentTelemetryValue);
						}

					}

				}
				LOGGER.info("Telemetry JSON data processed is " + finalTelemetryData.toString());
				result = finalTelemetryData.toString();
			} else {

				LOGGER.info("Incoming JSON data is not in required format" + telemetryData);

			}
		} catch (JSONException exception) {
			LOGGER.error("Error processing json data: {}", exception.getMessage());
			exception.printStackTrace();
		}
		return result;
	}

	/**
	 * Send the response to Elastic search index
	 * 
	 * @param telemetryData
	 * @return
	 */
	@Override
	public String sendJSONReport(String telemetryData, String platform) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String responseBody = null;
		String index = null;
		if (platform.equals(Constants.RDKV)) {
			index = rdkvIndex;
		} else if (platform.equals(Constants.RDKB)) {
			index = rdkbIndex;
		}
		HttpPost httpPost = new HttpPost(elasticsearchURL + index + Constants.ES_DOC_PRETTY);
		httpPost.setHeader(Constants.ACCEPT, Constants.APPLICATION_JSON);
		httpPost.setHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
		final String authorization = elasticUsername + ":" + elasticPassword;
		final byte[] encodedAuth = Base64.encodeBase64(authorization.getBytes(StandardCharsets.ISO_8859_1));
		final String authHeader = "Basic " + new String(encodedAuth);
		httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
		
		String jsonToBesent = telemetryData.toString();
		StringEntity stringEntity = null;
		try {
			stringEntity = new StringEntity(jsonToBesent);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error sending json data: {}", e.getMessage());
			e.printStackTrace();
			return responseBody;

		}
		httpPost.setEntity(stringEntity);
		ResponseHandler<String> responseHandler = response -> {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				LOGGER.error("Failed to upload to elastic search, with status code" + status);
				return null;
			}
		};
		try {
			responseBody = httpClient.execute(httpPost, responseHandler);
			LOGGER.info("Response from Elastic search" + responseBody);
		} catch (IOException e) {
			LOGGER.error("Failed to upload to elastic search"+ e.getMessage());
			e.printStackTrace();
		}
		return responseBody;

	}

}
