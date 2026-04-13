<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tickets</title>
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
        <li><a href="<%= request.getContextPath() %>/DashboardServlet"><i class="fa fa-home"></i> Dashboard</a></li>
        <li class="active"><a href="<%= request.getContextPath() %>/TicketServlet"><i class="fa fa-ticket"></i> Tickets</a></li>
        <li><a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a></li>
        <li><a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a></li>
        <li><a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a></li>
        <li><a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
</div>

<main class="main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-ticket"></i>
            <span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Admin" %></span>
        </div>
    </header>

    <c:if test="${param.success == 'deleted'}">
        <div class="page-alert success">Ticket deleted successfully.</div>
    </c:if>
    <c:if test="${param.success == 'assigned'}">
        <div class="page-alert success">Ticket assigned successfully.</div>
    </c:if>
    <c:if test="${param.error == 'category_mismatch'}">
        <div class="page-alert error">The selected agent does not match that ticket category.</div>
    </c:if>

    <section class="ticket-header">
        <h2>Tickets</h2>
        <p>Brief queue view. Open a ticket for full details.</p>
    </section>

    <section class="panel-card compact-panel admin-ticket-panel">
        <div class="table-wrap">
            <table class="compact-table admin-queue-table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Helpdesk ID</th>
                    <th>Subject</th>
                    <th>Category</th>
                    <th>Assigned</th>
                    <th>Status</th>
                    <th>Assign</th>
                    <th>Open</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty tickets}">
                        <tr><td colspan="8">No tickets found.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="t" items="${tickets}" varStatus="i">
                            <tr>
                                <td>${i.index + 1}</td>
                                <td>${t.helpdeskId}</td>
                                <td class="ticket-subject-cell">${t.subject}</td>
                                <td><span class="category-chip">${t.category}</span></td>
                                <td><c:out value="${empty t.assignedTo ? 'Unassigned' : t.assignedTo}"/></td>
                                <td><span class="status-badge ${t.status}">${t.status}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty t.assignedTo}">
                                            <c:choose>
                                                <c:when test="${empty t.matchingAgents}">
                                                    <span class="no-agent-hint">No match</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="<%= request.getContextPath() %>/AssignTicketServlet" method="post" class="ticket-inline-assign">
                                                        <input type="hidden" name="ticketId" value="${t.id}">
                                                        <input type="hidden" name="returnTo" value="TicketServlet">
                                                        <select name="assignedTo" required>
                                                            <option value="">Assign</option>
                                                            <c:forEach var="agent" items="${t.availableAgents}">
                                                                <c:if test="${agent.category eq t.category}">
                                                                    <option value="${agent.username}">${agent.username}</option>
                                                                </c:if>
                                                            </c:forEach>
                                                        </select>
                                                        <button type="submit" class="assign-btn">Go</button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge Assigned">Assigned</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-links">
                                        <a href="ViewTicketServlet?id=${t.id}" class="view-link">View</a>
                                        <a href="DeleteTicket?id=${t.id}" class="delete"
                                           onclick="return confirm('Are you sure you want to delete this ticket?')">Delete</a>
                                    </div>
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
