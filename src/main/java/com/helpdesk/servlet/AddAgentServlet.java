package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.security.PasswordUtils;
import com.helpdesk.util.InputValidator;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AddAgentServlet")
public class AddAgentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String username = InputValidator.requireText(request.getParameter("username"), "Username", 100);
            String email = InputValidator.requireEmail(request.getParameter("email"));
            String password = InputValidator.requireText(request.getParameter("password"), "Password", 100);
            String category = validateCategory(conn, request.getParameter("category"));

            try (PreparedStatement duplicateCheck = conn.prepareStatement(
                    "SELECT id FROM users WHERE email=? OR username=?")) {
                duplicateCheck.setString(1, email);
                duplicateCheck.setString(2, username);
                try (ResultSet resultSet = duplicateCheck.executeQuery()) {
                    if (resultSet.next()) {
                        response.sendRedirect(buildRedirect("exists", username, email, category, request));
                        return;
                    }
                }
            }

            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO users (username, email, password, role, category) VALUES (?, ?, ?, 'agent', ?)")) {
                pst.setString(1, username);
                pst.setString(2, email);
                pst.setString(3, PasswordUtils.hash(password));
                pst.setString(4, category);
                pst.executeUpdate();
            }

            response.sendRedirect("AgentServlet?success=created");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(buildRedirect("validation",
                    request.getParameter("username"),
                    request.getParameter("email"),
                    request.getParameter("category"),
                    request));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(buildRedirect("error",
                    request.getParameter("username"),
                    request.getParameter("email"),
                    request.getParameter("category"),
                    request));
        }
    }

    private String validateCategory(Connection conn, String categoryValue) throws Exception {
        Set<String> categories = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return InputValidator.requireAllowedValue(categoryValue, "Agent category", categories);
    }

    private String buildRedirect(String error, String username, String email, String category, HttpServletRequest request) {
        return request.getContextPath() + "/AgentServlet?error=" + encode(error)
                + "&username=" + encode(username)
                + "&email=" + encode(email)
                + "&category=" + encode(category);
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}
