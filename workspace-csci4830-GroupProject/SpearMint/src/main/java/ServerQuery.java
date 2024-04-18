
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	//GET TABLE NAME:
    static String url = "jdbc:mysql://ec2-52-14-183-34.us-east-2.compute.amazonaws.com/SPEARMINT";
    static String user = "bschroeder";
    static String password = "csci";
    
    //Use in debugging:
    //static String url = "jdbc:mysql://ec2-52-14-191-24.us-east-2.compute.amazonaws.com:3306/myDBSchroeder";
    //static String user = "bschroeder_remote";
    //static String password = "csci4830";
    
    
    static Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServerQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		
		//Establish Connection
		response.setContentType("text/html;charset=UTF-8");
	      response.getWriter().println("-------- MySQL JDBC Connection Testing ------------<br>");
	      try {
	         Class.forName("com.mysql.cj.jdbc.Driver"); //old:com.mysql.jdbc.Driver
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

		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
