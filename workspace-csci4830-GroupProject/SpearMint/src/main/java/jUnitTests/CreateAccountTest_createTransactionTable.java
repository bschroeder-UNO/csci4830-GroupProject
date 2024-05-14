package jUnitTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import spearmint.CreateAccount;

import static org.junit.Assert.*;

import java.sql.*;

public class CreateAccountTest_createTransactionTable {

    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    private CreateAccount createAccount;

    @Before
    public void setUp() throws SQLException {
        createAccount = new CreateAccount();
    }

    @After
    public void tearDown() throws SQLException {
        // Cleanup after the tests
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS transactions_1");
        }
    }

    @Test
    public void testCreateTransactionTable_Success() {
        boolean result = createAccount.createTransactionTable(1);
        assertTrue(result);

        // Verify that the table was created
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES LIKE 'transactions_1'")) {
            assertTrue(resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Table creation verification failed.");
        }
    }

    @Test
    public void testCreateTransactionTable_Failure() {
        boolean result = createAccount.createTransactionTable(1);
        assertTrue(result);

        // Try to create the table again, which should fail
        boolean secondResult = createAccount.createTransactionTable(1);
        assertFalse(secondResult);
    }
}
