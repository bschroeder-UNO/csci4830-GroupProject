package spearmint;

import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddTransactionSingle")
public class AddTransactionSingle extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    public AddTransactionSingle() {
        super();
    }

    private void addTransaction(String username, double amount, String name, String type) throws SQLException {
        String tableName = "transactions_" + username;
        String sql = "INSERT INTO " + tableName + " (amount, name, type) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
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
        out.println("<title>Add Transaction</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Add Transaction</h2>");

        // Display the add transaction form
        out.println("<form action='AddTransactionSingle' method='post'>");
        out.println("Amount: <input type='number' name='amount' step='0.01' required><br><br>");
        out.println("Name: <input type='text' name='name' required><br><br>");
        out.println("Type: <select name='type' required>");
        out.println("<option value='BILLS'>BILLS</option>");
        out.println("<option value='FOOD'>FOOD</option>");
        out.println("<option value='ENTERTAINMENT'>ENTERTAINMENT</option>");
        out.println("<option value='MISC'>MISC</option>");
        out.println("<option value='TRANSPORTATION'>TRANSPORTATION</option>");
        out.println("<option value='HEALTHCARE'>HEALTHCARE</option>");
        out.println("<option value='EDUCATION'>EDUCATION</option>");
        out.println("<option value='OTHER'>OTHER</option>");
        out.println("</select><br><br>");
        out.println("<input type='submit' value='Add Transaction'>");
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

        String amountStr = request.getParameter("amount");
        String name = request.getParameter("name");
        String type = request.getParameter("type");

        // Debugging output
        System.out.println("Received parameters - Username: " + username + ", Amount: " + amountStr + ", Name: " + name + ", Type: " + type);

        if (amountStr != null && name != null && type != null) {
            try {
                double amount = Double.parseDouble(amountStr);
                addTransaction(username, amount, name, type);
                out.println("<p>Transaction added successfully for " + username + "!</p>");
            } catch (NumberFormatException e) {
                out.println("<p style='color: red;'>Invalid amount value.</p>");
            } catch (SQLException e) {
                out.println("<p style='color: red;'>Error adding transaction: " + e.getMessage() + "</p>");
            }
        } else {
            out.println("<p style='color: red;'>All fields are required.</p>");
        }

        // Redisplay the add transaction form
        out.println("<form action='AddTransactionSingle' method='post'>");
        out.println("Amount: <input type='number' name='amount' step='0.01' required><br><br>");
        out.println("Name: <input type='text' name='name' required><br><br>");
        out.println("Type: <select name='type' required>");
        out.println("<option value='BILLS'>Bills</option>");
        out.println("<option value='FOOD'>Food</option>");
        out.println("<option value='ENTERTAINMENT'>Entertainment</option>");
        out.println("<option value='MISC'>Misc</option>");
        out.println("<option value='TRANSPORTATION'>Transportation</option>");
        out.println("<option value='HEALTHCARE'>Healthcare</option>");
        out.println("<option value='EDUCATION'>Education</option>");
        out.println("<option value='OTHER'>Other</option>");
        out.println("</select><br><br>");
        out.println("<input type='submit' value='Add Transaction'>");
        out.println("</form>");

        // Link to redirect to Home servlet
        out.println("<br><br>");
        out.println("<a href='Home?username=" + username + "'>Go to Home</a>");

        out.println("</body>");
        out.println("</html>");
    }
}
