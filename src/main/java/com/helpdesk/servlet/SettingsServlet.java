package com.helpdesk.servlet;

import com.helpdesk.dao.SettingDao;
import com.helpdesk.db.DBConnection;
import com.helpdesk.util.InputValidator;
import com.helpdesk.util.SettingType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {

    private final SettingDao settingDao = new SettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("departments", settingDao.findEntries(SettingType.DEPARTMENT));
            request.setAttribute("categories", settingDao.findEntries(SettingType.CATEGORY));
            request.setAttribute("statuses", settingDao.findEntries(SettingType.STATUS));
            request.getRequestDispatcher("settings.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=db");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            SettingType type = SettingType.fromRequestValue(request.getParameter("type"));
            String name = InputValidator.requireText(request.getParameter("name"), "Name", 100);

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO " + type.getTableName() + " (name) VALUES (?)")) {
                ps.setString(1, name);
                ps.executeUpdate();
            }

            response.sendRedirect("SettingsServlet?success=added");
        } catch (IllegalArgumentException e) {
            response.sendRedirect("SettingsServlet?error=validation");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("SettingsServlet?error=db");
        }
    }
}
