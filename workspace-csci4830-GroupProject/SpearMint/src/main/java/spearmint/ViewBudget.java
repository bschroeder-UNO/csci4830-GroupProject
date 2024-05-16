package spearmint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ViewBudget")
public class ViewBudget extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ViewBudget() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        double budget = 0.0;
        try {
            budget = UpdateBudget.getCurrentBudget(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Format the budget to display two decimal places
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedBudget = df.format(budget);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>View Budget</title></head><body>");
        out.println("<h2>Current Budget for " + username + ": $" + formattedBudget + "</h2>");
        out.println("<a href='Home'>Back to Home</a>");
        out.println("</body></html>");
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
