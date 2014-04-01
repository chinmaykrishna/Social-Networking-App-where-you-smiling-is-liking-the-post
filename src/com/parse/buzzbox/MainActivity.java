package com.parse.buzzbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends Activity {
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static final int SEARCH_RADIUS=100;
	private Location lastLocation = null;
    private Location currentLocation = null;
    private LocationManager locationManager;
    private Context con;
	private ParseQueryAdapter<BuzzboxPost> posts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		con = this;
		locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
		
		currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
		Log.d("current loc", currentLocation.toString());
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
	
	

	  //post button clicked
	  public void post_function(View v)
	  {
		  
			final ParseUser currentUser = ParseUser.getCurrentUser();
			
			//pop up a dialog box
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.new_post);
			dialog.setTitle("New Post");			
			Button dialogButtonA = (Button) dialog.findViewById(R.id.dialogButtonOK);
			Button dialogButtonC = (Button) dialog.findViewById(R.id.dialogButtonCancel);
			
			//cancel button clicked
			dialogButtonC.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			
			//send button clicked
			dialogButtonA.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					EditText pnre = (EditText)dialog.findViewById(R.id.post_message);
					String message = pnre.getText().toString();
					if(message.trim().length()>=1)
					{
						//now send post
						dialog.dismiss();
						BuzzboxPost new_post = new BuzzboxPost();
						new_post.setLocation(geoPointFromLocation(currentLocation));
						new_post.setText(message);
						new_post.setUser(currentUser);
						new_post.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								//successfull
								Toast.makeText(con, "posted", Toast.LENGTH_SHORT).show();
							}
						});
					}
					else
					{
						//invalid message
						Toast.makeText(con, "Please enter a valid message", Toast.LENGTH_SHORT).show();
					}
				}
				
			});
	
			dialog.show();				
	  }
	  
	  /*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private static ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	  }
}
