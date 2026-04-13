package com.helpdesk.servlet;

import com.helpdesk.db.DBConnection;
import com.helpdesk.util.SettingType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteSettingServlet")
public class DeleteSettingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String type = request.getParameter("type");
        String idParam = request.getParameter("id");
        if (type == null || idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect("SettingsServlet?error=missing_params");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            SettingType settingType = SettingType.fromRequestValue(type);
            int id = Integer.parseInt(idParam);
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + settingType.getTableName() + " WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            response.sendRedirect("SettingsServlet?success=deleted");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("SettingsServlet?error=db");
        }
    }
}
