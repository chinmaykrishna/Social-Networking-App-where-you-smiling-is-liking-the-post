package com.parse.buzzbox;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
    protected double Latitude,Longitude;
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
		
		if(currentLocation!=null) lastLocation= currentLocation;
		
		Log.d("current loc", currentLocation.toString());
		Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
		ParseGeoPoint p = geoPointFromLocation(myLoc);
		setQuery(p);
	        
	        
	}
	
	// This method will Set up our customized query and will then update the List View.
	public void setQuery(final ParseGeoPoint pgp){
		// Set up a customized query
	    ParseQueryAdapter.QueryFactory<BuzzboxPost> factory =
	        new ParseQueryAdapter.QueryFactory<BuzzboxPost>() {
	          public ParseQuery<BuzzboxPost> create() {
	            
	            ParseQuery<BuzzboxPost> query = BuzzboxPost.getQuery();
	            query.include("user");
	            query.orderByDescending("createdAt");
	            query.whereWithinKilometers("location", pgp, SEARCH_RADIUS);
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
	

	  //Post button clicked. This button will navigate the user to a new Activity where he will be able to post.
	public void post_message(View v)
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
					
					EditText pnre = (EditText)dialog.findViewById(R.id.location);
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
	
	
		// If the user wants to set a different radius.
	  public void Location(View v)
	  {
		  
			final ParseUser currentUser = ParseUser.getCurrentUser();
			
			//pop up a dialog box
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.new_post);
			dialog.setTitle("Enter Location");			
			Button dialogButtonA = (Button) dialog.findViewById(R.id.dialogButtonOK);
			Button dialogButtonC = (Button) dialog.findViewById(R.id.dialogButtonCancel);
			
			//cancel button clicked
			dialogButtonC.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			
			//Go button clicked
			dialogButtonA.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					EditText location = (EditText)dialog.findViewById(R.id.location);
					String loc = location.getText().toString();
					if(loc.trim().length()>=1)
					{
						//now find that place and set current location to this place.
						dialog.dismiss();
						new FindPlace().execute(loc);
						
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
	  
	  // This class will search for the new Location in a background thread when the user sets another location.
	  private class FindPlace extends AsyncTask<String,Void, JSONObject> {
			 
			 ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
		 	     String add=null;


			    @Override
			    protected void onPreExecute() {
			        super.onPreExecute();

			        //this method will be running on UI thread
			        pdLoading.setMessage("\tLoading...");
			        pdLoading.show();
			    }
			    
		     protected JSONObject doInBackground(String... url) {
		    	String uri = "http://maps.google.com/maps/api/geocode/json?address=" + url[0] + "&sensor=false";
		    	HttpGet httpGet = new HttpGet(uri);
		 	    HttpClient client = new DefaultHttpClient();
		 	    HttpResponse response;
		 	    StringBuilder stringBuilder = new StringBuilder();
		 	    try {
		 	        response = client.execute(httpGet);
		 	        HttpEntity entity = response.getEntity();
		 	        InputStream stream = entity.getContent();
		 	        int b;
		 	        

		 	        while ((b = stream.read()) != -1) {
		 	            stringBuilder.append((char) b);
		 	        }
		 	    } catch (ClientProtocolException e) {
		 	        e.printStackTrace();
		 	    } catch (IOException e) {
		 	        e.printStackTrace();
		 	    }

		 	    JSONObject jsonObject = new JSONObject();
		 	    try {
		 	        jsonObject = new JSONObject(stringBuilder.toString());

		 	       return jsonObject;


		 	    } catch (JSONException e) {

		 			return null;
		 	    }
				
		 	       
		     }

		     protected void onPostExecute(JSONObject result) {
		         pdLoading.dismiss();
		         if(result !=null)
		         {
			 	       try {
						Longitude = ((JSONArray)result.get("results")).getJSONObject(0)
						            .getJSONObject("geometry").getJSONObject("location")
						            .getDouble("lng");
						Latitude = ((JSONArray)result.get("results")).getJSONObject(0)
				 	            	.getJSONObject("geometry").getJSONObject("location")
				 	            	.getDouble("lat");
						ParseGeoPoint p = new ParseGeoPoint(Latitude,Longitude);
						setQuery(p);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				 	   

		         }
		         else {
		        	 Toast mtoast = Toast.makeText(MainActivity.this, "Cannot Find this place. Try setting another Location", Toast.LENGTH_SHORT);
		     		 mtoast.show();
		         }
		     }
		     
		     
		    	 
		     }
	  
	  /*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private static ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	  }


}
