package jUnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spearmint.CreateAccount;

import static org.junit.Assert.*;

import java.sql.*;

public class CreateAccountTest_insertUser {

    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    private CreateAccount createAccount;

    @Before
    public void setUp() throws SQLException {
        createAccount = new CreateAccount();
        // Clean up any test data before starting
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, "testUser");
            statement.executeUpdate();
            statement.setString(1, "duplicateUser");
            statement.executeUpdate();
        }
    }

    @After
    public void tearDown() throws SQLException {
        // Clean up any test data after testing
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, "testUser");
            statement.executeUpdate();
            statement.setString(1, "duplicateUser");
            statement.executeUpdate();
        }
    }

    @Test
    public void testInsertUser_Success() {
        boolean result = createAccount.insertUser("testUser", "password123");
        assertTrue(result); // This should return true because testUser is a new user
    }

    @Test
    public void testInsertUser_Duplicate() {
        // First insertion should succeed
        boolean firstInsertion = createAccount.insertUser("duplicateUser", "password123");
        assertTrue(firstInsertion);
        
        // Second insertion should fail (assuming username is unique)
        boolean secondInsertion = createAccount.insertUser("duplicateUser", "password123");
        assertFalse(secondInsertion);
    }
}
