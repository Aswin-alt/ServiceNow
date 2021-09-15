package com.aswin.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aswin.model.User;

public class GetMethod {
	String url1;
	String password;
	String id;
	String username;
	int flag;
	
	private static HttpURLConnection con = null;
	int statuscode;
	
	public GetMethod(String id, String url1, String username, String password, int flag) {
		this.id = id;
		this.url1 = url1;
		this.username = username;
		this.password = password;
		this.flag = flag;
	}
	
	public void get() throws IOException, SQLException, ClassNotFoundException {
		
		int len = 0, max_len = 0;
		try {
		do {
			String query_limit;
			String url;
//			if(flag == 0) {
//	    		query_limit = "?sysparm_query=ORDERBYsys_created_on%5Etablename%3Dincident&sysparm_fields=sys_id%2Ctablename%2Cdisplay_value&sysparm_limit=5&sysparm_offset=" + len;
//				url = url1 + "/api/now/table/sys_audit_delete" + query_limit;
//				System.out.println("1st 5min Check!DELETE");
//			}
//			else {
				query_limit = "?sysparm_query=ORDERBYsys_created_on%5Etablename%3Dincident%5Esys_updated_onONLast%2015%20minutes%40javascript%3Ags.beginningOfLast15Minutes()%40javascript%3Ags.endOfLast15Minutes()&sysparm_fields=sys_id%2Ctablename%2Cdisplay_value&sysparm_limit=5&sysparm_offset=" + len;
				url = url1 + "/api/now/table/sys_audit_delete" + query_limit;
				System.out.println("5min Check!DELETE");
//			}
			
			
			URL myurl = new URL(url);
			
	        con = (HttpURLConnection) myurl.openConnection();
	        con.setRequestMethod("GET");
//	        con.setRequestProperty("Content-Type","application/json");
//	        con.setRequestProperty("Accept","application/json");
	        
	      //Autherization
	        String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode( 
	           auth.getBytes(Charset.forName("US-ASCII")) );
	        String authHeader = "Basic " + new String( encodedAuth );
	        //String authHeader = "Basic " + "YWRtaW46OFk3bnJTQUNlYmdH";
	        //System.out.println(authHeader);
	        
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
	       	System.out.println(responseBody);	
	      //JSON Parser
			//Servicenow
			JSONObject json = new JSONObject(responseBody);
			JSONArray result = (JSONArray)json.get("result");
			
	         ServiceNowDAO dao = null;
	         
	         max_len = result.length();
				
			for(int i=0;i<result.length();i++) {
				JSONObject obj = (JSONObject) result.get(i);
				String number = obj.getString("display_value");
				System.out.println(number);	
				try {
						
					//User detail is deleted from database
					dao = new ServiceNowDAO();
					dao.DeleteData1(id, number);
					System.out.println("Deleted");
				}
				catch(SQLIntegrityConstraintViolationException e) {
					continue;
				}
				
			}
			len = len + 5;
			System.out.println(max_len);
		}while(max_len == 5);
		}
		finally {
			if (con != null) {
		        try {
		            con.disconnect();
		        }catch (Exception ex) {
		            System.out.println("Error");
		        }
			}
		}
	}
}
