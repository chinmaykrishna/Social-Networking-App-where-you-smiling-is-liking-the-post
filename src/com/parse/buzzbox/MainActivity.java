package com.parse.buzzbox;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MainActivity extends Activity {
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static final int SEARCH_RADIUS=100;
	private Location lastLocation = null;
	  private Location currentLocation = null;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 // Set up a customized query
	    ParseQueryAdapter.QueryFactory<BuzzboxPost> factory =
	        new ParseQueryAdapter.QueryFactory<BuzzboxPost>() {
	          public ParseQuery<BuzzboxPost> create() {
	            Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
	            ParseQuery<BuzzboxPost> query = BuzzboxPost.getQuery();
	            query.include("user");
	            query.orderByDescending("createdAt");
	            query.whereWithinKilometers("location", geoPointFromLocation(myLoc), SEARCH_RADIUS);
	            query.setLimit(MAX_POST_SEARCH_RESULTS);
	            return query;
	          }
	        };
	}
	
	/*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	  }

}
