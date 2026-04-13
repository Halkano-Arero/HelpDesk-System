<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Assigned Tickets</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/tickets.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div class="sidebar">
    <div class="logo">
        <img src="images/logo.png" alt="Helpdesk Logo">
        <h2>Helpdesk System</h2>
    </div>

    <ul class="nav-links">
        <li>
            <a href="${pageContext.request.contextPath}/AgentDashboardServlet">
                <i class="fa fa-home"></i> Dashboard
            </a>
        </li>
        <li class="active">
            <a href="${pageContext.request.contextPath}/AgentTicketServlet">
                <i class="fa fa-ticket"></i> My Tickets
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/AgentProfileServlet">
                <i class="fa fa-user"></i> My Profile
            </a>
        </li>
    </ul>
</div>

<main class="main-content user-main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-bell"></i>
            <span>Hello, <%= session.getAttribute("username") %></span>
        </div>
        <div class="topbar-right">
            <form action="<%= request.getContextPath() %>/LogoutServlet">
                <button type="submit" class="logout-btn"
                        onclick="return confirm('Are you sure you want to logout?');">
                    <i class="fa fa-sign-out-alt"></i> Logout
                </button>
            </form>
        </div>
    </header>

    <section class="panel-card compact-panel user-tickets-panel">
        <div class="panel-header">
            <div>
                <h3>My Assigned Tickets</h3>
                <p>Compact support queue with the current status of each ticket.</p>
            </div>
        </div>

        <div class="table-wrap">
            <table class="compact-table user-ticket-table agent-ticket-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>HelpDesk ID</th>
                    <th>Subject</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty tickets}">
                        <tr>
                            <td colspan="6">No tickets assigned to you.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${tickets}" varStatus="i">
                            <tr>
                                <td>${i.index + 1}</td>
                                <td>${t.helpdeskId}</td>
                                <td class="ticket-subject-cell">${t.subject}</td>
                                <td><span class="status-badge ${t.status}">${t.status}</span></td>
                                <td>${t.createdAt}</td>
                                <td>
                                    <a href="AgentViewTicketServlet?id=${t.id}" class="view-link">View</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>

</body>
</html>
