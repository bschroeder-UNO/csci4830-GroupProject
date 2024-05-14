package jUnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spearmint.CreateAccount;

import static org.junit.Assert.*;

import java.sql.*;

public class CreateAccountTest_getUserId {

    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    private CreateAccount createAccount;

    @Before
    public void setUp() throws SQLException {
        createAccount = new CreateAccount();
        
        // Setup for the tests
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, "existingUser");
            statement.executeUpdate();
        }
        
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            statement.setString(1, "existingUser");
            statement.setString(2, "password123");
            statement.executeUpdate();
        }
    }

    @After
    public void tearDown() throws SQLException {
        // Cleanup after the tests
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, "existingUser");
            statement.executeUpdate();
        }
    }

    @Test
    public void testGetUserId_UserExists() {
        int userId = createAccount.getUserId("existingUser");
        assertTrue(userId > 0); // Assuming IDs are positive integers
    }

    @Test
    public void testGetUserId_UserDoesNotExist() {
        int userId = createAccount.getUserId("nonExistingUser");
        assertEquals(-1, userId);
    }
}
