package com.helpdesk.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !"user".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.getRequestDispatcher("user_profile.jsp").forward(request, response);
    }
}
