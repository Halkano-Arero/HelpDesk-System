package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AdminProfileServlet")
public class AdminProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String email = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, email FROM users WHERE email = ? AND role = 'admin'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                request.setAttribute("username", rs.getString("username"));
                request.setAttribute("email", rs.getString("email"));
            }

            request.getRequestDispatcher("admin_profile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=db");
        }
    }
}
