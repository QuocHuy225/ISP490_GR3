<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <title>Test JSP Page</title>
</head>
<body>
    <h1>✅ JSP Test Page Loaded Successfully!</h1>

    <h3>Context Path:</h3>
    <p>${pageContext.request.contextPath}</p>

    <h3>Session Test:</h3>
    <%
        HttpSession session = request.getSession();
        session.setAttribute("testValue", "Hello from session!");
    %>
    <p>Session test value set: <strong><%= session.getAttribute("testValue") %></strong></p>

    <h3>Servlet Routing Test:</h3>
    <ul>
        <li><a href="<%= request.getContextPath() %>/test">Go to /test Servlet</a></li>
        <li><a href="<%= request.getContextPath() %>/slot">Go to /slot Servlet</a></li>
    </ul>
</body>
</html>
