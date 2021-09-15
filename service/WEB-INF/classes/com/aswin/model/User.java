package com.aswin.model;

public class User {
	private String number;
	private String short_description;
	private String sys_id;
	private String updated_on;
	private String id;
	
	public User(String id, String number, String short_description, String sys_id, String updated_on)
	{
		this.id = id;
		this.number = number;
		this.short_description = short_description;
		this.sys_id = sys_id;
		this.updated_on = updated_on;
	}
	
	public String getId()
	{
		return id;
	}
	public String getNumber()
	{
		return number;
	}
	public String getShortDescription()
	{
		return short_description;
	}
	public String getSysId()
	{
		return sys_id;
	}
	public String getUpdatedOn()
	{
		return updated_on;
	}

}
