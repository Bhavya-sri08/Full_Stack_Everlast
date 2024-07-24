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

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/everLast", "root", "8185");

            // Check if username already exists
            pstmt = con.prepareStatement("SELECT * FROM login WHERE email = ?");
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Username already exists
                String alertHtml = "<html><head><script>alert('User already exists, try to login once again'); window.top.location.href='login.html';</script></head><body></body></html>";

                response.setContentType("text/html");
                response.getWriter().println(alertHtml);
            } else {
                // Username does not exist, proceed with registration
                pstmt = con.prepareStatement("INSERT INTO login(email, password, name) VALUES (?, ?, ?)");
                pstmt.setString(1, email);
                pstmt.setString(2, password); // In a real application, hash the password before storing
                pstmt.setString(3, request.getParameter("name"));
                pstmt.executeUpdate();

                response.getWriter().println("Registration successful");
                response.sendRedirect("login.html");
            }
        } catch (ClassNotFoundException | SQLException e) {
            response.getWriter().println("ERROR: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                response.getWriter().println("ERROR closing resources: " + e.getMessage());
            }
        }
    }
}
