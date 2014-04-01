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
  public String getText() {
    return getString("text");
  }

  public void setText(String value) {
    put("text", value);
  }

  public ParseUser getUser() {
    return getParseUser("user");
  }

  public void setUser(ParseUser value) {
    put("user", value);
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


