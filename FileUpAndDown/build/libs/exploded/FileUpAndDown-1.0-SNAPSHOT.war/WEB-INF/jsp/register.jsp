<%--
  Created by IntelliJ IDEA.
  User: zhaochaochao
  Date: 2018/6/11
  Time: 10:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>注册</h2>
<form action="register" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td>姓名:</td>
            <td><input type="text" name="userName"></td>
        </tr>
        <tr>
            <td>头像:</td>
            <td><input type="file" name="image"></td>
        </tr>
        <tr>
            <td><input type="submit" value="注册"></td>
        </tr>
    </table>
</form>
</body>
</html>
