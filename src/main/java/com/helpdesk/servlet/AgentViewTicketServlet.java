package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
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

@WebServlet("/AgentViewTicketServlet")
public class AgentViewTicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"agent".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        String agentName = String.valueOf(session.getAttribute("username"));

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, helpdesk_id, subject, description, priority, status, agent_response FROM tickets WHERE id = ? AND assigned_to = ?")) {

            ps.setInt(1, id);
            ps.setString(2, agentName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    request.setAttribute("ticketId", rs.getInt("id"));
                    request.setAttribute("helpdeskId", rs.getString("helpdesk_id"));
                    request.setAttribute("subject", rs.getString("subject"));
                    request.setAttribute("description", rs.getString("description"));
                    request.setAttribute("priority", rs.getString("priority"));
                    request.setAttribute("status", rs.getString("status"));
                    request.setAttribute("agentResponse", rs.getString("agent_response"));
                    request.getRequestDispatcher("agent_view_ticket.jsp").forward(request, response);
                    return;
                }
            }

            response.sendRedirect("AgentTicketServlet?error=notfound");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AgentTicketServlet?error=db");
        }
    }
}
