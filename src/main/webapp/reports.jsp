<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/tickets.css">
    <link rel="stylesheet" href="css/reports.css">
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
        <li><a href="<%= request.getContextPath() %>/TicketServlet"><i class="fa fa-ticket"></i> Tickets</a></li>
        <li class="active"><a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a></li>
        <li><a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a></li>
        <li><a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a></li>
        <li><a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
</div>

<main class="main-content reports-main">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-chart-line"></i>
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

    <section class="panel-card compact-panel report-hero">
        <div>
            <h2>Reports</h2>
            <p>Generate a quick operational snapshot for the helpdesk team.</p>
        </div>
        <div class="report-range-note">Range: <strong>${reportRangeLabel}</strong></div>
    </section>

    <section class="panel-card compact-panel report-toolbar-card">
        <form class="report-toolbar" action="<%= request.getContextPath() %>/ReportsServlet" method="get">
            <label>
                <span>From</span>
                <input type="date" name="startDate" value="${filterStart}">
            </label>
            <label>
                <span>To</span>
                <input type="date" name="endDate" value="${filterEnd}">
            </label>
            <div class="report-actions">
                <button type="submit" class="report-btn primary" name="view" value="1">Generate</button>
                <button type="submit" class="report-btn secondary" name="export" value="csv">Export CSV</button>
            </div>
        </form>
    </section>

    <section class="report-summary-grid">
        <div class="report-stat-card blue">
            <span>${totalTickets}</span>
            <small>Total Tickets</small>
        </div>
        <div class="report-stat-card green">
            <span>${assignedTickets}</span>
            <small>Assigned</small>
        </div>
        <div class="report-stat-card orange">
            <span>${unassignedTickets}</span>
            <small>Unassigned</small>
        </div>
        <div class="report-stat-card purple">
            <span>${ratedTickets}</span>
            <small>Rated</small>
        </div>
        <div class="report-stat-card blue">
            <span>${totalUsers}</span>
            <small>Total Users</small>
        </div>
    </section>

    <section class="report-grid">
        <div class="panel-card compact-panel report-chart-card">
            <div class="panel-header">
                <div>
                    <h3>Status Snapshot</h3>
                    <p>Compact view of ticket states.</p>
                </div>
            </div>
            <div class="chart-with-legend">
                <div class="chart-canvas-wrap report-chart-wrap">
                    <canvas id="reportStatusChart"></canvas>
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

        <div class="panel-card compact-panel report-breakdown-card">
            <div class="panel-header">
                <div>
                    <h3>Category Breakdown</h3>
                    <p>Tickets grouped by issue category.</p>
                </div>
            </div>
            <div class="table-wrap">
                <table class="compact-table report-table">
                    <thead>
                    <tr>
                        <th>Category</th>
                        <th>Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty categoryCounts}">
                            <tr><td colspan="2">No category data available.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="entry" items="${categoryCounts}">
                                <tr>
                                    <td><span class="category-chip"><c:out value="${entry.key}"/></span></td>
                                    <td><strong>${entry.value}</strong></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <section class="panel-card compact-panel report-timeline-card">
        <div class="panel-header">
            <div>
                <h3>Created At Timeline</h3>
                <p>Tickets grouped by the day they were created.</p>
            </div>
        </div>
        <div class="chart-canvas-wrap report-timeline-wrap">
            <canvas id="createdTimelineChart"></canvas>
        </div>
    </section>

    <section class="report-grid report-two-cols">
        <div class="panel-card compact-panel report-breakdown-card">
            <div class="panel-header">
                <div>
                    <h3>Top Agents</h3>
                    <p>Most active assignees in this range.</p>
                </div>
            </div>
            <div class="table-wrap">
                <table class="compact-table report-table">
                    <thead>
                    <tr>
                        <th>Agent</th>
                        <th>Tickets</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty topAgents}">
                            <tr><td colspan="2">No assigned tickets in this range.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="agent" items="${topAgents}">
                                <tr>
                                    <td>${agent.username}</td>
                                    <td><strong>${agent.total}</strong></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="panel-card compact-panel report-breakdown-card">
            <div class="panel-header">
                <div>
                    <h3>Ticket Status</h3>
                    <p>Read the full status mix at a glance.</p>
                </div>
            </div>
            <div class="table-wrap">
                <table class="compact-table report-table">
                    <thead>
                    <tr>
                        <th>Status</th>
                        <th>Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="entry" items="${statusChart}">
                        <tr>
                            <td><span class="category-chip"><c:out value="${entry.key}"/></span></td>
                            <td><strong>${entry.value}</strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <section class="report-grid report-two-cols">
        <div class="panel-card compact-panel report-breakdown-card">
            <div class="panel-header">
                <div>
                    <h3>User Roles</h3>
                    <p>Accounts currently available in the system.</p>
                </div>
            </div>
            <div class="table-wrap">
                <table class="compact-table report-table">
                    <thead>
                    <tr>
                        <th>Role</th>
                        <th>Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="entry" items="${roleCounts}">
                        <tr>
                            <td><span class="status-badge ${entry.key}"><c:out value="${entry.key}"/></span></td>
                            <td><strong>${entry.value}</strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="panel-card compact-panel report-breakdown-card">
            <div class="panel-header">
                <div>
                    <h3>Recent Users</h3>
                    <p>Latest registered accounts for admin review.</p>
                </div>
            </div>
            <div class="table-wrap">
                <table class="compact-table report-table">
                    <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Category</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty recentUsers}">
                            <tr><td colspan="4">No user records available.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="u" items="${recentUsers}">
                                <tr>
                                    <td>${u.username}</td>
                                    <td>${u.email}</td>
                                    <td><span class="category-chip">${u.role}</span></td>
                                    <td><span class="category-chip">${u.category}</span></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <section class="panel-card compact-panel report-recent-card">
        <div class="panel-header">
            <div>
                <h3>Recent Tickets</h3>
                <p>Latest items in the selected date range.</p>
            </div>
        </div>
        <div class="table-wrap">
            <table class="compact-table admin-queue-table">
                <thead>
                <tr>
                    <th>Helpdesk ID</th>
                    <th>Subject</th>
                    <th>Category</th>
                    <th>Assigned</th>
                    <th>Status</th>
                    <th>Created Date</th>
                    <th>Time</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty recentTickets}">
                        <tr><td colspan="7">No tickets found for this report.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="ticket" items="${recentTickets}">
                            <tr>
                                <td>${ticket.helpdeskId}</td>
                                <td class="ticket-subject-cell">${ticket.subject}</td>
                                <td><span class="category-chip">${ticket.category}</span></td>
                                <td><c:out value="${empty ticket.assignedTo ? 'Unassigned' : ticket.assignedTo}"/></td>
                                <td><span class="status-badge ${ticket.status}">${ticket.status}</span></td>
                                <td>${ticket.createdDate}</td>
                                <td>${ticket.createdTime}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
