package spearmint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateAccount
 */
@WebServlet("/CreateAccount")
public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
	private static final String JDBC_USER = "bschroeder_remote";
	private static final String JDBC_PASSWORD = "csci4830";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<html>");
		out.println("<head>");
		out.println("<title>Create Account</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h2>Create Account</h2>");

		// Check if the form was submitted
		if (request.getParameter("username") != null && request.getParameter("password") != null) {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			boolean accountExists = checkAccountExists(username);

			// Display error message if account already exists
			if (accountExists) {
				out.println("<p style='color: red;'>Account already exists.</p>");
				// Create account form
				out.println("<form action='CreateAccount' method='post'>");
				out.println("Username: <input type='text' name='username'><br>");
				out.println("Password: <input type='password' name='password'><br>");
				out.println("<input type='submit' value='Create Account'>");
				out.println("</form>");
			} else {
				// Insert username and password into users table
				if (insertUser(username, password)) {
					int userId = getUserId(username);
					if (userId != -1 && createTransactionTable(username)) {
						out.println("<p>Account created successfully.</p>");
						// Redirect to login page
						out.println("<a href='Login'>Login</a>");
					} else {
						out.println("<p style='color: red;'>Failed to create account.</p>");
						// Create account form
						out.println("<form action='CreateAccount' method='post'>");
						out.println("Username: <input type='text' name='username'><br>");
						out.println("Password: <input type='password' name='password'><br>");
						out.println("<input type='submit' value='Create Account'>");
						out.println("</form>");
					}
				}
			}
		} else {
			// Display the create account form
			out.println("<form action='CreateAccount' method='post'>");
			out.println("Username: <input type='text' name='username'><br>");
			out.println("Password: <input type='password' name='password'><br>");
			out.println("<input type='submit' value='Create Account'>");
			out.println("</form>");
		}

		out.println("</body>");
		out.println("</html>");
	}

	public boolean checkAccountExists(String username) {
		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {
			statement.setString(1, username);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next(); // True if account exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean insertUser(String username, String password) {
		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
			statement.setString(1, username);
			statement.setString(2, password);
			return statement.executeUpdate() > 0; // True if insertion successful
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getUserId(String username) {
		try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
				PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE username = ?")) {
			statement.setString(1, username);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt("id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // User not found
	}

	public boolean createTransactionTable(String username) {
	    String tableName = "transactions_" + username;
	    try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
	         Statement statement = connection.createStatement()) {
	        String createTableQuery = "CREATE TABLE " + tableName + " ("
	                + "id INT PRIMARY KEY AUTO_INCREMENT, "
	                + "amount DECIMAL(10, 2), "
	                + "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	                + "name VARCHAR(100), "
	                + "type ENUM('BILLS', 'FOOD', 'ENTERTAINMENT', 'MISC', 'TRANSPORTATION', 'HEALTHCARE', 'EDUCATION', 'OTHER')"
	                + ")";
	        statement.executeUpdate(createTableQuery);
	        return true; // Table created successfully
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

}
