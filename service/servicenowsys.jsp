<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="javax.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>User</title>
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
	<%String sys = request.getParameter("sys"); %>
	
	<form class="container w-25 text-center" style="margin-top: 100px;" action="servicenowsys" method="post">
		<p>Please select the CRUD operation to be performed for <%=sys %> : </p>
		<input type="radio" id="get" name="tasktype" value="fetch" checked>
		<label for="get">Read or Fetch Incident</label><br>
		<input type="radio" id="delete" name="tasktype" value="delete">
		<label for="delete">Delete Incident</label><br>
		<input type="radio" id="put" name="tasktype" value="update">
		<label for="put">Update Incident</label><br>
		  
		<button type="submit" name="sys" value=<%=sys %>>Submit</button><br>
	</form>
	
	<form class="container w-25 text-center" style="margin-top: 100px;" action="servicenow" method="get" id="form">
		<button type="submit" onclick="session()">Choose other incident</button>
	</form>
	<form class="container w-25 text-center" style="margin-top: 100px;" action="servicenow.jsp" method="get" id="form">
		<button type="submit" onclick="session()">Back to main menu</button>
	</form>
	<br>
	<form action="index" method="post">
		<button type="submit" name="logstat" value="logout">Log Out</button>
	</form>
	
	 
		<script type="text/javascript">
		function session(){
			<%
				session.removeAttribute("sys");
			%>
			document.getElementById("form").submit();
		}
	
		
		<!--function timer() {
			var myVar = setInterval(myTimer, 1000);
		}
		function myTimer(){
			alert(hi);
		}-->
	</script>
	
	
</body>
</html>