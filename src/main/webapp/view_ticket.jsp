<%-- 
    Document   : view_ticket
    Created on : Nov 26, 2025, 10:39:51 AM
    Author     : HP
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>View Ticket</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/tickets.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>

<body>

<c:set var="role" value="${sessionScope.role}" />

 <!-- SIDEBAR -->
<div class="sidebar">
   <div class="logo">
        <img src="images/logo.png" alt="Helpdesk Logo">
        <h2>Helpdesk System</h2>
    </div>

    <ul class="nav-links">
        <c:choose>
            <c:when test="${role eq 'admin'}">
                <li>
                    <a href="${pageContext.request.contextPath}/DashboardServlet">
                        <i class="fa fa-home"></i> Dashboard
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/TicketServlet">
                        <i class="fa fa-ticket"></i> Tickets
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/AgentServlet">
                        <i class="fa fa-users"></i> Agents
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/AdminProfileServlet">
                        <i class="fa fa-user"></i> My Profile
                    </a>
                </li>
            </c:when>
            <c:otherwise>
                <li>
                    <a href="${pageContext.request.contextPath}/UserDashboardServlet">
                        <i class="fa fa-home"></i> Dashboard
                    </a>
                </li>
                <li class="active">
                    <a href="${pageContext.request.contextPath}/UserTicketsServlet">
                        <i class="fa fa-ticket"></i> My Tickets
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/LoadUserCreateTicketServlet">
                        <i class="fa fa-plus"></i> Create Ticket
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/UserProfileServlet">
                        <i class="fa fa-user"></i> My Profile
                    </a>
                </li>
            </c:otherwise>
        </c:choose>

    </ul>
</div>
                
<main class="main-content">
    <section class="ticket-header ticket-detail-hero">
        <div>
            <h2>Ticket Details</h2>
            <p>
                <c:choose>
                    <c:when test="${role eq 'admin'}">Admin ticket view. Review and manage the ticket from here.</c:when>
                    <c:otherwise>User ticket view. You can review your ticket and follow its progress.</c:otherwise>
                </c:choose>
            </p>
        </div>
        <div class="ticket-hero-meta">
            <span class="category-chip">${category}</span>
            <span class="status-badge ${ticket.status}">${ticket.status}</span>
        </div>
    </section>

    <section class="panel-card ticket-detail-card">
        <div class="ticket-detail-top">
            <div>
                <div class="ticket-detail-label">Helpdesk ID</div>
                <h3>${ticket.helpdeskId}</h3>
            </div>
            <div class="ticket-detail-subject">
                <div class="ticket-detail-label">Subject</div>
                <p>${ticket.subject}</p>
            </div>
        </div>

        <div class="ticket-detail-grid">
            <div class="ticket-info-tile">
                <span>Requester</span>
                <strong>${ticket.name}</strong>
                <small>${email}</small>
            </div>
            <div class="ticket-info-tile">
                <span>Category</span>
                <strong>${category}</strong>
                <small>Priority: ${priority}</small>
            </div>
            <div class="ticket-info-tile">
                <span>Assigned To</span>
                <strong><c:out value="${empty ticket.assignedTo ? 'Unassigned' : ticket.assignedTo}"/></strong>
                <small>Created by ${created_by}</small>
            </div>
            <div class="ticket-info-tile">
                <span>Created At</span>
                <strong>${ticket.createdAt}</strong>
                <small>ID #${ticket.id}</small>
            </div>
        </div>

        <div class="ticket-detail-description">
            <div class="ticket-detail-label">Description</div>
            <div class="desc-box">${description}</div>
        </div>

        <div class="ticket-detail-footer">
            <div class="ticket-detail-label">Current Status</div>
            <div class="ticket-status-row">
                <span class="status-badge ${ticket.status}">${ticket.status}</span>
                <span class="ticket-note">
                    <c:choose>
                        <c:when test="${role eq 'admin'}">Use the dashboard to reassign or delete if needed.</c:when>
                        <c:otherwise>Your ticket is visible here for reference only.</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </div>

        <div class="actions">
            <c:if test="${role eq 'admin'}">
                <a href="DeleteTicketServlet?id=${ticket.id}" class="btn red"
                   onclick="return confirm('Are you sure you want to delete?')">Delete</a>
            </c:if>
        </div>
    </section>

</main>

</body>
</html>
