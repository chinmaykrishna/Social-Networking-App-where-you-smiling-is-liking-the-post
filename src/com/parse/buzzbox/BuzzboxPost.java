package com.parse.buzzbox;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 *  text 
 *  user - author (ParseUser)
 *  userId - author parseUser objectId 
 *  NoOfEmpathizes
 *  IsEmpathised - true or false of current user
 *  location of post creation 
 */
@ParseClassName("Posts")
public class BuzzboxPost extends ParseObject {
	
	public BuzzboxPost()
	{
		
	}
	
  public String getText() {
    return getString("text");
  }

  public void setText(String value) {
    put("text", value);
  }

  public void setUser(ParseUser value) {
	    put("user", value);
	  }
  
  public ParseUser getUser() {
    return getParseUser("user");
  }
  
  public void setUserId(String value) {
	    put("user_id", value);
	  }

public String getUserId() {
  return getString("user_id");
}
  
  
  
  
  public void Init(String key, int mood){
	  put(key,0);
	  put("IsEmpathized","false");
	  put("mood",mood);
	  //put("NoOfEmpathizes",val);
  }
  
  public int getNoofPosts(){
	  return ParseUser.getCurrentUser().getInt("noOfPosts");
  }

  
  
  public int no_of_empathizes(){
	  return getInt("NoOfEmpathizes");
  }
  
  public void set_no_of_empathizes(int value){
	  put("NoOfEmpathizes", value);
  }
  
  public void add_to_empathizes(){
	  int temp = getInt("NoOfEmpathizes");
	  temp++;
	  put("NoOfEmpathizes",temp);
	  put("IsEmpathized","true");
  }
  
  public String IsEmpathized(){
	  return getString("IsEmpathized");
  }

  public ParseGeoPoint getLocation() {
    return getParseGeoPoint("location");
  }

  public void setLocation(ParseGeoPoint value) {
    put("location", value);
  }

  
  public static ParseQuery<BuzzboxPost> getQuery() {
    return ParseQuery.getQuery(BuzzboxPost.class);
  }
}


