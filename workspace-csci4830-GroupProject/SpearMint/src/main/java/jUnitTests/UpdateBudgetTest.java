package jUnitTests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;
import spearmint.UpdateBudget;

//NOTE: TESTS ARE IN PROGRESS AS DEVELOPMENT IS NEARING COMPLETION.
public class UpdateBudgetTest {
	private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

	@Test
    public void testGetCurrentBudget() throws SQLException {
        UpdateBudget updateBudget = new UpdateBudget();
        double currentBudget = updateBudget.getCurrentBudget();

        // Assert that the current budget is correct (assuming you have test data in the database)
        assertEquals(100.0, currentBudget, 0.01);
    }

    @Test
    public void testUpdateBudget() throws SQLException {
        UpdateBudget updateBudget = new UpdateBudget();
        updateBudget.updateBudget(50.0);

        // Retrieve the updated budget from the database and verify that it was updated correctly
        double updatedBudget = getCurrentBudgetFromDatabase();
        assertEquals(150.0, updatedBudget, 0.01);
    }

    @Test
    public void testInitializeBudget() throws SQLException {
        UpdateBudget updateBudget = new UpdateBudget();
        updateBudget.initializeBudget();

        // Retrieve the initialized budget from the database and verify that it was initialized correctly
        double initializedBudget = getCurrentBudgetFromDatabase();
        assertEquals(0.0, initializedBudget, 0.01);
    }

    private double getCurrentBudgetFromDatabase() throws SQLException {
        double budget = 0.0;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT budget FROM client_budget WHERE client_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, 1); // Assuming client_id 1 for testing
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        budget = resultSet.getDouble("budget");
                    }
                }
            }
        }
        return budget;
    }
}
