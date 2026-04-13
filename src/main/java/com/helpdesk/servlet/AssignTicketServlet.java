package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.TicketNotificationService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AssignTicketServlet")
public class AssignTicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String ticketId = request.getParameter("ticketId");
        String assignedTo = request.getParameter("assignedTo");
        String returnTo = request.getParameter("returnTo");

        if (returnTo == null || returnTo.isBlank()) {
            returnTo = "DashboardServlet";
        }

        try (Connection conn = DBConnection.getConnection()) {
            String ticketCategory = findTicketCategory(conn, ticketId);
            if (ticketCategory == null) {
                response.sendRedirect(request.getContextPath() + "/" + returnTo + "?error=ticket_not_found");
                return;
            }

            String agentCategory = findAgentCategory(conn, assignedTo);
            if (agentCategory == null) {
                response.sendRedirect(request.getContextPath() + "/" + returnTo + "?error=agent_not_found");
                return;
            }

            if (!ticketCategory.equalsIgnoreCase(agentCategory)) {
                response.sendRedirect(request.getContextPath() + "/" + returnTo + "?error=category_mismatch");
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE tickets SET assigned_to = ?, status = CASE WHEN status = 'New' THEN 'Open' ELSE status END WHERE id = ?")) {
                ps.setString(1, assignedTo);
                ps.setInt(2, Integer.parseInt(ticketId));
                ps.executeUpdate();
            }

            notifyAssignment(conn, ticketId, assignedTo);

            response.sendRedirect(request.getContextPath() + "/" + returnTo + "?success=assigned");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/" + returnTo + "?error=assign_failed");
        }
    }

    private void notifyAssignment(Connection conn, String ticketId, String assignedTo) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT subject, category, created_by FROM tickets WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(ticketId));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TicketNotificationService.notifyTicketAssigned(
                            rs.getString("subject"),
                            rs.getString("category"),
                            assignedTo,
                            rs.getString("created_by"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String findTicketCategory(Connection conn, String ticketId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT category FROM tickets WHERE id = ?")) {
            ps.setInt(1, Integer.parseInt(ticketId));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("category") : null;
            }
        }
    }

    private String findAgentCategory(Connection conn, String username) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("SELECT category FROM users WHERE username = ? AND role = 'agent'")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("category") : null;
            }
        }
    }
}
