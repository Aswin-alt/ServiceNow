<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="javax.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>ServiceNow</title>
</head>
<body>

	<%
	try{
		String log = session.getAttribute("logstat").toString();
		if(log.equals("login")){
			response.sendRedirect("servicenow.jsp");
		}
	}
	catch(Exception e){
	}
	%>
	
	<h1>Hello</h1>
	<form class="container w-25 text-center" style="margin-top: 100px;" action="index" method="post" id="form">
		
		<div class="form-outline mb-4" style="text-align: left;">
			<label class="form-label text-left" for="url">URL</label> 
			<input type="text" id="url" class="form-control" size="50" name="url" required />
		</div>  <br>
		Enter the Login Credentials : <br><br>
		
		<div class="form-outline mb-4" style="text-align: left;">
			<label class="form-label text-left" for="username">User Name</label> 
			<input type="text" id="username" class="form-control" size="50" name="username" required/>
		</div>  
		
		<div class="form-outline mb-4" style="text-align: left;">
			<label class="form-label text-left" for="password">Password</label> 
			<input type="password" id="password" class="form-control" size="50" name="password" required/>
		</div>  
		  
		<button type="submit" name="logstat" value="login">Login</button><br>
	</form>
		   		
 	<!-- <script type=text/javascript>
		  
	function url1(){
		var url = document.getElementById("url").value;
		var username = document.getElementById("username").value;
		if(url == "https://dev106062.service-now.com" && username == "admin"){
			document.getElementById("form").action = "index";
			document.getElementById("form").submit();
		}
		else{
			alert("Enter proper URL or Username!");
		}
			    
	}
  
	</script>-->
	
</body>
</html>