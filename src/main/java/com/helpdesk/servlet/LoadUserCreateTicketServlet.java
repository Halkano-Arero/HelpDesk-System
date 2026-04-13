package com.helpdesk.servlet;

import com.helpdesk.dao.SettingDao;
import com.helpdesk.db.DBConnection;
import com.helpdesk.util.SettingType;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/LoadUserCreateTicketServlet")
public class LoadUserCreateTicketServlet extends HttpServlet {

    private final SettingDao settingDao = new SettingDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("departments", settingDao.findNames(SettingType.DEPARTMENT));
            request.setAttribute("categories", settingDao.findNames(SettingType.CATEGORY));
            request.setAttribute("statuses", settingDao.findNames(SettingType.STATUS));
            request.getRequestDispatcher("/user_create_ticket.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("user_dashboard.jsp?error=agents_load_failed");
        }
    }
}
