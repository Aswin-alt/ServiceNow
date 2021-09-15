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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.json.JSONArray;
import org.json.JSONObject;

import com.aswin.dao.ServiceNowDAO;
import com.aswin.model.User;

/**
 * Servlet implementation class ServiceNowSys
 */
//@WebServlet("/servicenowsys")
public class ServiceNowSys extends HttpServlet {
	HttpURLConnection con = null;
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceNowSys() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int statuscode;
		HttpSession session = request.getSession();
		
		//Session check
		try{
			String log = session.getAttribute("logstat").toString();
			//session.setMaxInactiveInterval(60);
		}
		catch(Exception e){
			response.sendRedirect("timeout.jsp");
		}
		String url1 = session.getAttribute("url").toString();
		String username = session.getAttribute("username").toString();
		String password = session.getAttribute("password").toString();
		String id = session.getAttribute("id").toString();
		
        try {
        	
        	response.setContentType("text/html; charset=utf-8");
        	PrintWriter out = response.getWriter();
        	
        	String sys = request.getParameter("sys");
			URL url = new URL(url1 + "/api/now/table/incident/" + sys);
			
            con = (HttpURLConnection) url.openConnection();
            
            con.setRequestMethod("GET");
            
            String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode( 
	           auth.getBytes(Charset.forName("US-ASCII")) );
	        String authHeader = "Basic " + new String( encodedAuth );
	        
            con.setRequestProperty("Authorization", authHeader);
            
            statuscode = con.getResponseCode();
            
            //Check success or not
            if(statuscode == 200) {
	            out.println("<h4>Status code : " + statuscode + "</h4>");
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
	
	            out.println(responseBody);
	            
	            User u = null;
	            ServiceNowDAO dao = null;
	            
	            //JSON Parser
				//Servicenow
				JSONObject json = new JSONObject(responseBody);
				JSONObject result = (JSONObject)json.get("result");
				String number = result.getString("number");
				String about = result.getString("short_description");
				String sys_id = result.getString("sys_id");
				String created_on = result.getString("sys_created_on");
				String updated_on = result.getString("sys_updated_on");
				
				//String open = result.getString("opened_by");
				
				try {
					//User details are sent to model layer
					u = new User(id, number, about, sys_id, updated_on);
					
					//User details are stored in database
					dao = new ServiceNowDAO();
					dao.insertData(u);
				}
				catch(SQLIntegrityConstraintViolationException e) {
					out.println("<br>The Data already present in Local Database!");
				}
					
				out.println("<br>Number : " + number + "<br>Short_description : " + about + "<br>Sys ID : " + sys_id + "<br>Created On : " + created_on + "<br>Updated On : " + updated_on + "<br>");
            }
            else {
            	out.println("Sorry!Couldnot READ data, the incident does not exist!");
            	out.println("<br>With Status code : " + statuscode);
            	
            }
            
          //Back Button
			out.println("<form action=\"servicenowsys.jsp\" method=\"get\">");
			out.println("<br><br><button type=\"submit\" name=\"sys\" value=\"" + sys + "\">Back</button>");
        }
        catch(Exception e) {
        	System.out.println(e);
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
		
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		String sys = request.getParameter("sys");
		
		//Session
		HttpSession session = request.getSession();
		session.setAttribute("sys",sys);
		
		String task = request.getParameter("tasktype");
		if(task.equals("fetch")) {
			doGet(request,response);
		}
		if(task.equals("delete")) {
			doDelete(request,response);
		}
		if(task.equals("update")) {
			doPut(request,response);
		}
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.sendRedirect("update.jsp");
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		int statuscode;
		String number = "", about = "", updated_on = "";
        try {
        	//Setting content type to text/html
        	response.setContentType("text/html; charset=utf-8");
        	PrintWriter out = response.getWriter();
        	
        	HttpSession session = request.getSession();
    		String url1 = session.getAttribute("url").toString();
    		String username = session.getAttribute("username").toString();
    		String password = session.getAttribute("password").toString();
    		String id = session.getAttribute("id").toString();
    		
    		String sys = request.getParameter("sys");
    		
    		
        	String url = url1 + "/api/now/table/incident/" + sys;
			
			URL myurl = new URL(url);
            
            con = (HttpURLConnection) myurl.openConnection();
            
            con.setRequestMethod("DELETE");
            
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode( 
               auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
    	    
    	    con.setRequestProperty("Authorization", authHeader);
    	    
    	    //Status Code
            statuscode = con.getResponseCode();
            
            //Check success or not
            if(statuscode == 204 || statuscode == 200) {
            	out.println("<h1>DELETED!</h1><br>");
	            out.println("Status code : " + statuscode);
	            
	            	User u = null;
	            	ServiceNowDAO dao = null;
	    				
	    			//User details are sent to model layer
	    			u = new User(id, number, about, sys, updated_on);
	    			
	    			//User details are stored in database
	    			dao = new ServiceNowDAO();
	    			dao.DeleteData(u);
	    	       
            }
            else {
            	out.println("Sorry!Couldnot DELETE, the incident does not exist!");
            	out.println("<br>With Status code : " + statuscode);
            }
            
          //Back Button
			out.println("<form action=\"servicenow\" method=\"get\">");
			out.println("<br><br><button type=\"submit\" name=\"sys\" value=\"" + sys + "\">Back</button></form>");
            
        } 
        catch(Exception e) {
        	System.out.println(e);
        }
        finally {

            con.disconnect();
        }
	}

}
