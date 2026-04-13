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
      <li><a href="<%= request.getContextPath() %>/UserDashboardServlet"><i class="fa fa-home"></i> Dashboard</a></li>
      <li class="active"><a href="<%= request.getContextPath() %>/LoadUserCreateTicketServlet"><i class="fa fa-plus"></i> Create Ticket</a></li>
      <li><a href="<%= request.getContextPath() %>/UserTicketsServlet"><i class="fa fa-ticket"></i> My Tickets</a></li>
      <li><a href="<%= request.getContextPath() %>/UserProfileServlet"><i class="fa fa-user"></i> My Profile</a></li>
    </ul>
  </div>

  <main class="main-content">
    <header class="topbar">
      <div class="right">
        <h2><span>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "User" %></span></h2>
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

    <div class="ticket-page">
      <h2>Create New Ticket</h2>

      <c:if test="${param.error == 'validation'}">
        <p style="color:red;">Please review the form and provide valid values.</p>
      </c:if>

      <form action="<%= request.getContextPath() %>/UserInsertTicketServlet" method="post" class="ticket-form">
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

        <label>Priority</label>
        <select name="priority">
          <option value="Low">Low</option>
          <option value="Medium" selected>Medium</option>
          <option value="High">High</option>
        </select>

        <div class="form-note">
          Your ticket will be reviewed by an admin and assigned to the right agent based on category.
        </div>

        <button type="submit" class="btn-submit">Create Ticket</button>
      </form>
    </div>
  </main>

  <script src="<%= request.getContextPath() %>/js/dashboard.js" defer></script>
</body>
</html>
