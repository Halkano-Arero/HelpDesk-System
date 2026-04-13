package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.TicketNotificationService;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/UserCreateTicketServlet")
public class UserCreateTicketServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
         // ---- Session check ----
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect("login.jsp");
            return;
        }


        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String category = request.getParameter("category");
        String priority = request.getParameter("priority");
        String assignedTo = request.getParameter("assignedTo");
        String createdBy= (String) session.getAttribute("email");

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

                response.sendRedirect(request.getContextPath() +
                        "/UserDashboardServlet?success=ticket_created");
            } else {
                response.sendRedirect("user_create_ticket.jsp?error=failed");
            }
        

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("user_create_ticket.jsp?error=db");
        }
    }
}
