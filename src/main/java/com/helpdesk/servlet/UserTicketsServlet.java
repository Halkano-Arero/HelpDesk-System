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

@WebServlet("/UserTicketsServlet")
public class UserTicketsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userEmail = (String) session.getAttribute("email");
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String sql =
                "SELECT id, helpdesk_id, name, subject, assigned_to, created_at, status " +
                "FROM tickets WHERE created_by = ? ORDER BY created_at DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, userEmail);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ticket t = new Ticket(
                        rs.getInt("id"),
                        rs.getString("helpdesk_id"),
                        rs.getString("name"),
                        rs.getString("subject"),
                        rs.getString("assigned_to"),
                        rs.getString("created_at"),
                        rs.getString("status")
                );
                tickets.add(t);
            }

            // VERY IMPORTANT (this was missing before)
            request.setAttribute("tickets", tickets);

            request.getRequestDispatcher("/user_tickets.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("user_dashboard.jsp?error=db");
        }
    }
}
