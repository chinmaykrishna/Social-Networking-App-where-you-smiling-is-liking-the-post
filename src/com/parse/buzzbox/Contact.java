package com.parse.buzzbox;


//Simple contact object commonly used to store friends 
public class Contact {
	
	String phone,name,object_id;
	
	public Contact(String ph_no, String name,String Obj_id)
	{
		phone = ph_no;
		this.name = name;
		object_id = Obj_id;
	}
	
	public void setPhoneNumber(String ph_no)
	{
		phone  = ph_no;
	}

	
	public String getPhoneNumber()
	{
		return phone;
	}
	
	public void setObjectID(String Obj_id)
	{
		object_id = Obj_id;
	}

	
	public String getObjectID()
	{
		return object_id ;
	}
	
	public void setName(String name)
	{
		this.name  = name;
	}

	public String getName()
	{
		return name;
	}
	
}
