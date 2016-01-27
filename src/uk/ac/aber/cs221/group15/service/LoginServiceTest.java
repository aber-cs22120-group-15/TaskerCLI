package uk.ac.aber.cs221.group15.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Simon Scott
 * @author Darren White
 * @version 0.0.1
 */
public class LoginServiceTest {

	private LoginService service;

	@Before
	public void setUp() throws Exception {
		service = new LoginService();
	}

	/**
	 * Test for login with email and password
	 *
	 * @throws Exception If an exception occurs
	 */
	@Test
	public void testLogin() throws Exception {
		String token = service.login("sis22@aber.ac.uk", "scott");
		assertNotNull(token);

		token = service.login("sis@aber.ac.uk", "scott");
		assertNull(token);
	}
}