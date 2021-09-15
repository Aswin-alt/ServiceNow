<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Choose the task</title>
</head>
<body>
	<%
	try{
		String log = session.getAttribute("logstat").toString();
		//session.setMaxInactiveInterval(60);
	}
	catch(Exception e){
		response.sendRedirect("timeout.jsp");
	}
	%>
	<form class="container w-25 text-center" style="margin-top: 100px;" action="servicenow" method="post">
		<p>Please select the task type : </p>
		<input type="radio" id="get" name="tasktype" value="fetch" checked>
		<label for="get">Read Incident table</label><br>
		<input type="radio" id="post" name="tasktype" value="create">
		<label for="post">Create new incident</label><br>
		<button type="submit" name="logstat" value="login">Submit</button>
	</form>
	<br>
	<form action="index" method="post">
		<button type="submit" name="logstat" value="logout">Log Out</button>
	</form>
	<br><br>
	<form action="sessionview" method="get">
		<button type="submit">Check Session Details</button>
	</form>
</body>
</html>