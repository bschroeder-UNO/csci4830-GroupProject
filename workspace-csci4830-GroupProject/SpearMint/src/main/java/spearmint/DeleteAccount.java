package spearmint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteAccount")
public class DeleteAccount extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    public DeleteAccount() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String htmlResponse = "<!DOCTYPE html>"
                + "<html><head><title>Delete Account</title>"
                + "<script type=\"text/javascript\">" + "function confirmDeletion() {"
                + "return confirm('Are you sure you want to delete this account?');}" + "</script></head><body><h2>Delete Account</h2>"
                + "<form action=\"DeleteAccount\" method=\"post\" onsubmit=\"return confirmDeletion();\">"
                + "<label for=\"username\">Username:</label>"
                + "<input type=\"text\" id=\"username\" name=\"username\" required>"
                + "<input type=\"submit\" value=\"Delete Account\">" + "</form></body></html>";
        response.setContentType("text/html");
        response.getWriter().write(htmlResponse);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Username is required");
            return;
        }

        try {
            if (deleteUserAndTransactionTable(username)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Account deleted successfully. <a href='login.html'>Click here to login</a>");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Account deletion failed");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error during account deletion: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean deleteUserAndTransactionTable(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            connection.setAutoCommit(false); // Start transaction

            boolean userDeleted = deleteUser(connection, username);
            boolean tableDropped = deleteTransactionTable(connection, username);

            if (userDeleted && tableDropped) {
                connection.commit(); // Commit transaction
                return true;
            } else {
                connection.rollback(); // Rollback transaction on failure
                return false;
            }
        }
    }

    private boolean deleteUser(Connection connection, String username) throws SQLException {
        String query = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private boolean deleteTransactionTable(Connection connection, String username) throws SQLException {
        String query = "DROP TABLE IF EXISTS transactions_" + username;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            return true;
        }
    }
}
