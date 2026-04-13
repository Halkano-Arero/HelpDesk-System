package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.InputValidator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdateUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        String idParam = request.getParameter("id");
        try {
            int id = Integer.parseInt(idParam);
            String role = InputValidator.requireText(request.getParameter("role"), "Role", 20).toLowerCase();
            String category = InputValidator.requireText(request.getParameter("category"), "Category", 100);

            if (!("admin".equals(role) || "agent".equals(role) || "user".equals(role))) {
                response.sendRedirect(request.getContextPath() + "/UsersServlet?error=validation");
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String currentUsername = null;
                String currentRole = null;

                try (PreparedStatement ps = conn.prepareStatement("SELECT username, role FROM users WHERE id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=not_found");
                            return;
                        }
                        currentUsername = rs.getString("username");
                        currentRole = rs.getString("role");
                    }
                }

                if ("admin".equalsIgnoreCase(currentRole) && !"admin".equalsIgnoreCase(role)) {
                    try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'admin'")) {
                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            if (rs.getInt(1) <= 1) {
                                response.sendRedirect(request.getContextPath() + "/UsersServlet?error=last_admin");
                                return;
                            }
                        }
                    }
                }

                if (!"agent".equalsIgnoreCase(role)) {
                    category = "Other";
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE users SET role = ?, category = ? WHERE id = ?")) {
                    ps.setString(1, role);
                    ps.setString(2, category);
                    ps.setInt(3, id);
                    ps.executeUpdate();
                }

            }

            response.sendRedirect(request.getContextPath() + "/UsersServlet?success=updated");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=db");
        }
    }
}
