<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register User</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="login-wrapper">
    <div class="login-card">
        <h2>Create Account</h2>

        <c:if test="${param.error == 'exists'}">
            <p style="color:red;">An account with that email already exists.</p>
        </c:if>
        <c:if test="${param.error == 'validation'}">
            <p style="color:red;">Please fill in all fields with valid values.</p>
        </c:if>

        <form action="RegisterServlet" method="post">
            <label>Username:</label>
            <input type="text" name="username" required>

            <label>Email:</label>
            <input type="email" name="email" required>

            <label>Password:</label>
            <input type="password" name="password" minlength="8" required>

            <label>Account Type:</label>
            <select name="role" required>
                <option value="user" selected>User</option>
                <option value="agent">Agent</option>
                <option value="admin">Admin</option>
            </select>

            <p style="font-size: 0.9rem; color: #666;">New registrations create standard user accounts.</p>
            <button type="submit">Register</button>
        </form>

        <p>Already have an account? <a href="login.jsp">Login</a></p>
    </div>
</div>
</body>
</html>
