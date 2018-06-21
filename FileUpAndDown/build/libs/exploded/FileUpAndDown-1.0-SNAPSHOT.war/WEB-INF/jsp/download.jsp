<%--
  Created by IntelliJ IDEA.
  User: zhaochaochao
  Date: 2018/6/11
  Time: 10:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Download</title>
</head>
<body>
<h2>文件下载</h2>
<a href="download?filename=${requestScope.user.image.originalFilename}">
    ${requestScope.user.image.originalFilename }
</a>
</body>
</html>
