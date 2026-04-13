<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Create Ticket</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
  <link rel="stylesheet" href="<%= request.getContextPath() %>/css/create_ticket.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
  <div class="sidebar">
    <div class="logo">
      <img src="<%= request.getContextPath() %>/images/logo.png" alt="Helpdesk Logo">
      <h2>Helpdesk System</h2>
    </div>
    <ul class="nav-links">
      <li><a href="<%= request.getContextPath() %>/DashboardServlet"><i class="fa fa-home"></i> Dashboard</a></li>
      <li><a href="<%= request.getContextPath() %>/TicketServlet"><i class="fa fa-ticket"></i> Tickets</a></li>
      <li><a href="<%= request.getContextPath() %>/ReportsServlet"><i class="fa fa-chart-line"></i> Reports</a></li>
      <li><a href="<%= request.getContextPath() %>/AgentServlet"><i class="fa fa-users"></i> Agents</a></li>
      <li><a href="<%= request.getContextPath() %>/SettingsServlet"><i class="fa fa-cog"></i> Settings</a></li>
      <li><a href="<%= request.getContextPath() %>/AdminProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
  </div>

  <main class="main-content">
    <header class="topbar">
      <div class="right">
        <h2><span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "Admin" %></span></h2>
      </div>
    </header>

    <div class="ticket-page">
      <h2>Create New Ticket</h2>

      <c:if test="${param.error == 'validation'}">
        <p style="color:red;">Please review the form and provide valid values.</p>
      </c:if>

      <form action="<%= request.getContextPath() %>/InsertTicketServlet" method="post" class="ticket-form">
        <label>Help Desk ID (auto-generated)</label>
        <input type="text" name="helpdesk_view" value="Will be generated" readonly>

        <label>Name</label>
        <input type="text" name="name" placeholder="Enter ticket name" required>

        <label>Email ID</label>
        <input type="email" name="email" value="<%= session.getAttribute("email") != null ? session.getAttribute("email") : "" %>" required>

        <label>Department</label>
        <select name="department" required>
          <option value="">Select Department</option>
          <c:forEach var="department" items="${departments}">
            <option value="${department}">${department}</option>
          </c:forEach>
        </select>

        <label>Issue Category</label>
        <select name="category" required>
          <option value="">Select Category</option>
          <c:forEach var="category" items="${categories}">
            <option value="${category}">${category}</option>
          </c:forEach>
        </select>

        <label>Subject</label>
        <input type="text" name="subject" placeholder="Enter ticket title" required>

        <label>Description</label>
        <textarea name="description" rows="5" placeholder="Enter ticket description" required></textarea>

        <label>Status</label>
        <select name="status">
          <c:forEach var="status" items="${statuses}">
            <option value="${status}" ${status == 'New' ? 'selected' : ''}>${status}</option>
          </c:forEach>
        </select>

        <label>Priority</label>
        <select name="priority">
          <option value="Low">Low</option>
          <option value="Medium" selected>Medium</option>
          <option value="High">High</option>
        </select>

        <div class="form-note">
          After saving, assign this ticket from the admin dashboard or tickets queue.
        </div>

        <button type="submit" class="btn-submit">Create Ticket</button>
      </form>
    </div>
  </main>

  <script src="<%= request.getContextPath() %>/js/dashboard.js" defer></script>
</body>
</html>
