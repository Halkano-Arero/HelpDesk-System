<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assigned Ticket</title>
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
        <li><a href="${pageContext.request.contextPath}/AgentDashboardServlet"><i class="fa fa-home"></i> Dashboard</a></li>
        <li class="active"><a href="${pageContext.request.contextPath}/AgentTicketServlet"><i class="fa fa-ticket"></i> My Tickets</a></li>
        <li><a href="${pageContext.request.contextPath}/AgentProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
</div>

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

    <section class="ticket-detail-hero">
        <div>
            <h2>Assigned Ticket</h2>
            <p>Review the issue, then respond and update the ticket status.</p>
        </div>
        <div class="ticket-hero-meta">
            <span class="category-chip">${priority}</span>
            <span class="status-badge ${status}">${status}</span>
        </div>
    </section>

    <section class="agent-workbench">
        <div class="panel-card ticket-detail-card">
            <div class="ticket-detail-top">
                <div>
                    <div class="ticket-detail-label">Ticket ID</div>
                    <h3>${helpdeskId}</h3>
                </div>
                <div class="ticket-detail-subject">
                    <div class="ticket-detail-label">Subject</div>
                    <p>${subject}</p>
                </div>
            </div>

            <div class="ticket-detail-grid">
                <div class="ticket-info-tile">
                    <span>Priority</span>
                    <strong>${priority}</strong>
                    <small>Current status: ${status}</small>
                </div>
                <div class="ticket-info-tile">
                    <span>Current Status</span>
                    <strong>${status}</strong>
                    <small>Assigned ticket</small>
                </div>
                <div class="ticket-info-tile">
                    <span>Response Saved</span>
                    <strong><c:out value="${empty agentResponse ? 'No' : 'Yes'}"/></strong>
                    <small>Previous agent reply</small>
                </div>
            </div>

            <div class="ticket-detail-description">
                <div class="ticket-detail-label">Description</div>
                <div class="desc-box">${description}</div>
            </div>
        </div>

        <div class="panel-card response-panel">
            <div class="panel-header">
                <div>
                    <h3>Reply & Update</h3>
                    <p>Write a clear response and set the right status.</p>
                </div>
            </div>

            <form action="AgentUpdateTicketServlet" method="post" class="response-form">
                <input type="hidden" name="id" value="${ticketId}">

                <label>Status</label>
                <select name="status">
                    <option value="Open" ${status == 'Open' ? 'selected' : ''}>Open</option>
                    <option value="Answered" ${status == 'Answered' ? 'selected' : ''}>Answered</option>
                    <option value="Solved" ${status == 'Solved' ? 'selected' : ''}>Solved</option>
                    <option value="Closed" ${status == 'Closed' ? 'selected' : ''}>Closed</option>
                </select>

                <label>Agent Response</label>
                <textarea name="response" rows="10" placeholder="Add a helpful response for the requester" required>${agentResponse}</textarea>

                <button type="submit" class="assign-btn response-submit">Update Ticket</button>
            </form>
        </div>
    </section>
</main>
</body>
</html>
