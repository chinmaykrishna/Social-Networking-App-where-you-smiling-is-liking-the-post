package com.parse.buzzbox;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Comments")
public class CommentsObject extends ParseObject {

	//Default constructor
	public CommentsObject() 
	{
		
	}
	
	//Comment Text
	public String getText() 
	{    
		return getString("text");
	}

	public void setText(String value) 
	{
	    put("text", value);
	}
	
	//comment intended to post
	public void toPost(String postId) 
	{
		put("topost", postId);
	}

	
	public static ParseQuery<CommentsObject> getQuery() {
	    return ParseQuery.getQuery(CommentsObject.class);
	  }
}
