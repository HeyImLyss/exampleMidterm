<%--
  Created by IntelliJ IDEA.
  User: Lyss
  Date: 2026-10-19
  Time: 5:44 p.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Registration (With Salting)</title>
</head>
<body>
<h2>User Registration (With Salting)</h2>
<form action="register-salted" method="post">
    <label>Username:</label><br>
    <input type="text" name="username" required><br><br>

    <label>Password:</label><br>
    <input type="password" name="password" required><br><br>

    <input type="submit" value="Register User">
</form>
</body>
</html>