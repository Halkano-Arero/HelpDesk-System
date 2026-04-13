package com.helpdesk.filter;

import java.io.IOException;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/",
            "/index.html",
            "/login.jsp",
            "/register.jsp",
            "/login",
            "/LoginServlet",
            "/RegisterServlet"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
            return;
        }

        String role = String.valueOf(session.getAttribute("role"));
        if ((path.startsWith("/AgentDashboardServlet")
                || path.startsWith("/AgentTicketServlet")
                || path.startsWith("/AgentViewTicketServlet")
                || path.startsWith("/AgentUpdateTicketServlet")
                || path.startsWith("/AgentProfileServlet")
                || path.startsWith("/UpdateAgentProfileServlet")
                || path.startsWith("/dashboard_agents.jsp")
                || path.startsWith("/agent_"))
                && !"agent".equalsIgnoreCase(role)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }
        if ((path.startsWith("/UserDashboardServlet")
                || path.startsWith("/UserTicketsServlet")
                || path.startsWith("/UserProfileServlet")
                || path.startsWith("/UserCreateTicketServlet")
                || path.startsWith("/UserInsertTicketServlet")
                || path.startsWith("/LoadUserCreateTicketServlet")
                || path.startsWith("/UpdateUserProfileServlet")
                || path.startsWith("/user_")) && !"user".equalsIgnoreCase(role)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }
        if (path.startsWith("/ViewTicketServlet")) {
            if (!"admin".equalsIgnoreCase(role) && !"user".equalsIgnoreCase(role)) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=unauthorized");
                return;
            }
        } else if ((path.startsWith("/DashboardServlet")
                || path.startsWith("/TicketServlet")
                || path.startsWith("/LoadCreateTicketServlet")
                || path.startsWith("/InsertTicketServlet")
                || path.startsWith("/UsersServlet")
                || path.startsWith("/UpdateUserServlet")
                || path.startsWith("/DeleteUserServlet")
                || path.startsWith("/ReportsServlet")
                || path.startsWith("/AgentServlet")
                || path.startsWith("/AddAgentServlet")
                || path.startsWith("/DeleteAgentServlet")
                || path.startsWith("/UpdateAgentCategoryServlet")
                || path.startsWith("/AssignTicketServlet")
                || path.startsWith("/SettingsServlet")
                || path.startsWith("/AddSettingServlet")
                || path.startsWith("/DeleteSettingServlet")
                || path.startsWith("/AdminProfileServlet")
                || path.startsWith("/UpdateAdminProfileServlet")
                || path.startsWith("/create_ticket.jsp")
                || path.startsWith("/dashboard.jsp")
                || path.startsWith("/users.jsp")
                || path.startsWith("/tickets.jsp")
                || path.startsWith("/reports.jsp")
                || path.startsWith("/agents.jsp")
                || path.startsWith("/settings.jsp")
                || path.startsWith("/admin_profile.jsp"))
                && !"admin".equalsIgnoreCase(role)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?error=unauthorized");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/");
    }
}
