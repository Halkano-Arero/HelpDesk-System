package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.TicketNotificationService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/InsertTicketServlet")
public class InsertTicketServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of("New", "Open", "Answered", "Solved", "Closed");
    private static final Set<String> ALLOWED_PRIORITIES = Set.of("Low", "Medium", "High");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        String createdBy = (String) session.getAttribute("email");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO tickets (name, email, department, category, subject, description, priority, created_by, status) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, InputValidator.requireText(request.getParameter("name"), "Name", 150));
            ps.setString(2, InputValidator.requireEmail(request.getParameter("email")));
            ps.setString(3, InputValidator.requireText(request.getParameter("department"), "Department", 100));
            ps.setString(4, InputValidator.requireText(request.getParameter("category"), "Category", 100));
            ps.setString(5, InputValidator.requireText(request.getParameter("subject"), "Subject", 200));
            ps.setString(6, InputValidator.requireText(request.getParameter("description"), "Description", 2000));
            ps.setString(7, InputValidator.requireAllowedValue(request.getParameter("priority"), "Priority", ALLOWED_PRIORITIES));
            ps.setString(8, createdBy);
            ps.setString(9, InputValidator.requireAllowedValue(normalizeStatus(request.getParameter("status")), "Status", ALLOWED_STATUSES));

            if (ps.executeUpdate() == 0) {
                response.sendRedirect(request.getContextPath() + "/create_ticket.jsp?error=insert_failed");
                return;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    try (PreparedStatement update = conn.prepareStatement("UPDATE tickets SET helpdesk_id=? WHERE id=?")) {
                        update.setString(1, String.format("HP_%03d", id));
                        update.setLong(2, id);
                        update.executeUpdate();
                    }
                }
            }

            TicketNotificationService.notifyTicketCreated(
                    request.getParameter("subject"),
                    request.getParameter("category"),
                    request.getParameter("priority"),
                    createdBy,
                    null);

            response.sendRedirect(request.getContextPath() + "/DashboardServlet?msg=ticket_created");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/create_ticket.jsp?error=validation");
        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/create_ticket.jsp?error=exception");
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "New";
        }
        if ("solved".equalsIgnoreCase(status)) {
            return "Solved";
        }
        return status;
    }
}
