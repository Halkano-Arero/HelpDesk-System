package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/DashboardServlet")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DBConnection.getConnection()) {

            // ===== COUNTS =====
            request.setAttribute("totalTickets", count(conn, "SELECT COUNT(*) FROM tickets"));
            request.setAttribute("newTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE status='New'"));
            request.setAttribute("openTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Open'"));
            request.setAttribute("closedTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Closed'"));
            request.setAttribute("solvedTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Solved'"));
            request.setAttribute("answeredTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Answered'"));
            request.setAttribute("unansweredTickets", count(conn,
                    "SELECT COUNT(*) FROM tickets WHERE (status='New' OR status='Open') AND (assigned_to IS NULL OR assigned_to='')"));
            request.setAttribute("ratedTickets", count(conn, "SELECT COUNT(*) FROM tickets WHERE rating > 0"));
            request.setAttribute("totalAgents", count(conn, "SELECT COUNT(*) FROM users WHERE role='agent'"));
            request.setAttribute("totalUsers", count(conn, "SELECT COUNT(*) FROM users"));

            // ===== STATUS CHART DATA =====
            Map<String, Integer> statusData = new LinkedHashMap<>();
            List<String> statusOrder = Arrays.asList("New", "Open", "Answered", "Solved", "Closed");
            for (String status : statusOrder) {
                statusData.put(status, 0);
            }
            String statusSql = "SELECT status, COUNT(*) total FROM tickets GROUP BY status";
            try (PreparedStatement ps = conn.prepareStatement(statusSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("total");
                    statusData.put(status, count);
                }
            }
            request.setAttribute("statusChart", statusData);


            // ===== RECENT TICKETS =====
            List<Ticket> recentTickets = new ArrayList<>();
            String recentSql = "SELECT id, helpdesk_id, name, subject, assigned_to, created_at, status " +
                    "FROM tickets ORDER BY created_at DESC LIMIT 5";

            try (PreparedStatement ps = conn.prepareStatement(recentSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recentTickets.add(new Ticket(
                            rs.getInt("id"),
                            rs.getString("helpdesk_id"),
                            rs.getString("name"),
                            rs.getString("subject"),
                            rs.getString("assigned_to"),
                            rs.getString("created_at"),
                            rs.getString("status")
                    ));
                }
            }
            request.setAttribute("recentTickets", recentTickets);
            request.setAttribute("categories", loadCategories(conn));
            request.setAttribute("assignmentQueue", loadAssignmentQueue(conn));

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Dashboard error");
        }
    }

    private int count(Connection conn, String sql) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private List<String> loadCategories(Connection conn) throws SQLException {
        List<String> categories = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT name FROM categories ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    private List<Map<String, Object>> loadAssignmentQueue(Connection conn) throws SQLException {
        List<Map<String, String>> agents = loadAgents(conn);
        List<Map<String, Object>> tickets = new ArrayList<>();
        String sql = "SELECT id, helpdesk_id, name, subject, category, priority, assigned_to, created_at, status "
                + "FROM tickets WHERE assigned_to IS NULL OR assigned_to='' ORDER BY created_at DESC LIMIT 8";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> ticket = new HashMap<>();
                String category = rs.getString("category");
                ticket.put("id", rs.getInt("id"));
                ticket.put("helpdeskId", rs.getString("helpdesk_id"));
                ticket.put("name", rs.getString("name"));
                ticket.put("subject", rs.getString("subject"));
                ticket.put("category", category);
                ticket.put("priority", rs.getString("priority"));
                ticket.put("assignedTo", rs.getString("assigned_to"));
                ticket.put("createdAt", rs.getString("created_at"));
                ticket.put("status", rs.getString("status"));
                ticket.put("matchingAgents", filterAgentsByCategory(agents, category));
                ticket.put("availableAgents", agents);
                tickets.add(ticket);
            }
        }

        return tickets;
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
