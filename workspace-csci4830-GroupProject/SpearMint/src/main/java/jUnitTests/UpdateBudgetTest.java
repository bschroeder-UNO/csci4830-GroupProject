package jUnitTests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import spearmint.UpdateBudget;

public class UpdateBudgetTest {
    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    private UpdateBudget updateBudget;

    @Before
    public void setUp() throws SQLException {
        updateBudget = new UpdateBudget();
        // Ensure the user exists in the database and set a known budget
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement insertStmt = connection.prepareStatement(
                 "INSERT INTO users (username, password, budget) VALUES (?, ?, ?)")) {
            insertStmt.setString(1, "testuser");
            insertStmt.setString(2, "password"); // Assuming a password field exists
            insertStmt.setDouble(3, 100.0);
            insertStmt.executeUpdate();
        }
    }

    @After
    public void tearDown() throws SQLException {
        // Remove the test user after testing
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?")) {
            statement.setString(1, "testuser");
            statement.executeUpdate();
        }
    }

    @Test
    public void testGetCurrentBudget() throws SQLException {
        double currentBudget = UpdateBudget.getCurrentBudget("testuser");

        // Assert that the current budget is correct
        assertEquals(100.0, currentBudget, 0.01);
    }

    @Test
    public void testUpdateBudget() throws SQLException {
        updateBudget.updateBudget("testuser", 50.0);

        // Retrieve the updated budget from the database and verify that it was updated correctly
        double updatedBudget = UpdateBudget.getCurrentBudget("testuser");
        assertEquals(50.0, updatedBudget, 0.01);
    }
}
