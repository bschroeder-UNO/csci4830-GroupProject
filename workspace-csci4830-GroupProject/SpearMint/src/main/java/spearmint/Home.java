package spearmint;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@WebServlet("/Home")
public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Home() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Home</title></head><body>");
        out.println("<h2>Welcome, " + username + "</h2>");
        out.println("<form action='ViewBudget' method='POST'>"
                + "    <input type='hidden' name='username' value='" + username + "'>"
                + "    <button type='submit'>View Budget</button>"
                + "</form>"
                + "");
        out.println("<form action='UpdateBudget' method='POST'>"
                + "    <input type='hidden' name='username' value='" + username + "'>"
                + "    <button type='submit'>Update Budget</button>"
                + "</form>"
                + "");
        out.println("<form action='AddTransactionSingle' method='POST'>"
                + "    <input type='hidden' name='username' value='" + username + "'>"
                + "    <button type='submit'>Add Single Transaction</button>"
                + "</form>"
                + "");
        out.println("<form action='AddTransactionsCSV' method='GET'>" // Changed to GET
                + "    <input type='hidden' name='username' value='" + username + "'>"
                + "    <button type='submit'>Add Multiple Transactions</button>"
                + "</form>"
                + "");
        out.println("<a href='DeleteAccount'>Delete Account</a>");
        out.println("<a href='Login'>Logout</a>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
