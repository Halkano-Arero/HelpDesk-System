package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import com.helpdesk.util.InputValidator;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {

    private static final Set<String> ALLOWED_ROLES = Set.of("user", "agent", "admin");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DBConnection.getConnection()) {
            String username = InputValidator.requireText(request.getParameter("username"), "Username", 100);
            String email = InputValidator.requireEmail(request.getParameter("email"));
            String password = InputValidator.requireText(request.getParameter("password"), "Password", 100);
            String role = InputValidator.requireAllowedValue(request.getParameter("role"), "Account type", ALLOWED_ROLES);

            try (PreparedStatement duplicateCheck = conn.prepareStatement("SELECT id FROM users WHERE email=? OR username=?")) {
                duplicateCheck.setString(1, email);
                duplicateCheck.setString(2, username);
                try (ResultSet resultSet = duplicateCheck.executeQuery()) {
                    if (resultSet.next()) {
                        response.sendRedirect("register.jsp?error=exists");
                        return;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, PasswordUtils.hash(password));
                ps.setString(4, role);

                if (ps.executeUpdate() > 0) {
                    response.sendRedirect("login.jsp?success=registered");
                } else {
                    response.sendRedirect("register.jsp?error=failed");
                }
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("register.jsp?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=db");
        }
    }
}
