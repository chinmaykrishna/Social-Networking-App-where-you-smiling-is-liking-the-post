package com.parse.buzzbox;

import java.io.Serializable;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Messages_Thread")
public class MessageObject extends ParseObject implements Serializable{

	//Default constructor
	public MessageObject() 
	{
		
	}
	
	public String getText() {
	    return getString("text");
	  }
	
	  public void setText(String value) {
	    put("text", value);
	  }
	
	  public void setMood(int id)
	  {
		  put("mood",id);
	  }
	  
	  public int getMood()
	  {
		  return getInt("mood");
	  }
	  
	  public ParseUser getAuthor() {
	    return getParseUser("user");
	  }
	  
	  public void setAuthor(ParseUser value) {
	    put("user", value);
	  }
	  
	  public String getAuthorName() {
		    return getString("user_name");
		  }
		  
		  public void setAuthorName(String value) {
		    put("user_name", value);
		  }
		  
		  public String getAuthorAvatar() {
			    return getString("user_avatar");
			  }
			  
			  public void setAuthorAvatar(String value) {
			    put("user_avatar", value);
			  }
			  
	  public void setEmpathised(boolean bool)
	  {
		  put("emp",bool);
	  }
	  
	  public boolean getEmpathised()
	  {
		 return getBoolean("emp");
	  }
	  
	  public void setReceipentObjID(String user)
	  {
		  put("receipent",user);
	  }
	  
	  public String getReceipent()
	  {
		  return getString("receipent");
	  }
	  
	  public void addComment(String comment, ParseUser user)
	  {
		  add("comments", comment);
		  add("comment_author",user);
	  }
	  
	  public List<String> getCommentList()
	  {
		return getList("comments");  
	  }
	  
	  public List<ParseUser> getCommentAuthors()
	  {
		return getList("comment_authors");  
	  }
	  
	  public void setViaPost(String str)
	  {
		  put("viaPost",str);	  
	  }
	  
	  public String getViaPost()
	  {
		  return getString("viaPost");
	  }
	  
	  public static ParseQuery<MessageObject> getQuery() {
		    return ParseQuery.getQuery(MessageObject.class);
	  }
}
