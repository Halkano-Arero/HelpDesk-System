package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/AddSettingServlet")
public class AddSettingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String type = request.getParameter("type");
        String name = request.getParameter("name");

        String table = type + "s"; // departments, categories, statuses

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps =
                conn.prepareStatement("INSERT INTO " + table + "(name) VALUES (?)");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("SettingsServlet");
    }
}
