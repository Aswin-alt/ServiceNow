package com.aswin.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.aswin.dao.ServiceNowDAO;
import com.aswin.model.User;

/**
 * Servlet implementation class ServiceNowUpdate
 */
//@WebServlet("/servicenowupdate")
public class ServiceNowUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
    HttpURLConnection con = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceNowUpdate() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession();
		
		//Session check
		try{
			String log = session.getAttribute("logstat").toString();
					//session.setMaxInactiveInterval(60);
		}
		catch(Exception e){
			response.sendRedirect("timeout.jsp");
		}
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		//Get the parameter value
	    String caller_id = request.getParameter("caller_id");
	    String short_description = request.getParameter("short_description");
		
		
		String url1 = session.getAttribute("url").toString();
		String username = session.getAttribute("username").toString();
		String password = session.getAttribute("password").toString();
		String sys = session.getAttribute("sys").toString();	
		String id = session.getAttribute("id").toString();	
		
		String url = url1 + "/api/now/table/incident/" + sys;
		
		URL myurl = new URL(url);
        con = (HttpURLConnection) myurl.openConnection();
	
	    //add reuqest header
	    con.setRequestMethod("PUT");
	    con.setRequestProperty("User-Agent", "Mozilla/5.0");
	    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    con.setRequestProperty("Content-Type", "application/json");
	    
	    //Authenticate
	    String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode( 
           auth.getBytes(Charset.forName("US-ASCII")) );
        String authHeader = "Basic " + new String( encodedAuth );
	    
	    con.setRequestProperty("Authorization", authHeader);
	    
	    JSONObject result = new JSONObject();
	    
	    if((short_description == null && caller_id == null) || short_description.equals("") && caller_id.equals("")) {
	    	out.println("Enter some value!!!");
	    	out.println("<br><a href=\"update.jsp\">Back</a>");
	    }
	    
	    else {
		    if((caller_id.equals("") || caller_id == null) && short_description != null) {
		    	// putting data to JSONObject
			    result.put("short_description", short_description);
		    }
		    
		    else if((short_description.equals("") || short_description == null) && caller_id != null) {
		    	// putting data to JSONObject
			    result.put("caller_id", caller_id);
		    }
		    
		    else if(short_description != null && caller_id != null) {
		    	// putting data to JSONObject
			    result.put("caller_id", caller_id);
			    result.put("short_description", short_description);
		    }
	       
		    String urlParameters = result.toString();
		    
		    
		    // Send post request
		    con.setDoOutput(true);	
		    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
		        wr.writeBytes(urlParameters);
		        wr.flush();
		    }
		    
		    int statuscode = con.getResponseCode();
		    
		    if(statuscode == 200) {
			    out.println("<h2>Updated!!!</h2><br>");
			    out.println("<h4>'PUT' request to URL : </h4>" + url);
			    out.println("<br>Put parameters : " + urlParameters);
			    out.println("<br>Status Code : " + statuscode + "<br>");
			
			    try (BufferedReader in = new BufferedReader(
			            new InputStreamReader(con.getInputStream()))) {
			
			        String line;
			        StringBuilder response1 = new StringBuilder();
			
			        while ((line = in.readLine()) != null) {
			            response1.append(line);
			        }
			
			        //print result
			        //out.println(response1.toString());
			        
			        //Parse JSON data
			        JSONObject json = new JSONObject(response1.toString());
			        JSONObject obj = (JSONObject)json.get("result");
					
					User u = null;
					ServiceNowDAO dao = null;
					
					String number = obj.getString("number");
					String about = obj.getString("short_description");
					String sys_id = obj.getString("sys_id");
					String created_on = obj.getString("sys_created_on");
					String updated_on = obj.getString("sys_updated_on");
					
					//Print Data
					out.println("<br>Number : " + number + "<br>Short_description : " + about + "<br>Sys ID : " + sys_id + "<br>Created On : " + created_on + "<br>Updated On : " + updated_on + "<br>");
			           	
					//User details are sent to model layer
					u = new User(id, number, about, sys_id, updated_on);
					
					//User details are stored in database
					dao = new ServiceNowDAO();
					dao.updateData(u);
		        
			    } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    finally {
			    	con.disconnect();
			    }
		    }
		    else {
		    	out.println("Sorry!Couldnot UPDATE, the incident does not exist!");
	        	out.println("<br>With Status code : " + statuscode);
		    }
		    out.println("<form action=\"servicenowsys.jsp\" method=\"get\">");
			out.println("<button type=\"submit\" name=\"sys\" value=\"" + sys + "\">Back</button></form>");
		}
	}

}
