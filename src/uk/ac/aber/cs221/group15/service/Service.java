package uk.ac.aber.cs221.group15.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class sends requests to the database server
 *
 * @author Darren White
 * @version 0.0.1
 */
public abstract class Service {

	/**
	 * The status value associated with the 'status' response for success
	 */
	public static final int STATUS_SUCCESS = 0;

	/**
	 * The status value associated with the 'status' response for error
	 */
	public static final int STATUS_ERROR = 1;

	/**
	 * The base url of the database
	 */
	protected static final String URL_BASE = "http://users.aber.ac.uk/dkm2/TaskerMAN/";

	/**
	 * The base api url with the method suffix (and %s for the method)
	 */
	protected static final String URL_METHOD_TEMPLATE = URL_BASE + "api.php?method=%s";

	/**
	 * The key attribute to get the error object
	 */
	private static final String KEY_ERROR = "error";

	/**
	 * The key attribute to get the error message
	 */
	private static final String KEY_ERROR_MESSAGE = "message";

	/**
	 * The key attribute to get the response object
	 */
	private static final String KEY_RESPONSE = "response";

	/**
	 * The key attribute to get the status object
	 */
	private static final String KEY_STATUS = "status";

	/**
	 * The value representing an error for the status
	 */
	private static final String VALUE_STATUS_ERROR = "error";

	/**
	 * The value representing success for the status
	 */
	private static final String VALUE_STATUS_SUCCESS = "success";

	/**
	 * The main JSONObject is stored here
	 */
	private JSONObject result;

	/**
	 * Gets the error message if there is one. May return
	 * null if the status is not the error status or if
	 * submit has not been called
	 *
	 * @return The error message response
	 * @link getStatus()
	 */
	public String getErrorMessage() {
		// If the status is not error, there is no error message
		if (getStatus() != STATUS_ERROR) {
			return null;
		}

		// Get the error object
		JSONObject errObj = (JSONObject) result.get(KEY_ERROR);
		// Return the error message if there is one
		return errObj != null ? (String) errObj.get(KEY_ERROR_MESSAGE) : null;
	}

	/**
	 * The response object if there is one. May return null
	 * if the status is not the success status.
	 *
	 * @return The response object
	 * @link getStatus()
	 */
	public JSONObject getResponse() {
		// If the status is not success, there is no response object
		if (getStatus() != STATUS_SUCCESS) {
			return null;
		}

		// Get the response object and return it
		return (JSONObject) result.get(KEY_RESPONSE);
	}

	/**
	 * Gets the status value after submit has been called. Can be one of
	 * STATUS_SUCCESS and STATUS_ERROR.
	 *
	 * @return A status value
	 * @link STATUS_SUCCESS
	 * @link STATUS_ERROR
	 */
	public int getStatus() {
		// Must submit before getting the status
		if (result == null) {
			throw new IllegalStateException("Must submit before querying results");
		}

		// Get the status object
		String status = (String) result.get(KEY_STATUS);

		// Return the status as an integer
		switch (status) {
			case VALUE_STATUS_SUCCESS:
				return STATUS_SUCCESS;
			case VALUE_STATUS_ERROR:
				return STATUS_ERROR;
			default:
				// No other status values - shouldn't happen
				throw new IllegalStateException("Unknown status: " + status);
		}
	}

	/**
	 * Submits the arguments and returns a status integer. Status value
	 * can be one of STATUS_SUCCESS and STATUS_ERROR.
	 *
	 * @param url The url to request data from
	 * @return The status value
	 * @throws IOException    If an I/O exception occurs
	 * @throws ParseException If a Parse exception occurs
	 * @link getStatus()
	 */
	protected int submit(String url) throws IOException, ParseException {
		// Create a new parser for json
		JSONParser parser = new JSONParser();
		// Open the stream and prepare a reader
		InputStreamReader in = new InputStreamReader(new URL(url).openStream());

		// Read the stream
		result = (JSONObject) parser.parse(in);

		// Return the status
		return getStatus();
	}
}