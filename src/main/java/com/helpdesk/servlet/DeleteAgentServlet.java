package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DeleteAgentServlet")
public class DeleteAgentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String agentId = request.getParameter("id");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement findAgent = conn.prepareStatement("SELECT username FROM users WHERE id = ? AND role = 'agent'")) {

            findAgent.setInt(1, Integer.parseInt(agentId));
            String username = null;
            try (java.sql.ResultSet rs = findAgent.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }

            if (username == null) {
                response.sendRedirect(request.getContextPath() + "/AgentServlet?error=notfound");
                return;
            }

            try (PreparedStatement unassign = conn.prepareStatement("UPDATE tickets SET assigned_to = NULL, status = 'New' WHERE assigned_to = ?")) {
                unassign.setString(1, username);
                unassign.executeUpdate();
            }

            int rows;
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ? AND role = 'agent'")) {
                ps.setInt(1, Integer.parseInt(agentId));
                rows = ps.executeUpdate();
            }

            if (rows > 0) {
                response.sendRedirect(request.getContextPath() + "/AgentServlet?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/AgentServlet?error=notfound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AgentServlet?error=db");
        }
    }
}
