package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT username, email, password, role FROM users WHERE email=? AND username=?")) {

            ps.setString(1, email);
            ps.setString(2, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !PasswordUtils.matches(password, rs.getString("password"))) {
                    response.sendRedirect("login.jsp?error=invalid");
                    return;
                }

                if (PasswordUtils.isLegacyPlaintext(rs.getString("password"))) {
                    try (PreparedStatement update = conn.prepareStatement("UPDATE users SET password=? WHERE email=?")) {
                        update.setString(1, PasswordUtils.hash(password));
                        update.setString(2, email);
                        update.executeUpdate();
                    }
                }

                HttpSession existingSession = request.getSession(false);
                if (existingSession != null) {
                    existingSession.invalidate();
                }

                String role = rs.getString("role");
                HttpSession session = request.getSession(true);
                session.setAttribute("username", rs.getString("username"));
                session.setAttribute("email", rs.getString("email"));
                session.setAttribute("role", role);

                if ("admin".equalsIgnoreCase(role)) {
                    response.sendRedirect(request.getContextPath() + "/DashboardServlet");
                } else if ("agent".equalsIgnoreCase(role)) {
                    response.sendRedirect(request.getContextPath() + "/AgentDashboardServlet");
                } else if ("user".equalsIgnoreCase(role)) {
                    response.sendRedirect(request.getContextPath() + "/UserDashboardServlet");
                } else {
                    response.sendRedirect("login.jsp?error=role");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=db");
        }
    }
}
