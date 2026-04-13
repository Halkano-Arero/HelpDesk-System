<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Settings</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/settings.css">
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
        <li><a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a></li>
        <li><a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a></li>
        <li class="active"><a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a></li>
        <li><a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
</div>

<div class="main-content">
    <c:if test="${param.success == 'deleted' || param.success == 'added'}">
        <div class="alert-success">Settings updated successfully.</div>
    </c:if>
    <c:if test="${param.error == 'validation'}">
        <div class="alert-error">Please provide a valid setting name.</div>
    </c:if>

    <header class="topbar">
        <div class="topbar-left">
            <h2><span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Admin" %></span></h2>
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

    <br><h2>System Settings</h2>

    <div class="settings-container">
        <div class="settings-card">
            <h3>Departments</h3>
            <form action="<%= request.getContextPath() %>/SettingsServlet" method="post">
                <input type="hidden" name="type" value="department">
                <input type="text" name="name" placeholder="New Department" required>
                <button class="btn-add">Add</button>
            </form>
            <table>
                <tr><th>Name</th><th>Action</th></tr>
                <c:forEach var="d" items="${departments}">
                    <tr>
                        <td>${d.name}</td>
                        <td><a class="btn-delete" href="DeleteSettingServlet?type=department&id=${d.id}">Delete</a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="settings-card">
            <h3>Categories</h3>
            <form action="<%= request.getContextPath() %>/SettingsServlet" method="post">
                <input type="hidden" name="type" value="category">
                <input type="text" name="name" placeholder="New Category" required>
                <button class="btn-add">Add</button>
            </form>
            <table>
                <tr><th>Name</th><th>Action</th></tr>
                <c:forEach var="c" items="${categories}">
                    <tr>
                        <td>${c.name}</td>
                        <td><a class="btn-delete" href="DeleteSettingServlet?type=category&id=${c.id}">Delete</a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>

        <div class="settings-card">
            <h3>Statuses</h3>
            <form action="<%= request.getContextPath() %>/SettingsServlet" method="post">
                <input type="hidden" name="type" value="status">
                <input type="text" name="name" placeholder="New Status" required>
                <button class="btn-add">Add</button>
            </form>
            <table>
                <tr><th>Name</th><th>Action</th></tr>
                <c:forEach var="s" items="${statuses}">
                    <tr>
                        <td>${s.name}</td>
                        <td><a class="btn-delete" href="DeleteSettingServlet?type=status&id=${s.id}">Delete</a></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</div>

</body>
</html>
