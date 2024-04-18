
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServerQuery
 */
@WebServlet("/ServerQuery")
public class ServerQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// static String url =
	// "jdbc:mysql://ec2-52-14-183-34.us-east-2.compute.amazonaws.com/SPEARMINT";
	// static String user = "bschroeder";
	// static String password = "csci";

	// Use in debugging:
	static String url = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/myDBSchroeder";
	static String user = "bschroeder_remote";
	static String password = "csci4830";
	

	static Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ServerQuery() {
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
		response.setContentType("text/html;charset=UTF-8");

		// Establish Connection
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println("-------- MySQL JDBC Connection Testing ------------<br>");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // old:com.mysql.jdbc.Driver
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
		response.getWriter().println("MySQL JDBC Driver Registered!<br>");
		connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		if (connection != null) {
			response.getWriter().println("You made it, take control your database now!<br>");
			System.out.println("You made it, take control of your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		String file = "input.csv";
		List<String[]> rows = readCSV(file);
		insertRows(rows);
		String read = readRows();
		response.getWriter().append(read);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * Method to read csv file.
	 */
	private static List<String[]> readCSV(String filename) throws IOException {
		List<String[]> rows = new ArrayList<>();
		File file = new File(filename);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				rows.add(parts);
			}
		}
		return rows;
	}
	
	private static String readRows() {
		StringBuilder html = new StringBuilder();
		try {
	         String selectSQL = "SELECT * FROM Account_1";
	         html.append(selectSQL + "<br>");
	         html.append("------------------------------------------<br>");
	         PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
	         //preparedStatement.setString(1, thePhone);
	         ResultSet rs = preparedStatement.executeQuery();
	         while (rs.next()) {
	            String id = rs.getString("ID");
	            String firstName = rs.getString("FIRST_NAME");
	            String lastName = rs.getString("LAST_NAME");
	            String amount = rs.getString("TRANS_AMT");
	            String transType= rs.getString("TRANS_TYPE");
	            String location = rs.getString("LOCATION");
	            html.append("USER ID: " + id + ", ");
	            html.append("FIRST NAME: " + firstName + ", ");
	            html.append("LAST NAME: " + lastName + ", ");
	            html.append("USER PHONE: " + amount + ", ");
	            html.append("USER PHONE: " + transType + ", ");
	            html.append("USER EMAIL: " + location + "<br>");
	         }
	      } catch (SQLException e) {
	         e.printStackTrace();
	      }
		return html.toString();
	}
	
	private static void insertRows(List<String[]> rows){
		for(String[] row : rows) {
			String firstName = row[0];
			String lastName = row[1];
			String amount = row[2];
			String transType = row[3];
			String location = row[4];
			String insertSQL = "INSERT INTO Account_1 (id, FIRST_NAME, LAST_NAME, TRANS_AMT, LOCATION) values (default, ?, ?, ?, ?)";
			try {
				connection = null;
				//response.getWriter().println("-------- MySQL JDBC Connection Testing ------------<br>");
				//Get driver and connect to server
				try {
					Class.forName("com.mysql.cj.jdbc.Driver"); //old:com.mysql.jdbc.Driver
				} catch (ClassNotFoundException e) {
					System.out.println("Where is your MySQL JDBC Driver?");
					e.printStackTrace();
					return;
				}
				//response.getWriter().println("MySQL JDBC Driver Registered!<br>");
				connection = null;
				try {
					connection = DriverManager.getConnection(url, user, password);
				} catch (SQLException e) {
					System.out.println("Connection Failed! Check output console");
					e.printStackTrace();
					return;
				}
				if (connection != null) {
					//response.getWriter().println("You made it, take control your database now!<br>");
				} else {
					System.out.println("Failed to make connection!");
				}
				PreparedStatement preparedStmt = connection.prepareStatement(insertSQL);
				preparedStmt.setString(1, firstName);
				preparedStmt.setString(2, lastName);
				preparedStmt.setString(3, amount);
				preparedStmt.setString(4, transType);
				preparedStmt.setString(5, location);
				preparedStmt.execute();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	private static String generateHTMLTable(List<String[]> rows) {
		StringBuilder html = new StringBuilder();
		html.append("<table border=\"1\">");
		for (String[] row : rows) {
			html.append("<tr>");
			for (String cell : row) {
				html.append("<td>").append(cell).append("</td>");
			}
			html.append("</tr>");
		}
		html.append("</table>");
		return html.toString();
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
