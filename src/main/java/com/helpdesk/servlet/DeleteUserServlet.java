package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        String currentUsername = String.valueOf(session.getAttribute("username"));
        String userIdParam = request.getParameter("id");
        if (userIdParam == null || userIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=validation");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            int userId = Integer.parseInt(userIdParam);
            String username = null;
            String email = null;
            String role = null;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT username, email, role FROM users WHERE id = ?")) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                        email = rs.getString("email");
                        role = rs.getString("role");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/UsersServlet?error=not_found");
                        return;
                    }
                }
            }

            if (currentUsername != null && currentUsername.equalsIgnoreCase(username)) {
                response.sendRedirect(request.getContextPath() + "/UsersServlet?error=self");
                return;
            }

            if ("admin".equalsIgnoreCase(role)) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT COUNT(*) FROM users WHERE role = 'admin'")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) <= 1) {
                            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=last_admin");
                            return;
                        }
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tickets SET assigned_to = NULL WHERE assigned_to = ?")) {
                ps.setString(1, username);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }

            String success = "deleted";
            if ("agent".equalsIgnoreCase(role)) {
                success = "deleted_agent";
            } else if ("user".equalsIgnoreCase(role)) {
                success = "deleted_user";
            }
            response.sendRedirect(request.getContextPath() + "/UsersServlet?success=" + success);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/UsersServlet?error=db");
        }
    }
}
