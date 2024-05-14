package spearmint;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class UpdateBudget extends HttpServlet {

    // JDBC URL, username, and password of MySQL server
	private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    // Method to get the current budget from the database
    public double getCurrentBudget() throws SQLException {
        double budget = 0.0;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT budget FROM client_budget WHERE client_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, getClientId()); // You need to implement getClientId() method
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        budget = resultSet.getDouble("budget");
                    }
                }
            }
        }
        return budget;
    }

    // Method to update the budget by adding or subtracting amounts
    public void updateBudget(double amount) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "UPDATE client_budget SET budget = budget + ? WHERE client_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setDouble(1, amount);
                statement.setInt(2, getClientId()); // You need to implement getClientId() method
                statement.executeUpdate();
            }
        }
    }

    // Method to initialize the budget if no data exists in the table
    public void initializeBudget() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "INSERT INTO client_budget (client_id, budget) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, getClientId()); // You need to implement getClientId() method
                statement.setDouble(2, 0.0);
                statement.executeUpdate();
            }
        }
    }

    // Method to get the client ID (You need to implement this according to your application)
    private int getClientId() {
        // Implement according to your application logic
        return 1; // For example, return a hardcoded client ID for now
    }

    // Servlet POST method to handle adding/subtracting from the budget
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        double amount = Double.parseDouble(request.getParameter("amount"));
        String operation = request.getParameter("operation");

        try {
            if (operation.equals("add")) {
                updateBudget(amount);
            } else if (operation.equals("subtract")) {
                updateBudget(-amount);
            }
            response.getWriter().println("Budget updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error updating budget: " + e.getMessage());
        }
    }

    // Servlet initialization method
    public void init() throws ServletException {
        try {
            // Check if data exists in the table, if not, initialize the budget
            if (getCurrentBudget() == 0.0) {
                initializeBudget();
            }
        } catch (SQLException e) {
            throw new ServletException("Error initializing budget: " + e.getMessage());
        }
    }
}
