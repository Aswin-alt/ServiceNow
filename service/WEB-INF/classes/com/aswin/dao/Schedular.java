package com.aswin.dao;

import java.util.TimerTask;

import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aswin.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;
import java.util.Date;

public class Schedular extends TimerTask {
	Date current;
	int flag;
	String id;
	String url1;
	String username;
	String password;
	
	private static HttpURLConnection con;
	int statuscode;
	
	public Schedular(String id, String url1, String uername, String password, int flag){
		this.id = id;
		this.url1 = url1;
		this.username = uername;
		this.password = password;
		this.flag = flag;
	}
	
	@Override
	public void run(){
		try {
			
			int len = 0, max_len = 0;
			//Delete
			GetMethod g = new GetMethod(id, url1, username, password, flag);
			g.get();
			
		do {
			String query_limit;
			String url;
			if(flag == 0) {
	    		query_limit = "?sysparm_query=ORDERBYsys_created_on&sysparm_fields=number%2Cshort_description%2Csys_id%2Csys_created_on%2Csys_updated_on%2Ccalendar_duration&sysparm_limit=5&sysparm_offset=" + len;
				url = url1 + "/api/now/table/incident" + query_limit;
				System.out.println("1st 5min Check!");
				//flag = 1;
			}
			else {
				query_limit = "?sysparm_query=ORDERBYsys_created_on%5Esys_updated_onONLast%2015%20minutes%40javascript%3Ags.beginningOfLast15Minutes()%40javascript%3Ags.endOfLast15Minutes()&sysparm_fields=number%2Cshort_description%2Csys_id%2Csys_created_on%2Csys_updated_on%2Ccalendar_duration&sysparm_limit=5&sysparm_offset=" + len;
				//String query_param = "?sysparm_query=sys_updated_onONLast%2015%20minutes%40javascript%3Ags.beginningOfLast15Minutes()%40javascript%3Ags.endOfLast15Minutes()";
				url = url1 + "/api/now/table/incident" + query_limit;
				System.out.println("5min Check!");
			}
			
			URL myurl = new URL(url);
			
	        con = (HttpURLConnection) myurl.openConnection();
	        
	        con.setRequestMethod("GET");
	        
	        //Autherization
	        String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode( 
	           auth.getBytes(Charset.forName("US-ASCII")) );
	        String authHeader = "Basic " + new String( encodedAuth );
	        //String authHeader = "Basic " + "YWRtaW46OFk3bnJTQUNlYmdH";
	        
	        con.setRequestProperty("Authorization", authHeader);
	        
	        statuscode = con.getResponseCode();
	        System.out.println(statuscode);
	      
	        
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
				
				 User u = null;
		         ServiceNowDAO dao = null;
		         
		         max_len = result.length();
				
				for(int i=0;i<result.length();i++) {
					JSONObject obj = (JSONObject) result.get(i);
					String number = obj.getString("number");
					String about = obj.getString("short_description");
					String sys_id = obj.getString("sys_id");
					//String created_on = obj.getString("sys_created_on");
					String updated_on = obj.getString("sys_updated_on");
					System.out.println(about);
					
					try {
						//User details are sent to model layer
						u = new User(id, number, about, sys_id, updated_on);
						
						//User details are stored in database
						dao = new ServiceNowDAO();
						dao.insertData(u);
						System.out.println("Create");
					}
					catch(SQLIntegrityConstraintViolationException e) {
						//User details are sent to model layer
						u = new User(id, number, about, sys_id, updated_on);
						
						//User details are stored in database
						dao = new ServiceNowDAO();
						String updated = dao.getData(u);
						
						if(!updated_on.equals(updated)) {
							
							dao.updateData(u);
							System.out.println("Update");
						}
						else {
							continue;
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				len = len + 5;
				System.out.println(max_len);
			}while(max_len == 5);
		}
		catch(Exception e) {
		}
		finally{
			flag = 1;
		}
	}
}
