package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.TicketNotificationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/CreateTicketServlet")
public class CreateTicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String priority = request.getParameter("priority");
        String assignedTo = request.getParameter("assignedTo");

        HttpSession session = request.getSession(false);
        String createdBy = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection()) {

            String sql =
                "INSERT INTO tickets (subject, description, category, priority, assigned_to, created_by, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'New')";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, subject);
            ps.setString(2, description);
            ps.setString(3, category);
            ps.setString(4, priority);
            ps.setString(5, assignedTo);
            ps.setString(6, createdBy);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                TicketNotificationService.notifyTicketCreated(subject, category, priority, createdBy, assignedTo);

                response.sendRedirect("dashboard.jsp?success=ticket_created");
            } else {
                response.sendRedirect("create_ticket.jsp?error=failed");
            }

            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create_ticket.jsp?error=db");
        }
    }
}
