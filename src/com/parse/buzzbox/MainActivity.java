package com.parse.buzzbox;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class MainActivity extends Activity {
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static final int SEARCH_RADIUS=100;
	private Location lastLocation = null;
	  private Location currentLocation = null;

	private ParseQueryAdapter<BuzzboxPost> posts;

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
	            //query.setLimit(MAX_POST_SEARCH_RESULTS);
	            return query;
	          }
	        };
	        
	        // Set up the query adapter
	        posts = new ParseQueryAdapter<BuzzboxPost>(this, factory) {
	          @Override
	          public View getItemView(BuzzboxPost post, View view, ViewGroup parent) {
	            if (view == null) {
	              view = View.inflate(getContext(), R.layout.buzzbox_post_item, null);
	            }
	            TextView contentView = (TextView) view.findViewById(R.id.contentView);
	            TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
	            contentView.setText(post.getText());
	            usernameView.setText(post.getUser().getUsername());
	            return view;
	          }
	        };
	        
	     // Attach the query adapter to the view
	        ListView postsView = (ListView) this.findViewById(R.id.postsView);
	        postsView.setAdapter(posts);
	}
	
	/*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	  }

}
