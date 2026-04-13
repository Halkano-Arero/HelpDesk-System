<%--
    Document   : user_dashboard
    Created on : Dec 9, 2025, 11:39:14 AM
    Author     : HP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/create_ticket.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/tickets.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
<div class="sidebar">
    <div class="logo">
        <img src="images/logo.png" alt="Helpdesk Logo">
        <h2>Helpdesk System</h2>
    </div>
    <ul class="nav-links">
        <li class="active">
            <a href="<%= request.getContextPath() %>/UserDashboardServlet">
                <i class="fa fa-home"></i> Dashboard
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/LoadUserCreateTicketServlet">
                <i class="fa fa-plus"></i> Create Ticket
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/UserTicketsServlet">
                <i class="fa fa-ticket"></i> My Tickets
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/UserProfileServlet">
                <i class="fa fa-user"></i> My Profile
            </a>
        </li>
    </ul>
</div>

<main class="main-content user-main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-bell"></i>
            <span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "User" %></span>
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

    <section class="cards compact-cards user-summary-cards">
        <div class="card green">My Tickets <span>${totalTickets}</span></div>
        <div class="card red">New <span>${newTickets}</span></div>
        <div class="card yellow">Open <span>${openTickets}</span></div>
        <div class="card blue">Solved <span>${closedTickets}</span></div>
    </section>

    <section class="panel-card compact-panel user-tickets-panel">
        <div class="panel-header">
            <div>
                <h3>Recent Tickets</h3>
                <p>Your latest ticket activity in one compact list.</p>
            </div>
        </div>

        <div class="table-wrap">
            <table class="compact-table user-ticket-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Helpdesk ID</th>
                    <th>Subject</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty recentTickets}">
                        <tr>
                            <td colspan="6">No tickets found.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${recentTickets}" var="t" varStatus="i">
                            <tr>
                                <td>${i.index + 1}</td>
                                <td>${t.helpdeskId}</td>
                                <td class="ticket-subject-cell">${t.subject}</td>
                                <td><span class="status-badge ${t.status}">${t.status}</span></td>
                                <td>${t.createdAt}</td>
                                <td>
                                    <a class="view-link" href="ViewTicketServlet?id=${t.id}">View</a>
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

<script src="js/dashboard.js" defer></script>
</body>
</html>
