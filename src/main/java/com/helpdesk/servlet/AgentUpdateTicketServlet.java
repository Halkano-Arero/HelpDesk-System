package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.TicketNotificationService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AgentUpdateTicketServlet")
public class AgentUpdateTicketServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of("Open", "Solved", "Closed", "Answered");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"agent".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String agentName = String.valueOf(session.getAttribute("username"));
        String status = InputValidator.requireAllowedValue(request.getParameter("status"), "Status", ALLOWED_STATUSES);
        String agentResponse = InputValidator.requireText(request.getParameter("response"), "Response", 2000);
        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE tickets SET status = ?, agent_response = ? WHERE id = ? AND assigned_to = ?")) {

            ps.setString(1, status);
            ps.setString(2, agentResponse);
            ps.setInt(3, id);
            ps.setString(4, agentName);

            if (ps.executeUpdate() > 0) {
                notifyUpdate(id);
                response.sendRedirect("AgentTicketServlet?success=updated");
            } else {
                response.sendRedirect("AgentTicketServlet?error=unauthorized");
            }
        } catch (IllegalArgumentException e) {
            response.sendRedirect("AgentTicketServlet?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AgentTicketServlet?error=db");
        }
    }

    private void notifyUpdate(int ticketId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT subject, category, status, assigned_to, created_by, agent_response FROM tickets WHERE id = ?")) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TicketNotificationService.notifyTicketUpdated(
                            rs.getString("subject"),
                            rs.getString("category"),
                            rs.getString("status"),
                            rs.getString("assigned_to"),
                            rs.getString("created_by"),
                            rs.getString("agent_response"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
