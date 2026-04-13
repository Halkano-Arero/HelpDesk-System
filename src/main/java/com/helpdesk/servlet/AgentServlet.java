package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AgentServlet")
public class AgentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equalsIgnoreCase(String.valueOf(session.getAttribute("role")))) {
            response.sendRedirect("login.jsp?error=unauthorized");
            return;
        }

        List<Map<String, String>> agentList = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT id, username, email, category FROM users WHERE role = 'agent' ORDER BY username");
             PreparedStatement categoryPs = conn.prepareStatement("SELECT name FROM categories ORDER BY name");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Map<String, String> agent = new HashMap<>();
                agent.put("id", rs.getString("id"));
                agent.put("username", rs.getString("username"));
                agent.put("email", rs.getString("email"));
                agent.put("category", rs.getString("category"));
                agentList.add(agent);
            }

            List<String> categories = new ArrayList<>();
            try (ResultSet categoryRs = categoryPs.executeQuery()) {
                while (categoryRs.next()) {
                    categories.add(categoryRs.getString("name"));
                }
            }

            request.setAttribute("agents", agentList);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("agents.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
