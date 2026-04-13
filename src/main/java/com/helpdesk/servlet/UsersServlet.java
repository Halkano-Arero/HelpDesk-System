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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        List<Map<String, String>> users = new ArrayList<>();
        Map<String, Integer> roleCounts = new LinkedHashMap<>();
        roleCounts.put("admin", 0);
        roleCounts.put("agent", 0);
        roleCounts.put("user", 0);

        try (Connection conn = DBConnection.getConnection()) {
            request.setAttribute("categories", loadCategories(conn));

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, username, email, role, category, created_at FROM users ORDER BY created_at DESC, id DESC");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> row = new LinkedHashMap<>();
                    row.put("id", rs.getString("id"));
                    row.put("username", rs.getString("username"));
                    row.put("email", rs.getString("email"));
                    row.put("role", rs.getString("role"));
                    row.put("category", rs.getString("category"));
                    row.put("createdAt", rs.getString("created_at"));
                    users.add(row);

                    String role = rs.getString("role");
                    roleCounts.put(role, roleCounts.getOrDefault(role, 0) + 1);
                }
            }

            request.setAttribute("users", users);
            request.setAttribute("totalUsers", users.size());
            request.setAttribute("roleCounts", roleCounts);
            request.getRequestDispatcher("users.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }

    private List<String> loadCategories(Connection conn) throws Exception {
        List<String> categories = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }
}
