package spearmint;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/AddTransactionsCSV")
@MultipartConfig
public class AddTransactionsCSV extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";

    public AddTransactionsCSV() {
        super();
    }

    private void addTransactions(String username, List<Transaction> transactions) throws SQLException {
        String tableName = "transactions_" + username;
        String sql = "INSERT INTO " + tableName + " (amount, name, type, transaction_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (Transaction transaction : transactions) {
                pstmt.setDouble(1, transaction.getAmount());
                pstmt.setString(2, transaction.getName());
                pstmt.setString(3, transaction.getType());
                pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        displayForm(response, "");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        // Retrieve the username from the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            displayForm(response, "Error: User is not logged in.");
            return;
        }

        Part filePart = request.getPart("file");
        if (filePart == null || filePart.getSize() == 0 || !filePart.getSubmittedFileName().endsWith(".csv")) {
            displayForm(response, "Error: Please upload a valid CSV file.");
            return;
        }

        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length != 4) {
                    displayForm(response, "Error: Invalid CSV format.");
                    return;
                }
                double amount = Double.parseDouble(values[0]);
                String name = values[1];
                String type = values[2];
                LocalDate date = LocalDate.parse(values[3]);
                LocalDateTime transactionDate = LocalDateTime.of(date, LocalTime.MIDNIGHT);
                transactions.add(new Transaction(amount, name, type, transactionDate));
            }
        } catch (Exception e) {
            displayForm(response, "Error parsing CSV file: " + e.getMessage());
            return;
        }

        try {
            addTransactions(username, transactions);
            displayForm(response, "Transactions added successfully for " + username + "!");
        } catch (SQLException e) {
            displayForm(response, "Error adding transactions: " + e.getMessage());
        }
    }

    private void displayForm(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Add Transactions CSV</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Add Transactions from CSV</h2>");

        if (!message.isEmpty()) {
            out.println("<p style='color: red;'>" + message + "</p>");
        }

        // Display the upload CSV form
        out.println("<form action='AddTransactionsCSV' method='post' enctype='multipart/form-data'>");
        out.println("Select CSV File: <input type='file' name='file' accept='.csv' required><br><br>");
        out.println("<input type='submit' value='Upload CSV'>");
        out.println("</form>");

        // Link to redirect to Home servlet
        out.println("<br><br>");
        out.println("<a href='Home'>Go to Home</a>");

        out.println("</body>");
        out.println("</html>");
    }
}

class Transaction {
    private double amount;
    private String name;
    private String type;
    private LocalDateTime transactionDate;

    public Transaction(double amount, String name, String type, LocalDateTime transactionDate) {
        this.amount = amount;
        this.name = name;
        this.type = type;
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
}
