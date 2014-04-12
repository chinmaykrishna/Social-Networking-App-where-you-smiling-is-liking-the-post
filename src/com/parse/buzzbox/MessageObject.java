package com.parse.buzzbox;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Messages")
public class MessageObject extends ParseObject {

	//Default constructor
	public MessageObject() 
	{
		
	}
	
	//Message Text
	public String getText() 
	{    
		return getString("text");
	}

	public void setText(String value) 
	{
	    put("text", value);
	}
	
	//Message intended to user
	public void toUserObjectID(String value) 
	{
		put("toobjectid", value);
	}
	
	/*Message Type: Message is via post messsage or personally sent after searching users mail id
	 * Via post message type: "via_post"
	 * personal message type: "personal" 
	 */
	
	public void setType(String value) 
	{
		put("type", value);
	}
	
	public String getType()
	{
		return getString("type");
	}
	
	//post via which message came 
	
	public void setViaPost(String value) 
	{
		put("post", value);
	}
	
	public String getViaPost()
	{
		return getString("post");
	}
	
	public static ParseQuery<MessageObject> getQuery() {
	    return ParseQuery.getQuery(MessageObject.class);
	  }
}
