package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.InputValidator;
import java.io.IOException;
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

@WebServlet("/UpdateAgentCategoryServlet")
public class UpdateAgentCategoryServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String agentId = request.getParameter("id");

        try (Connection conn = DBConnection.getConnection()) {
            String category = validateCategory(conn, request.getParameter("category"));
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET category = ? WHERE id = ? AND role = 'agent'")) {
                ps.setString(1, category);
                ps.setInt(2, Integer.parseInt(agentId));
                int updated = ps.executeUpdate();
                if (updated == 0) {
                    response.sendRedirect(request.getContextPath() + "/AgentServlet?error=notfound");
                    return;
                }
            }

            response.sendRedirect(request.getContextPath() + "/AgentServlet?success=updated");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AgentServlet?error=update_failed");
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
}
