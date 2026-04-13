<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/tickets.css">
    <link rel="stylesheet" href="css/agents.css">
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
            <a href="<%= request.getContextPath() %>/DashboardServlet"><i class="fa fa-home"></i> Dashboard</a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/TicketServlet"><i class="fa fa-ticket"></i> Tickets</a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a>
        </li>
    </ul>
</div>

<main class="main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-bell"></i>
            <span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Admin" %></span>
        </div>
        <div class="topbar-right">
            <form action="<%= request.getContextPath() %>/LogoutServlet">
                <button type="submit" class="logout-btn" onclick="return confirm('Are you sure you want to logout?');">
                    <i class="fa fa-sign-out-alt"></i> Logout
                </button>
            </form>
        </div>
    </header>

    <c:if test="${param.success == 'assigned'}">
        <div class="page-alert success">Ticket assigned successfully.</div>
    </c:if>
    <c:if test="${param.success == 'created'}">
        <div class="page-alert success">Agent created successfully.</div>
    </c:if>
    <c:if test="${param.error == 'category_mismatch'}">
        <div class="page-alert error">That agent cannot take this ticket because the categories do not match.</div>
    </c:if>
    <c:if test="${param.error == 'assign_failed' || param.error == 'validation'}">
        <div class="page-alert error">The request could not be completed. Please review the form and try again.</div>
    </c:if>

    <section class="cards compact-cards">
        <div class="card green">Total Tickets<span>${totalTickets}</span></div>
        <div class="card red">New<span>${newTickets}</span></div>
        <div class="card yellow">Open<span>${openTickets}</span></div>
        <div class="card green">Solved<span>${solvedTickets}</span></div>
        <div class="card orange">Agents<span>${totalAgents}</span></div>
        <div class="card blue">Users<span>${totalUsers}</span></div>
        <a class="card shortcut-card" href="<%= request.getContextPath() %>/AgentServlet">
            Manage Agents
            <span><i class="fa fa-users"></i></span>
        </a>
        <a class="card shortcut-card" href="<%= request.getContextPath() %>/UsersServlet">
            System Users
            <span><i class="fa fa-user-group"></i></span>
        </a>
    </section>

    <section class="panel-card compact-panel">
        <div class="panel-header">
            <div>
                <h3>Agent Management</h3>
                <p>Manage categories and coverage from the Agents page.</p>
            </div>
            <a class="panel-link" href="<%= request.getContextPath() %>/AgentServlet">Open Agents</a>
        </div>
    </section>

    <section class="panel-card compact-panel">
        <div class="panel-header">
            <div>
                <h3>User Management</h3>
                <p>Review registered users from the Users page.</p>
            </div>
            <a class="panel-link" href="<%= request.getContextPath() %>/UsersServlet">Open Users</a>
        </div>
    </section>

    <section class="dashboard-split">
        <div class="panel-card compact-panel recent-panel">
            <div class="panel-header">
                <div>
                    <h3>Recent Tickets</h3>
                    <p>Latest activity at a glance.</p>
                </div>
                <a class="panel-link" href="<%= request.getContextPath() %>/TicketServlet">View all</a>
            </div>

            <div class="table-wrap">
                <table class="compact-table">
                    <thead>
                    <tr>
                        <th>#</th>
                        <th>ID</th>
                        <th>Subject</th>
                        <th>Assigned</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty recentTickets}">
                            <tr><td colspan="5">No recent tickets found.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="t" items="${recentTickets}" varStatus="i">
                                <tr>
                                    <td>${i.index + 1}</td>
                                    <td>${t.helpdeskId}</td>
                                    <td class="ticket-subject-cell">${t.subject}</td>
                                    <td><c:out value="${empty t.assignedTo ? 'Unassigned' : t.assignedTo}"/></td>
                                    <td><span class="status-badge ${t.status}">${t.status}</span></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <section class="charts-container compact-chart-wrap dashboard-chart-row">
        <div class="chart-box compact-chart">
            <div class="panel-header compact-chart-header">
                <div>
                    <h3>Ticket Status</h3>
                    <p>Quick status snapshot.</p>
                </div>
            </div>
            <div class="chart-with-legend">
                <div class="chart-canvas-wrap">
                    <canvas id="statusChart"></canvas>
                </div>
                <div class="status-mini-legend">
                    <c:forEach var="entry" items="${statusChart}" varStatus="s">
                        <span class="status-mini-item">
                            <i class="status-dot status-dot-${s.index % 6}"></i>
                            <strong><c:out value="${entry.key}"/></strong>
                            <small>${entry.value}</small>
                        </span>
                    </c:forEach>
                </div>
            </div>
        </div>
    </section>

    <section class="panel-card compact-panel assignment-panel">
        <div class="panel-header">
            <div>
                <h3>Assignment Queue</h3>
                <p>Unassigned tickets with matching agents.</p>
            </div>
        </div>

        <div class="table-wrap">
            <table class="ticket-table compact-assignment-table">
                <thead>
                <tr>
                    <th>Ticket</th>
                    <th>Category</th>
                    <th>Assign</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty assignmentQueue}">
                        <tr><td colspan="3">No tickets available for assignment.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="ticket" items="${assignmentQueue}">
                            <tr>
                                <td>
                                    <div class="compact-ticket-meta">
                                        <strong>${ticket.helpdeskId}</strong>
                                        <small><c:out value="${empty ticket.assignedTo ? 'Unassigned' : ticket.assignedTo}"/></small>
                                    </div>
                                </td>
                                <td><span class="category-chip">${ticket.category}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty ticket.matchingAgents}">
                                            <span class="no-agent-hint">No match</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="<%= request.getContextPath() %>/AssignTicketServlet" method="post" class="assignment-form compact-assignment-form">
                                                <input type="hidden" name="ticketId" value="${ticket.id}">
                                                <input type="hidden" name="returnTo" value="DashboardServlet">
                                                <div class="agent-picker" data-agent-picker>
                                                    <input type="search" class="agent-search" placeholder="Search agents" aria-label="Search agents for ${ticket.category}">
                                                    <select name="assignedTo" required class="agent-select">
                                                        <option value="">Select agent</option>
                                                        <c:forEach var="agent" items="${ticket.availableAgents}">
                                                            <c:if test="${agent.category eq ticket.category}">
                                                                <option value="${agent.username}" data-agent-name="${agent.username}" data-agent-category="${agent.category}">
                                                                    ${agent.username} - ${agent.category}
                                                                </option>
                                                            </c:if>
                                                        </c:forEach>
                                                    </select>
                                                    <small class="agent-picker-note">Matching agents only.</small>
                                                </div>
                                                <button type="submit" class="assign-btn compact-assign-btn">Go</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
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
<script src="js/assignment-picker.js" defer></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
const statusCtx = document.getElementById('statusChart').getContext('2d');
const statusLabels = [<c:forEach var="entry" items="${statusChart}" varStatus="s">'<c:out value="${entry.key}"/>'<c:if test="${!s.last}">,</c:if></c:forEach>];
const statusValues = [<c:forEach var="entry" items="${statusChart}" varStatus="s">${entry.value}<c:if test="${!s.last}">,</c:if></c:forEach>];
const statusColors = ['#e74c3c', '#f1c40f', '#3498db', '#2ecc71', '#95a5a6', '#9b59b6', '#e67e22'];
new Chart(statusCtx, {
    type: 'doughnut',
    data: {
        labels: statusLabels,
        datasets: [{
            data: statusValues,
            backgroundColor: statusLabels.map((_, index) => statusColors[index % statusColors.length]),
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '70%',
        plugins: {
            legend: {
                display: false
            }
        }
    }
});
</script>
</body>
</html>
