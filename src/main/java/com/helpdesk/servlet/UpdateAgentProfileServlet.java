package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.TicketNotificationService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UpdateAgentProfileServlet")
public class UpdateAgentProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String sessionEmail = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection()) {
            String email = InputValidator.requireEmail(request.getParameter("email"));
            String oldPassword = InputValidator.requireText(request.getParameter("oldPassword"), "Current password", 100);
            String newPassword = request.getParameter("newPassword");

            try (PreparedStatement check = conn.prepareStatement("SELECT password FROM users WHERE email=?")) {
                check.setString(1, sessionEmail);
                try (ResultSet rs = check.executeQuery()) {
                    if (!rs.next() || !PasswordUtils.matches(oldPassword, rs.getString("password"))) {
                        response.sendRedirect("agent_profile.jsp?error=wrongpass");
                        return;
                    }
                }
            }

            boolean updatePassword = newPassword != null && !newPassword.trim().isEmpty();
            try (PreparedStatement update = conn.prepareStatement(
                    updatePassword
                            ? "UPDATE users SET email=?, password=? WHERE email=?"
                            : "UPDATE users SET email=? WHERE email=?")) {
                update.setString(1, email);
                if (updatePassword) {
                    update.setString(2, PasswordUtils.hash(newPassword.trim()));
                    update.setString(3, sessionEmail);
                } else {
                    update.setString(2, sessionEmail);
                }
                update.executeUpdate();
            }

            session.setAttribute("email", email);
            if (updatePassword || !sessionEmail.equalsIgnoreCase(email)) {
                TicketNotificationService.notifyAccountUpdated(sessionEmail, email, "agent");
            }
            response.sendRedirect("agent_profile.jsp?success=1");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("agent_profile.jsp?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("agent_profile.jsp?error=db");
        }
    }
}
