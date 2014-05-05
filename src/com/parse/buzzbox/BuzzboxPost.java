package com.parse.buzzbox;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


/**
 * Data model for a post.
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

  public ParseUser getUser() {
    return getParseUser("user");
  }
  
  public void setFav(){
	  put("Fav",1);
  }
  
  public int getFav(String key){
	  return getInt(key);
  }
  
  public void Init(String key){
	  put(key,0);
	  put("IsEmpathized","false");
	  //put("NoOfEmpathizes",val);
  }
  
  public int getNoofPosts(){
	  return ParseUser.getCurrentUser().getInt("noOfPosts");
  }

  public void setUser(ParseUser value) {
    put("user", value);
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

  //to put likes, dislikes to post
  
  public static ParseQuery<BuzzboxPost> getQuery() {
    return ParseQuery.getQuery(BuzzboxPost.class);
  }
}


