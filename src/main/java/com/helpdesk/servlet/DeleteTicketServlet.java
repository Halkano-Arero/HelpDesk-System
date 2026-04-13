package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteTicket")
public class DeleteTicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ticketId = request.getParameter("id");

        if (ticketId == null || ticketId.isEmpty()) {
            response.sendRedirect("TicketServlet?error=invalid");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "DELETE FROM tickets WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(ticketId));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                // back to tickets page or dashboard
                response.sendRedirect("TicketServlet?success=deleted");
            } else {
                response.sendRedirect("TicketServlet?error=notfound");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("TicketServlet?error=db");
        }
    }
}
