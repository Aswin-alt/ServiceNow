<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@page import="javax.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
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
	<form class="container w-25 text-center" style="margin-top: 100px;" action="servicenowcreate" method="post">
		
		Enter Data to be inserted : <br><br>
		
		<div class="form-outline mb-4" style="text-align: left;">
			<label class="form-label text-left" for="caller_id">Caller ID</label> 
			<input type="text" id="caller_id" class="form-control" size="50" name="caller_id"/>
		</div>  
		
		<div class="form-outline mb-4" style="text-align: left;">
			<label class="form-label text-left" for="short_description">Short Description</label> 
			<input type="text" id="short_description" class="form-control" size="50" name="short_description"/>
		</div>  
		  
		<button type="submit">Submit</button><br>
	</form>
	<br>
	<form action="servicenow.jsp" method="post">
		<button type="submit">Back to main menu</button>
	</form>
</body>
</html>