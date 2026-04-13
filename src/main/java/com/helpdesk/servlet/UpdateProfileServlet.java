package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.TicketNotificationService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.*;

@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String email = (String) session.getAttribute("email");
        String newPassword = InputValidator.requireText(request.getParameter("password"), "Password", 100);

        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ Get role
            String roleSql = "SELECT role FROM users WHERE email = ?";
            PreparedStatement psRole = conn.prepareStatement(roleSql);
            psRole.setString(1, email);

            ResultSet rs = psRole.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("login.jsp");
                return;
            }

            String role = rs.getString("role");
            // 2️⃣ Update password
            String updateSql = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setString(1, PasswordUtils.hash(newPassword));
            psUpdate.setString(2, email);

            psUpdate.executeUpdate();
            TicketNotificationService.notifyAccountUpdated(email, email, role);

            // 3️⃣ Redirect correct
            if ("admin".equalsIgnoreCase(role)) {
                response.sendRedirect("admin_profile.jsp?success=updated");
            } else {
                response.sendRedirect("agent_profile.jsp?success=updated");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=db");
        }
    }
}
