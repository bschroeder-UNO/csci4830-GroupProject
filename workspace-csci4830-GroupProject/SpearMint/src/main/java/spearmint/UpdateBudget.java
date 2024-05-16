package spearmint;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateBudget")
public class UpdateBudget extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    // Method to get the current budget from the database
    public static double getCurrentBudget(String username) throws SQLException {
        double budget = 0.0;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT budget FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        budget = resultSet.getDouble("budget");
                    }
                }
            }
        }
        return budget;
    }

    // Method to update the budget
    public void updateBudget(String username, double newBudget) throws SQLException {
        String sql = "UPDATE users SET budget = ? WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBudget);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Update Budget</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Update Budget</h2>");

        // Display the update budget form
        out.println("<form action='UpdateBudget' method='post'>");
        out.println("New Budget: <input type='number' name='newBudget' step='0.01' required><br><br>");
        out.println("<input type='submit' value='Update Budget'>");
        out.println("</form>");

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Retrieve the username from the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            out.println("<p style='color: red;'>Error: User is not logged in.</p>");
            return;
        }

        String newBudgetStr = request.getParameter("newBudget");

        // Debugging output
        System.out.println("Received parameters - Username: " + username + ", New Budget: " + newBudgetStr);

        if (newBudgetStr != null) {
            try {
                double newBudget = Double.parseDouble(newBudgetStr);
                updateBudget(username, newBudget);
                out.println("<p>Budget updated successfully for " + username + "!</p>");
            } catch (NumberFormatException e) {
                out.println("<p style='color: red;'>Invalid budget value.</p>");
            } catch (SQLException e) {
                out.println("<p style='color: red;'>Error updating budget: " + e.getMessage() + "</p>");
            }
        } else {
            out.println("<p style='color: red;'>New budget value is missing.</p>");
        }

        // Display the update budget form again
        out.println("<form action='UpdateBudget' method='post'>");
        out.println("New Budget: <input type='number' name='newBudget' step='0.01' required><br><br>");
        out.println("<input type='submit' value='Update Budget'>");
        out.println("</form>");

        // Link to redirect to Home servlet
        out.println("<br><br>");
        out.println("<a href='Home?username=" + username + "'>Return Home</a>");

        out.println("</body>");
        out.println("</html>");
    }
}
