<%-- 
    Document   : user_profile
    Created on : Jan 1, 2026, 5:05:08 PM
    Author     : HP
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page session="true" %>
<%
    String username = (String) session.getAttribute("username");
    String email = (String) session.getAttribute("email");
    String role = (String) session.getAttribute("role");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Profile | User</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <link rel="stylesheet" href="css/profile.css">
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
            <a href="<%= request.getContextPath() %>/UserDashboardServlet">
                <i class="fa fa-home"></i> Dashboard
            </a>
        </li>

        <li>
            <a href="<%= request.getContextPath() %>/LoadUserCreateTicketServlet">
                <i class="fa fa-plus"></i> Create Ticket
            </a>
        </li>

        <li>
            <a href="<%= request.getContextPath() %>/UserTicketsServlet">
                <i class="fa fa-ticket"></i> My Tickets
            </a>
        </li>
        
        <li class="active">
            <a href="<%= request.getContextPath() %>/UserProfileServlet">
                <i class="fa fa-user"></i> My Profile
            </a>
        </li>


    </ul>
</div>



<!-- MAIN CONTENT -->
<div class="main-content"> 

    <!-- PAGE BODY -->
    <div class="page-body">

        <div class="profile-card">
            <%@ page contentType="text/html;charset=UTF-8" %>

            <h2>User Profile</h2>

            <form action="UpdateUserProfileServlet" method="post">
                <label>Username (Cannot be changed)</label>
                <input type="text" value="<%= username %>" disabled>

                <label>Role (Cannot be changed)</label>
                <input type="text" value="<%= role %>" disabled>

                <label>Email Address</label>
                <input type="email" name="email" value="<%= email %>" required>

                <hr>

                <h4>Change Password</h4>

                <label>Old Password</label>
                <input type="password" name="oldPassword" placeholder="Enter old password">

                <label>New Password</label>
                <input type="password" name="newPassword" placeholder="Leave blank to keep current">

                <button type="submit" class="btn-update">
                    Update Profile
                </button>
            </form><br>

            <c:if test="${param.success != null}">
                <p style="color:green;">Password updated successfully</p>
            </c:if>

            <c:if test="${param.error != null}">
                <p style="color:red;">Update failed</p>
            </c:if>

                    </div>

    </div>
</div>

</body>
</html>

