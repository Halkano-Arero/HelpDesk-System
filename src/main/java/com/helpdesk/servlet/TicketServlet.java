package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/TicketServlet")
public class TicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, Object>> ticketList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT id, helpdesk_id, name, subject, category, priority, assigned_to, created_at, status "
                    + "FROM tickets ORDER BY CASE WHEN assigned_to IS NULL OR assigned_to='' THEN 0 ELSE 1 END, created_at DESC";
            List<Map<String, String>> agents = loadAgents(conn);

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Map<String, Object> t = new HashMap<>();
                String category = rs.getString("category");
                t.put("id", rs.getInt("id"));
                t.put("helpdeskId", rs.getString("helpdesk_id"));
                t.put("name", rs.getString("name"));
                t.put("subject", rs.getString("subject"));
                t.put("category", category);
                t.put("priority", rs.getString("priority"));
                t.put("assignedTo", rs.getString("assigned_to"));
                t.put("createdAt", rs.getString("created_at"));
                t.put("status", rs.getString("status"));
                t.put("matchingAgents", filterAgentsByCategory(agents, category));
                t.put("availableAgents", agents);
                ticketList.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("tickets", ticketList);
        request.getRequestDispatcher("tickets.jsp").forward(request, response);
    }

    private List<Map<String, String>> loadAgents(Connection conn) throws SQLException {
        List<Map<String, String>> agents = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id, username, email, category FROM users WHERE role='agent' ORDER BY username");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> agent = new HashMap<>();
                agent.put("id", rs.getString("id"));
                agent.put("username", rs.getString("username"));
                agent.put("email", rs.getString("email"));
                agent.put("category", rs.getString("category"));
                agents.add(agent);
            }
        }
        return agents;
    }

    private List<Map<String, String>> filterAgentsByCategory(List<Map<String, String>> agents, String category) {
        List<Map<String, String>> matches = new ArrayList<>();
        for (Map<String, String> agent : agents) {
            if (category != null && category.equals(agent.get("category"))) {
                matches.add(agent);
            }
        }
        return matches;
    }
}
