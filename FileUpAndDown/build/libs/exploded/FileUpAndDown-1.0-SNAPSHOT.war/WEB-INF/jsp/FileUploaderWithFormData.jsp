<%--
  Created by IntelliJ IDEA.
  User: zhaochaochao
  Date: 2018/6/10
  Time: 14:07
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>FileUploaderWithFormData</title>
</head>
<body>
<h2>文件上传</h2>
<form action="upload" enctype="multipart/form-data" method="post">
    <table>
        <tr>
            <td>文件描述:</td>
            <td><input type="text" name="description"></td>
        </tr>
        <tr>
            <td>请选择文件:</td>
            <td><input type="file" name="file"></td>
        </tr>
        <tr>
            <td><input type="submit" value="上传"></td>
        </tr>
    </table>
</form>
</body>
</html>
