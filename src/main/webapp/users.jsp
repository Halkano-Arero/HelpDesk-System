<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Users</title>
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
        <li class="active"><a href="<%= request.getContextPath() %>/UsersServlet"><i class="fa fa-users"></i> Users</a></li>
        <li><a href="<%= request.getContextPath() %>/TicketServlet"><i class="fa fa-ticket"></i> Tickets</a></li>
        <li><a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a></li>
        <li><a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a></li>
        <li><a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a></li>
        <li><a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
</div>

<main class="main-content">
    <header class="topbar">
        <div class="topbar-left">
            <i class="fa fa-users"></i>
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

    <section class="panel-card compact-panel">
        <div class="panel-header">
            <div>
                <h3>System Users</h3>
                <p>Registered accounts in the HelpDesk system.</p>
            </div>
        </div>

        <c:if test="${param.error == 'self'}">
            <div class="page-alert error">You cannot delete your own account while logged in.</div>
        </c:if>
        <c:if test="${param.error == 'last_admin'}">
            <div class="page-alert error">At least one admin account must remain in the system.</div>
        </c:if>
        <c:if test="${param.error == 'validation' || param.error == 'db' || param.error == 'not_found'}">
            <div class="page-alert error">The user could not be removed. Please try again.</div>
        </c:if>
        <c:if test="${param.success == 'deleted' || param.success == 'deleted_agent' || param.success == 'deleted_user'}">
            <div class="page-alert success">User deleted successfully.</div>
        </c:if>
        <c:if test="${param.success == 'updated'}">
            <div class="page-alert success">User updated successfully.</div>
        </c:if>

        <div class="cards compact-cards user-summary-cards">
            <div class="card blue">Total Users<span>${totalUsers}</span></div>
            <div class="card green">Admins<span>${roleCounts.admin}</span></div>
            <div class="card yellow">Agents<span>${roleCounts.agent}</span></div>
            <div class="card orange">Users<span>${roleCounts.user}</span></div>
        </div>

        <div class="users-table-shell">
            <div class="users-table-shell-head">
                <div>
                    <h4>Manage Accounts</h4>
                    <p>Update role and category, or remove a user entirely.</p>
                </div>
            </div>

            <div class="table-wrap">
            <table class="compact-table admin-queue-table users-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Category</th>
                    <th>Created</th>
                    <th>Edit</th>
                    <th>Delete</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty users}">
                        <tr><td colspan="8">No users found.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.id}</td>
                                <td>${u.username}</td>
                                <td>${u.email}</td>
                                <td><span class="category-chip">${u.role}</span></td>
                                <td><span class="category-chip">${u.category}</span></td>
                                <td>${u.createdAt}</td>
                                <td>
                                    <details class="user-edit-wrap">
                                        <summary class="user-edit-toggle">Edit</summary>
                                        <form action="<%= request.getContextPath() %>/UpdateUserServlet" method="post" class="inline-category-form user-edit-form">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <select name="role" required>
                                                <option value="admin" ${u.role == 'admin' ? 'selected' : ''}>admin</option>
                                                <option value="agent" ${u.role == 'agent' ? 'selected' : ''}>agent</option>
                                                <option value="user" ${u.role == 'user' ? 'selected' : ''}>user</option>
                                            </select>
                                            <select name="category" required>
                                                <c:forEach var="category" items="${categories}">
                                                    <option value="${category}" ${category == u.category ? 'selected' : ''}>${category}</option>
                                                </c:forEach>
                                            </select>
                                            <button type="submit" class="save-btn">Save</button>
                                        </form>
                                    </details>
                                </td>
                                <td>
                                    <c:if test="${u.username ne sessionScope.username}">
                                        <form action="<%= request.getContextPath() %>/DeleteUserServlet" method="post"
                                              onsubmit="return confirm('Are you sure you want to delete this user?');">
                                            <input type="hidden" name="id" value="${u.id}">
                                            <button type="submit" class="delete-btn">Delete</button>
                                        </form>
                                    </c:if>
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
