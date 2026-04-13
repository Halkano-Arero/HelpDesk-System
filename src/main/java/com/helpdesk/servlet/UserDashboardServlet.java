package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/UserDashboardServlet")
public class UserDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userEmail = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection()) {

            request.setAttribute("totalTickets",
                count(conn, "SELECT COUNT(*) FROM tickets WHERE created_by=?", userEmail));

            request.setAttribute("newTickets",
                count(conn, "SELECT COUNT(*) FROM tickets WHERE created_by=? AND status='New'", userEmail));

            request.setAttribute("openTickets",
                count(conn, "SELECT COUNT(*) FROM tickets WHERE created_by=? AND status='Open'", userEmail));

            request.setAttribute("closedTickets",
                count(conn, "SELECT COUNT(*) FROM tickets WHERE created_by=? AND status IN ('Solved','Closed')", userEmail));

            // Recent tickets
            List<Ticket> tickets = new ArrayList<>();
            String sql =
                "SELECT id, helpdesk_id, subject, priority, status, created_at " +
                "FROM tickets WHERE created_by=? ORDER BY created_at DESC LIMIT 5";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userEmail);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tickets.add(new Ticket(
                    rs.getInt("id"),
                    rs.getString("helpdesk_id"),
                    null,
                    rs.getString("subject"),
                    null,
                    rs.getString("created_at"),
                    rs.getString("status")
                ));
            }

            request.setAttribute("recentTickets", tickets);
            request.getRequestDispatcher("user_dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }

    private int count(Connection conn, String sql, String email) throws Exception {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }
}
