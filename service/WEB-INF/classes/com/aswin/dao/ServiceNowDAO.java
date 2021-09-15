package com.aswin.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;

import com.aswin.model.User;

public class ServiceNowDAO {
	
	Connection con=null;
	Statement st = null;
	Timer timer;
	Schedular s;
	public ServiceNowDAO() throws ClassNotFoundException, SQLException
	{
		Class.forName("com.mysql.cj.jdbc.Driver"); 
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/servicenow?useSSL=false","root","aswin123");
		
	}
	
	public void insertData(User user) throws SQLException {
		String INSERT_DATA = "insert into incident" + user.getId() + "(number, short_description, Sys_id, updated_on) values(?, ?, ?, ?)";
		PreparedStatement pr = con.prepareStatement(INSERT_DATA);
		pr.setString(1, user.getNumber());
		pr.setString(2, user.getShortDescription());
		pr.setString(3, user.getSysId());
		pr.setString(4, user.getUpdatedOn());
		
		pr.executeUpdate();
		
		pr.close();
		con.close();
	}
	
	public void insertUser(String url, String username, String password) throws SQLException {
		String INSERT_DATA = "insert into user(url, username, password) values(?, ?, ?)";
		PreparedStatement pr = con.prepareStatement(INSERT_DATA);
		pr.setString(1, url);
		pr.setString(2, username);
		pr.setString(3, password);
		
		pr.executeUpdate();
		
		pr.close();
		con.close();
	}
	
	public void updateData(User user) throws SQLException {
		String INSERT_DATA = "update incident" + user.getId() + " set short_description = ?, Sys_id = ? where number = ?;";
		PreparedStatement pr = con.prepareStatement(INSERT_DATA);
		pr.setString(3, user.getNumber());
		pr.setString(1, user.getShortDescription());
		pr.setString(2, user.getSysId());
		
		pr.executeUpdate();
		
		pr.close();
		con.close();
	}
	
	public void DeleteData(User user) throws SQLException {
		
		String DELETE_DATA = "Delete from incident" + user.getId() + " where Sys_id = ?;";
		PreparedStatement pr = con.prepareStatement(DELETE_DATA);
		pr.setString(1, user.getSysId());
		
		pr.executeUpdate();
		
		pr.close();
		con.close();
	}
	
	public String getData(User user) throws SQLException{
		String SysId = user.getSysId();
		String view_query = "Select * from incident" + user.getId() + " where Sys_id=\"" + SysId + "\"";
		st = con.createStatement();
		ResultSet rs = st.executeQuery(view_query);
		rs.next();
		String update = rs.getString(5);
		return update;
	}
	
	public String getUser(String url) throws SQLException{
		String view_query = "select id from user where url = \"" + url + "\"";
		st = con.createStatement();
		ResultSet rs = st.executeQuery(view_query);
		rs.next();
		String id = rs.getString(1);
		return id;
	}
	
	
	public void getAllUser() throws SQLException, IOException, ClassNotFoundException{
		String view_query = "Select * from user";
		st = con.createStatement();
		ResultSet rs = st.executeQuery(view_query);
		while(rs.next()) {
			int flag = 0;
			String id = rs.getString(1);
			String url1 = rs.getString(2);
			String username = rs.getString(3);
			String password = rs.getString(4);
			//Event Scheduler
			timer = new Timer();
			s = new Schedular(id, url1, username, password, flag);
			timer.scheduleAtFixedRate(s, 10000, 300000);
			//timer.scheduleAtFixedRate(s, 10000, 60000);
			
		}
		
	}
	
	public void DeleteData1(String id, String number) throws SQLException {
		
		String DELETE_DATA = "Delete from incident" + id + " where number = \"" + number + "\";";
		PreparedStatement pr = con.prepareStatement(DELETE_DATA);
		
		pr.executeUpdate();
		System.out.println("DEleted");
		pr.close();
		con.close();
	}
	
	public void createTable(String id) throws SQLException {
		
		String CREATE = "create table incident" + id + "(id integer PRIMARY KEY not null auto_increment, number varchar(50) not null unique,short_description varchar(100) not null, Sys_id varchar(100) not null unique, updated_on varchar(50) not null);";
		PreparedStatement pr = con.prepareStatement(CREATE);
		
		pr.executeUpdate();
		
		pr.close();
		con.close();
	}

}
