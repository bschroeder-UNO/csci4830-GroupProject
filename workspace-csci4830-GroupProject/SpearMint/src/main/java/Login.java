

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.PrintWriter;
import java.sql.*;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-183-34.us-east-2.compute.amazonaws.com";
	//private static final String JDBC_USER = "bschroeder"
	//private static final String JDBC_PASSWORD = "csci";
	private static final String JDBC_URL = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/SPEARMINT";
    private static final String JDBC_USER = "bschroeder_remote";
    private static final String JDBC_PASSWORD = "csci4830";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		try {
	         Class.forName("com.mysql.cj.jdbc.Driver"); //old:com.mysql.jdbc.Driver
	      } catch (ClassNotFoundException e) {
	         System.out.println("Where is your MySQL JDBC Driver?");
	         e.printStackTrace();
	         return;
	      }
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();

	    out.println("<html>");
	    out.println("<head>");
	    out.println("<title>Login</title>");
	    out.println("</head>");
	    out.println("<body>");
	    out.println("<h2>Login</h2>");

	    // Check if the form was submitted
	    if (request.getParameter("username") != null && request.getParameter("password") != null) {
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	        boolean isValid = validateCredentials(username, password);
	        // Display error message if login failed
	        if (!isValid) {
	            out.println("<p style='color: red;'>Invalid username or password.</p>");
	            // Login form
		        out.println("<form action='Login' method='post'>");
		        out.println("Username: <input type='text' name='username'><br>");
		        out.println("Password: <input type='password' name='password'><br>");
		        out.println("<input type='submit' value='Login'>");
		        out.println("</form>");
	        } else {
	        	HttpSession session = request.getSession();
	        	session.setAttribute("username", username);
	        	response.sendRedirect("Home");
	        }
	        
	    } else {
	        // Display the login form
	        out.println("<form action='Login' method='post'>");
	        out.println("Username: <input type='text' name='username'><br>");
	        out.println("Password: <input type='password' name='password'><br>");
	        out.println("<input type='submit' value='Login'>");
	        out.println("</form>");
	    }

	    out.println("</body>");
	    out.println("</html>");
    }

    private boolean validateCredentials(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // True if user exists with given credentials
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