const reportCtx = document.getElementById('reportStatusChart').getContext('2d');
const reportLabels = [<c:forEach var="entry" items="${statusChart}" varStatus="s">'<c:out value="${entry.key}"/>'<c:if test="${!s.last}">,</c:if></c:forEach>];
const reportValues = [<c:forEach var="entry" items="${statusChart}" varStatus="s">${entry.value}<c:if test="${!s.last}">,</c:if></c:forEach>];
const reportColors = ['#e74c3c', '#f1c40f', '#3498db', '#2ecc71', '#95a5a6', '#9b59b6', '#e67e22'];
new Chart(reportCtx, {
    type: 'doughnut',
    data: {
        labels: reportLabels,
        datasets: [{
            data: reportValues,
            backgroundColor: reportLabels.map((_, index) => reportColors[index % reportColors.length]),
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '72%',
        plugins: {
            legend: {
                display: false
            }
        }
    }
});

const timelineCtx = document.getElementById('createdTimelineChart').getContext('2d');
const timelineLabels = [<c:forEach var="entry" items="${createdTimeline}" varStatus="s">'<c:out value="${entry.key}"/>'<c:if test="${!s.last}">,</c:if></c:forEach>];
const timelineValues = [<c:forEach var="entry" items="${createdTimeline}" varStatus="s">${entry.value}<c:if test="${!s.last}">,</c:if></c:forEach>];
new Chart(timelineCtx, {
    type: 'line',
    data: {
        labels: timelineLabels,
        datasets: [{
            label: 'Created Tickets',
            data: timelineValues,
            borderColor: '#17324d',
            backgroundColor: 'rgba(23, 50, 77, 0.12)',
            borderWidth: 3,
            fill: true,
            tension: 0.35,
            pointRadius: 3,
            pointHoverRadius: 5
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: false
            }
        },
        scales: {
            x: {
                grid: { display: false }
            },
            y: {
                beginAtZero: true,
                ticks: { precision: 0 }
            }
        }
    }
});
</script>
</body>
</html>
