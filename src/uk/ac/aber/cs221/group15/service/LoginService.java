package uk.ac.aber.cs221.group15.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * This service provides functionality to submit a login
 * request to the database and retrieve the token
 *
 * @author Darren White
 * @version 0.0.5
 */
public class LoginService extends Service {

	/**
	 * The method to submit in the final url with the
	 * email and password arguments
	 */
	private static final String URL_LOGIN = URL_API +
			"?method=login&email=%s&password=%s";

	/**
	 * The key attribute to get the token value
	 */
	private static final String KEY_USER_TOKEN = "key";

	/**
	 * Gets the user token for future database requests
	 *
	 * @return The unique token/key for the user
	 */
	public String getToken() {
		// No token if there was an error
		if (getStatus() != STATUS_SUCCESS) {
			return null;
		}

		// Get the token and return it
		return (String) ((JSONObject) getResponse()).get(KEY_USER_TOKEN);
	}

	/**
	 * Submits a login request and returns the token. The token can be
	 * null if an error occurred.
	 *
	 * @param email The email to submit
	 * @param pwd   The password to submit
	 * @return The unique token/key for the user
	 * @throws IOException    If an I/O exception occurs
	 * @throws ParseException If a Parse exception occurs
	 * @link Service.submit()
	 * @see Service
	 */
	public String login(String email, String pwd) throws IOException, ParseException {
		// Create the final url with the method, email, and password
		// Encode the email and password for the url
		String url = String.format(URL_LOGIN, encode(email), encode(pwd));

		// Submit the email and password
		if (submit(url) == STATUS_SUCCESS) {
			// Get the token and return it
			return getToken();
		} else {
			// Error submitting credentials
			return null;
		}
	}
}