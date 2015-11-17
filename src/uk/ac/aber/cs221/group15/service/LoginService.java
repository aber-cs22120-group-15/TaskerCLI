package uk.ac.aber.cs221.group15.service;

import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * This service provides functionality to submit a login
 * request to the database and retrieve the token
 *
 * @author Darren White
 * @version 0.0.1
 */
public class LoginService extends Service {

	/**
	 * The method to submit in the final url
	 */
	private static final String METHOD_NAME = "login";

	/**
	 * The extra arguments needed for the method to submit.
	 * Email and password are needed to login.
	 */
	private static final String METHOD_ARGUMENTS = "&email=%s&password=%s";

	/**
	 * The key attribute to get the token value
	 */
	private static final String KEY_TOKEN = "key";

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
		return (String) getResponse().get(KEY_TOKEN);
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
		String url = String.format(URL_METHOD_TEMPLATE, METHOD_NAME) + String.format(METHOD_ARGUMENTS, email, pwd);
		// Submit the email and password
		submit(url);

		// Get the token and return it
		return getToken();
	}
}