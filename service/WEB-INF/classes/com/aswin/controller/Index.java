package com.aswin.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aswin.dao.Schedular;
import com.aswin.dao.ServiceNowDAO;
import com.aswin.model.User;

/**
 * Servlet implementation class Index
 */
//@WebServlet("/index")
public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static HttpURLConnection con;
	ServiceNowDAO dao = null;
	Timer timer;
	Schedular s;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Index() {
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
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		
		String log = request.getParameter("logstat");
		
		//String task1 = request.getParameter("task");
		if(log.equals("logout")) {
			doDelete(request,response);
		}
		else if(log.equals("login")){
			
			String url1 = request.getParameter("url");
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			String url = url1 + "/api/now/table/incident";
			URL myurl = new URL(url);
			
			//HTTP status
            con = (HttpURLConnection) myurl.openConnection();
            
            con.setRequestMethod("GET");
            
            //Autherization
            String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode( 
	           auth.getBytes(Charset.forName("US-ASCII")) );
	        String authHeader = "Basic " + new String( encodedAuth );
	        
            con.setRequestProperty("Authorization", authHeader);
            
            int statuscode = con.getResponseCode();
			
			//Validation
			if (statuscode == 401) {
				out.println("Enter correct password!");
				out.println("<br><a href = \"index.jsp\">Back</a>");
	        }
			
			else {
			
				//session
				HttpSession session = request.getSession();
				session.setAttribute("url", url1);
				session.setAttribute("password", password);
				session.setAttribute("username", username);
				session.setAttribute("logstat", log);
				
				try {
					int flag = 0;
					//User details are stored in database
					dao = new ServiceNowDAO();
					dao.insertUser(url1, username, password);
					
					dao = new ServiceNowDAO();
					String id = dao.getUser(url1);
					session.setAttribute("id", id);
					
					dao = new ServiceNowDAO();
					dao.createTable(id);
					
					//Event Scheduler
					timer = new Timer();
					s = new Schedular(id, url1, username, password, flag);
					timer.scheduleAtFixedRate(s, 10000, 300000);
					//timer.scheduleAtFixedRate(s, 10000, 10000);
				}
				catch(SQLIntegrityConstraintViolationException e) {
					String id = null;
					try {
						dao = new ServiceNowDAO();
					} catch (ClassNotFoundException | SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						id = dao.getUser(url1);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					session.setAttribute("id", id);
					
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
				response.sendRedirect("servicenow.jsp");
				}
			}
			
		}
	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();
//		session.removeAttribute("url");
//		session.removeAttribute("username");
//		session.removeAttribute("password");
//		session.setAttribute("logstat", "logout");
		//timer.cancel();
		//s.cancel();
		System.out.println("Bye!");
		response.sendRedirect("index.jsp");
	}
}
