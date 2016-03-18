<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>cloudant</title>
</head>
<body>
	<h3>Cloudant NoSQL</h3>

    <form action="cloudantupload" method="POST" enctype="multipart/form-data">
            <input type="file" name="file" /><br>
       
            <input type="submit" class="btn" value="Upload" />
    </form>
    
    <h3>Result: </h3>
     <% if (request.getAttribute("msg") != null) { %>
       	<div><%= request.getAttribute("msg") %></div>
    <% } %> 
</body>
</html>