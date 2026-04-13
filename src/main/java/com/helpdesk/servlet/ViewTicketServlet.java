package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import model.Ticket;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ViewTicketServlet")
public class ViewTicketServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        String id = req.getParameter("id");
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/TicketServlet");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM tickets WHERE id = ?";
            if ("user".equalsIgnoreCase(role)) {
                sql += " AND created_by = ?";
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(id));
                if ("user".equalsIgnoreCase(role)) {
                    pst.setString(2, String.valueOf(session.getAttribute("email")));
                }

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Ticket t = new Ticket(
                                rs.getInt("id"),
                                rs.getString("helpdesk_id"),
                                rs.getString("name"),
                                rs.getString("subject"),
                                rs.getString("assigned_to"),
                                rs.getString("created_at"),
                                rs.getString("status")
                        );

                        req.setAttribute("ticket", t);
                        req.setAttribute("email", rs.getString("email"));
                        req.setAttribute("priority", rs.getString("priority"));
                        req.setAttribute("category", rs.getString("category"));
                        req.setAttribute("description", rs.getString("description"));
                        req.setAttribute("created_by", rs.getString("created_by"));
                        req.getRequestDispatcher("/view_ticket.jsp").forward(req, resp);
                        return;
                    }
                }
            }

            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Unable to load ticket");
        }
    }
}
