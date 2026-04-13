package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.TicketNotificationService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateAdminProfileServlet")
public class UpdateAdminProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String sessionEmail = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection()) {
            String newEmail = InputValidator.requireEmail(request.getParameter("email"));
            String oldPassword = InputValidator.requireText(request.getParameter("oldPassword"), "Current password", 100);
            String newPassword = request.getParameter("newPassword");

            try (PreparedStatement checkPs = conn.prepareStatement("SELECT password FROM users WHERE email=?")) {
                checkPs.setString(1, sessionEmail);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next()) {
                        response.sendRedirect("admin_profile.jsp?error=notfound");
                        return;
                    }
                    if (!PasswordUtils.matches(oldPassword, rs.getString("password"))) {
                        response.sendRedirect("admin_profile.jsp?error=wrongpassword");
                        return;
                    }
                }
            }

            boolean updatePassword = newPassword != null && !newPassword.trim().isEmpty();
            String sql = updatePassword
                    ? "UPDATE users SET email=?, password=? WHERE email=?"
                    : "UPDATE users SET email=? WHERE email=?";

            try (PreparedStatement update = conn.prepareStatement(sql)) {
                update.setString(1, newEmail);
                if (updatePassword) {
                    update.setString(2, PasswordUtils.hash(newPassword.trim()));
                    update.setString(3, sessionEmail);
                } else {
                    update.setString(2, sessionEmail);
                }

                if (update.executeUpdate() > 0) {
                    session.setAttribute("email", newEmail);
                    if (updatePassword || !sessionEmail.equalsIgnoreCase(newEmail)) {
                        TicketNotificationService.notifyAccountUpdated(sessionEmail, newEmail, "admin");
                    }
                    response.sendRedirect("admin_profile.jsp?success=1");
                } else {
                    response.sendRedirect("admin_profile.jsp?error=updatefailed");
                }
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("admin_profile.jsp?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin_profile.jsp?error=db");
        }
    }
}
