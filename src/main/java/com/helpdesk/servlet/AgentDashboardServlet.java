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

@WebServlet("/AgentDashboardServlet")
public class AgentDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !"agent".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String agentName = (String) session.getAttribute("username");

        try (Connection conn = DBConnection.getConnection()) {

            request.setAttribute("totalTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE assigned_to = ?", agentName));

            request.setAttribute("newTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='New' AND assigned_to = ?", agentName));

            request.setAttribute("openTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Open' AND assigned_to = ?", agentName));

            request.setAttribute("closedTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Closed' AND assigned_to = ?", agentName));

            request.setAttribute("answeredTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Answered' AND assigned_to = ?", agentName));

            request.setAttribute("unansweredTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Unanswered' AND assigned_to = ?", agentName));

            request.setAttribute("solvedTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE status='Solved' AND assigned_to = ?", agentName));

            request.setAttribute("ratedTickets",
                    count(conn, "SELECT COUNT(*) FROM tickets WHERE rating > 0 AND assigned_to = ?", agentName));

            // Recent tickets
            List<Ticket> recentTickets = new ArrayList<>();

            String sql = """
                SELECT id, helpdesk_id, name, subject, assigned_to, created_at, status
                FROM tickets
                WHERE assigned_to = ?
                ORDER BY created_at DESC
                LIMIT 5
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, agentName);
            ResultSet rs = ps.executeQuery();

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

            request.setAttribute("recentTickets", recentTickets);

            request.getRequestDispatcher("/dashboard_agents.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Agent dashboard error");
        }
    }

    private int count(Connection conn, String sql, String agent) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, agent);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }
}
