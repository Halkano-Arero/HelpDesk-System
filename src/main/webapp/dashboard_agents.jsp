<%-- 
    Document   : dashboard_agents
    Created on : Dec 20, 2025, 10:53:05 AM
    Author     : HP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Agent Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- CSS -->
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/agent.css">

    <!-- Icons -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>

<body>

<!-- SIDEBAR -->
<div class="sidebar">
     <div class="logo">
        <img src="images/logo.png" alt="Helpdesk Logo">
        <h2>Helpdesk System</h2>
    </div>

    <ul class="nav-links">
        <li class="active">
            <a href="${pageContext.request.contextPath}/AgentDashboardServlet">
                <i class="fa fa-home"></i> Dashboard
            </a>
        </li>

        <li>
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

<!-- MAIN CONTENT -->
<main class="main-content">
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


    <!-- DASHBOARD CARDS -->
    <section class="cards">

        <div class="card green">
            TOTAL TICKETS
            <span>${totalTickets}</span>
        </div>

        <div class="card red">
            NEW TICKETS
            <span>${newTickets}</span>
        </div>

        <div class="card yellow">
            OPEN TICKETS
            <span>${openTickets}</span>
        </div>

        <div class="card blue">
            CLOSED TICKETS
            <span>${closedTickets}</span>
        </div>

        <div class="card blue">
            ANSWERED TICKETS
            <span>${answeredTickets}</span>
        </div>

        <div class="card orange">
            UNANSWERED TICKETS
            <span>${unansweredTickets}</span>
        </div>

        <div class="card green">
            SOLVED TICKETS
            <span>${solvedTickets}</span>
        </div>

        <div class="card orange">
            RATED TICKETS
            <span>${ratedTickets}</span>
        </div>

    </section>

    <!-- RECENT TICKETS -->
    <section class="tickets">
        <h3>Recent Tickets</h3>

        <table>
            <thead>
            <tr>
                <th>#</th>
                <th>Help Desk ID</th>
                <th>Name</th>
                <th>Subject</th>
                <th>Assigned To</th>
                <th>Updated</th>
                <th>Action</th>
                <th>Status</th>
            </tr>
            </thead>

            <tbody>
            <c:choose>
                <c:when test="${empty recentTickets}">
                    <tr>
                        <td colspan="8">No tickets assigned to you.</td>
                    </tr>
                </c:when>

                <c:otherwise>
                    <c:forEach var="t" items="${recentTickets}" varStatus="i">
                        <tr>
                            <td>${i.index + 1}</td>
                            <td>${t.helpdeskId}</td>
                            <td>${t.name}</td>
                            <td>${t.subject}</td>
                            <td>${t.assignedTo}</td>
                            <td>${t.createdAt}</td>

                            <td>
                                <a class="btn-view"
                                   href="${pageContext.request.contextPath}/AgentViewTicketServlet?id=${t.id}">
                                    View
                                </a>
                            </td>

                            <td>
                                <span class="status ${t.status}">
                                    ${t.status}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </section>

</main>

</body>
</html>
