package uk.ac.aber.cs221.group15.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Simon Scott
 * @author Darren White
 * @version 0.0.2
 */
public class LoginServiceTest {

	/**
	 * Used for login protocols
	 */
	private LoginService loginService;

	/**
	 * {@inheritDoc}
	 */
	@Before
	public void setUp() throws Exception {
		// Create a new LoginService for login
		loginService = new LoginService();
	}

	/**
	 * Test for login with email and password
	 *
	 * @throws Exception If an exception occurs
	 */
	@Test
	public void testLogin() throws Exception {
		// Use correct email
		String email = "sis22@aber.ac.uk";
		// Use correct password
		String password = "Scott!";

		System.out.println("Login with correct credentials...");
		System.out.println("Email: " + email);
		System.out.println("Password: " + password);
		System.out.println();

		// Login with correct email & password
		String token = loginService.login(email, password);

		System.out.println("Login update check...");
		System.out.println("Token: " + token);
		System.out.println();

		// Token should be valid
		assertNotNull(token);

		// Change to incorrect email
		email = "sis@aber.ac.uk";
		// Change to incorrect password
		password = "scott1";

		System.out.println("Login with incorrect credentials...");
		System.out.println("Email: " + email);
		System.out.println("Password: " + password);
		System.out.println();

		// Login with incorrect email & password
		token = loginService.login(email, password);

		System.out.println("Login update check...");
		System.out.println("Token: " + token);
		System.out.println();

		// Token should be null
		assertNull(token);
	}
}