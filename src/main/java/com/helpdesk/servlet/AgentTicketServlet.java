package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AgentTicketServlet")
public class AgentTicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String agentName = (String) session.getAttribute("username");

        List<Ticket> tickets = new ArrayList<>();

        String sql = "SELECT id, helpdesk_id, name, subject, priority, status, created_at " +
                     "FROM tickets WHERE assigned_to = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, agentName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tickets.add(new Ticket(
                        rs.getInt("id"),
                        rs.getString("helpdesk_id"),
                        rs.getString("name"),
                        rs.getString("subject"),
                        agentName,
                        rs.getString("created_at"),
                        rs.getString("status")
                        
                        
                ));
            }

            request.setAttribute("tickets", tickets);
            request.getRequestDispatcher("/agent_ticket.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Unable to load agent tickets");
        }
    }
}
