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

import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aswin.dao.ServiceNowDAO;
import com.aswin.model.User;

import java.util.Base64;

/**
 * Servlet implementation class ServiceNow
 */
//@WebServlet("/servicenow")
public class ServiceNow extends HttpServlet {
	
	private static HttpURLConnection con;
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceNow() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//Session check
		try{
			HttpSession session= request.getSession();
			response.setContentType("text/html; charset=utf-8");
			PrintWriter out = response.getWriter();
			
			out.println("<h1>READ</h1><br>");
			
			//Normal run
			int statuscode;
        	
    		String url1 = session.getAttribute("url").toString();
    		String password = session.getAttribute("password").toString();
    		String username = session.getAttribute("username").toString();
    		//String id = session.getAttribute("id").toString();
    		
    		int len = 0, max_len = 0;
    		
    		do {
    		
    		String query_limit = "?sysparm_query=ORDERBYsys_created_on&sysparm_fields=number%2Cshort_description%2Csys_id%2Csys_created_on%2Csys_updated_on%2Ccalendar_duration&sysparm_limit=5&sysparm_offset=" + len;
			String url = url1 + "/api/now/table/incident" + query_limit;
        	
			URL myurl = new URL(url);
			
            con = (HttpURLConnection) myurl.openConnection();
            
            con.setRequestMethod("GET");
            
            //Autherization
            String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode( 
	           auth.getBytes(Charset.forName("US-ASCII")) );
	        String authHeader = "Basic " + new String( encodedAuth );
	        
            con.setRequestProperty("Authorization", authHeader);
            
            statuscode = con.getResponseCode();
            
            if(statuscode == 200) {
	            //out.println("<h4>Status code : " + statuscode + "</h4>");
	            StringBuilder content;
	
	            try (BufferedReader in = new BufferedReader(
	                    new InputStreamReader(con.getInputStream()))) {
	
	                String line;
	                content = new StringBuilder();
	                while ((line = in.readLine()) != null) {
	
	                    content.append(line);
	                    content.append(System.lineSeparator());
	                }
	                in.close();
	            }
	            String responseBody = content.toString();
	
	            //out.println(responseBody);
	            
	            //JSON Parser
				//Servicenow
				JSONObject json = new JSONObject(responseBody);
				JSONArray result = (JSONArray)json.get("result");
				
				max_len = result.length();
				
				if(result.length()>0) {
					for(int i=0;i<result.length();i++) {
						JSONObject obj = (JSONObject) result.get(i);
						String number = obj.getString("number");
						String about = obj.getString("short_description");
						String sys_id = obj.getString("sys_id");
						String created_on = obj.getString("sys_created_on");
						String updated_on = obj.getString("sys_updated_on");
						//String open = obj.getString("opened_by");
						
						out.println("<form action=\"servicenowsys.jsp\" method=\"get\">");
						out.println("<br>Number : " + number + "<br>Short_description : " + about + "<br>Sys ID : " + sys_id + "<br>Created On : " + created_on + "<br>Updated On : " + updated_on + "<br>");
						out.println("<button type=\"submit\" name=\"sys\" value=\"" + sys_id + "\">Get this incident</button><br>");
					}
				}
				else {
					out.println("<p>No incident recorded yet to display!</p>");
				}
            }
            else {
            	out.println("Sorry! Couldnot READ, Page not found!");
            	out.println("<br>With Status code : " + statuscode);
            }
          
//            out.println("<form action=\"servicenow.jsp\" method=\"get\">");
//			out.println("<br><br><button type=\"submit\">Back</button></form>");
            len = len + 5;
    		}while(max_len == 5);
    		//Back Button
            out.println("<a href=\"servicenow.jsp\">Back</a>");
		}
        catch(Exception e){
			response.sendRedirect("timeout.jsp");
		}
        finally {

            con.disconnect();
        }
	
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String task = request.getParameter("tasktype");
		if(task.equals("fetch")) {
			doGet(request,response);
		}
		if(task.equals("create")) {
			response.sendRedirect("create.jsp");
		}
	}
}