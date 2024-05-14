package jUnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spearmint.CreateAccount;

import static org.junit.Assert.*;

import java.sql.*;

public class CreateAccountTest_checkAccountExists {

	private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
	private static final String JDBC_USER = "bschroeder_remote";
	private static final String JDBC_PASSWORD = "csci4830";

	private CreateAccount createAccount;

	@Before
	public void setUp() throws SQLException {
		createAccount = new CreateAccount();
		// Insert a test user for testing
		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
			statement.setString(1, "testUser");
			statement.setString(2, "testPassword");
			statement.executeUpdate();
		}
	}

	@After
	public void tearDown() throws SQLException {
		// Remove the test user after testing
		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
			statement.setString(1, "testUser");
			statement.executeUpdate();
		}
	}

	@Test
	public void testCheckAccountExists_UserExists() {
		boolean result = createAccount.checkAccountExists("testUser");
		assertTrue(result); // This should return true because testUser was inserted in setUp()
	}

	@Test
	public void testCheckAccountExists_UserDoesNotExist() {
		boolean result = createAccount.checkAccountExists("nonExistentUser");
		assertFalse(result); // This should return false because nonExistentUser was not inserted in setUp()
	}
	
	

}
