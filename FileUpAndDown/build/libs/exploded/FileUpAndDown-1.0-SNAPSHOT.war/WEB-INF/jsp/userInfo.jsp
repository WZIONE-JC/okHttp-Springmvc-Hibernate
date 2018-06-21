<%--
  Created by IntelliJ IDEA.
  User: zhaochaochao
  Date: 2018/6/18
  Time: 8:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>userInfo</title>
</head>
<body>
<h2>注册</h2>
<form action="/simple/post" enctype="application/x-www-form-urlencoded" method="post">
    <table>
        <tr>
            <td>姓名:</td>
            <td><input type="text" name="userName" value="${user.userName }"></td>
        </tr>
        <tr>
            <td>密码:</td>
            <td><input type="password" name="password" value="${user.password }"></td>
        </tr>
        <tr>
            <td><input type="submit" value="注册"></td>
        </tr>
    </table>
</form>
</body>
</html>
