package com.parse.buzzbox;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class CommentsObject extends ParseObject {

	//Default constructor
	public CommentsObject() 
	{
		
	}
	
	//Comment author
	public void setAuthor(ParseUser value) 
	{
	    put("user", value);
	}
	
	public ParseUser getAuthor() 
	{    
		return getParseUser("user");
	}
	
	//author name
	public void setAuthorName(String value) 
	{
	    put("username", value);
	}
	
	public String getAuthorName() 
	{    
		return getString("username");
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
