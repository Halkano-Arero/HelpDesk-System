<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Agents</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/agents.css">
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
            <a href="<%= request.getContextPath() %>/DashboardServlet">
                <i class="fa fa-home"></i> Dashboard
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/TicketServlet">
                <i class="fa fa-ticket"></i> Tickets
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/ReportsServlet">
                <i class="fa fa-chart-line"></i> Reports
            </a>
        </li>
        <li class="active">
            <a href="<%= request.getContextPath() %>/AgentServlet">
                <i class="fa fa-users"></i> Agents
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/SettingsServlet">
                <i class="fa fa-cog"></i> Settings
            </a>
        </li>
        <li>
            <a href="<%= request.getContextPath() %>/AdminProfileServlet">
                <i class="fa fa-user"></i> My Profile
            </a>
        </li>
    </ul>
</div>

<main class="main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-users"></i>
            <span>Manage Agents</span>
        </div>
    </header>

    <c:if test="${param.success == 'created'}">
        <div class="page-alert success">Agent created successfully.</div>
    </c:if>
    <c:if test="${param.success == 'updated'}">
        <div class="page-alert success">Agent category updated successfully.</div>
    </c:if>
    <c:if test="${param.success == 'deleted'}">
        <div class="page-alert success">Agent removed and their tickets were returned to the unassigned queue.</div>
    </c:if>
    <c:if test="${param.error == 'exists'}">
        <div class="page-alert error">That username or email is already in use.</div>
    </c:if>
    <c:if test="${param.error == 'validation'}">
        <div class="page-alert error">Please fill out the agent details correctly.</div>
    </c:if>
    <c:if test="${param.error == 'error'}">
        <div class="page-alert error">The request could not be completed. Please try again.</div>
    </c:if>

    <section class="admin-grid">
        <div class="panel-card">
            <div class="panel-header">
                <div>
                    <h3>Add New Agent</h3>
                    <p>Each agent is tagged with one support category for smarter assignment.</p>
                </div>
            </div>

            <form action="<%= request.getContextPath() %>/AddAgentServlet" method="post" class="agent-quick-form">
                <label>Username</label>
                <input type="text" name="username" value="${param.username}" autocomplete="username" required>

                <label>Email</label>
                <input type="email" name="email" value="${param.email}" autocomplete="email" required>

                <label>Password</label>
                <input type="password" name="password" autocomplete="new-password" required>

                <label>Support Category</label>
                <select name="category" required>
                    <option value="">Select Category</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category}" ${category == param.category ? 'selected' : ''}>${category}</option>
                    </c:forEach>
                </select>

                <button type="submit" class="add-agent-btn">Create Agent</button>
            </form>
        </div>

        <div class="panel-card">
            <div class="panel-header">
                <div>
                    <h3>Agent Directory</h3>
                    <p>Review current support coverage by category.</p>
                </div>
            </div>

            <div class="table-wrap">
                <table class="ticket-table">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Category</th>
                        <th>Update Category</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty agents}">
                            <tr><td colspan="6">No agents have been created yet.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="a" items="${agents}">
                                <tr>
                                    <td>${a.id}</td>
                                    <td>${a.username}</td>
                                    <td>${a.email}</td>
                                    <td><span class="category-chip">${a.category}</span></td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/UpdateAgentCategoryServlet" method="post" class="inline-category-form">
                                            <input type="hidden" name="id" value="${a.id}">
                                            <select name="category" required>
                                                <c:forEach var="category" items="${categories}">
                                                    <option value="${category}" ${category == a.category ? 'selected' : ''}>${category}</option>
                                                </c:forEach>
                                            </select>
                                            <button type="submit" class="save-btn">Save</button>
                                        </form>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/DeleteAgentServlet"
                                              method="post"
                                              onsubmit="return confirm('Are you sure you want to delete this agent?');">
                                            <input type="hidden" name="id" value="${a.id}">
                                            <button type="submit" class="delete-btn">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </section>
</main>
</body>
</html>
