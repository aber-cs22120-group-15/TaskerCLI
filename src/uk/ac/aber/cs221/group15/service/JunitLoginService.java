package uk.ac.aber.cs221.group15.service;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.Test;

public class JunitLoginService {

	
	//working details
	//sis22@aber.ac.uk
	//scott
	
	@Test
	public void loginWorkingTest() throws IOException, ParseException {
		LoginService loginWorkingTest = new LoginService();
		
		String output = loginWorkingTest.login("sis22@aber.ac.uk", "scott");
		assertThat(output, not(equalTo(null)));
	}
	
	
	//incorrect details
	@Test
	public void loginFailTest() throws IOException, ParseException {
		LoginService loginFailTest = new LoginService();
		
		String output = loginFailTest.login("sis@aber.ac.uk", "scott");
		assertEquals(null, output);
	}

}
