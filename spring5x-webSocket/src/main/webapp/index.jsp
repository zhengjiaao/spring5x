<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页面</title>
</head>
<body>
<h2>Hello World!</h2>

<form action="${pageContext.request.contextPath}/rest/login/chat" method="get">
    <input name="username" type="text">
    <button id="btn">输入用户名后登录</button>
</form>

</body>
</html>
