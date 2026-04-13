<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Help Desk Login</title>
  <link rel="stylesheet" href="css/login.css" />
</head>
<body>
  <div class="login-wrapper">
    <div class="login-card">
      <img src="images/logo.png" alt="Help Desk Logo" class="logo">
      <h2>HelpDesk System</h2>
      <p class="subtitle">Sign in to continue</p>

      <c:if test="${param.error == 'invalid'}">
        <p style="color:red;">Invalid username, email, or password.</p>
      </c:if>
      <c:if test="${param.error == 'unauthorized'}">
        <p style="color:red;">Please sign in with the correct account.</p>
      </c:if>
      <c:if test="${param.success == 'registered'}">
        <p style="color:green;">Registration complete. You can sign in now.</p>
      </c:if>

      <form action="LoginServlet" method="post">
        <div class="input-group">
          <label for="username">Username</label>
          <input type="text" id="username" name="username" placeholder="Enter username" required />
        </div>

        <div class="input-group">
          <label for="email">Email</label>
          <input type="email" id="email" name="email" placeholder="Enter your email" required />
        </div>

        <div class="input-group">
          <label for="password">Password</label>
          <input type="password" id="password" name="password" placeholder="Enter password" required />
        </div>

        <button type="submit">Login</button>
      </form>

      <p>Don't have an account? <a href="register.jsp">Register here</a></p>

      <div class="footer">
        <p>&copy; 2026 Help Desk System</p>
      </div>
    </div>
  </div>
</body>
</html>
