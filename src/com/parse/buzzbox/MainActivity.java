package com.parse.buzzbox;

import android.R.drawable;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
	private static int SEARCH_RADIUS=100,flag=0;
	private Location lastLocation = null;
    private Location currentLocation = null;
    protected double Latitude,Longitude;
    private LocationManager locationManager;
    private static ParseGeoPoint p;
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
		p = geoPointFromLocation(myLoc);
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
	            
	            if(flag==0) query.orderByDescending("createdAt");	// If the user has not pressed the Featured Button.
	            
	            else query.orderByDescending("no_of_empathizes");	// If the user has pressed the Featured Button.
	            
	            flag=0;
	            
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
	            TextView count = (TextView) view.findViewById(R.id.Count_of_Empathizes);
	            
	            // ImageView im = (ImageView) view.findViewById(R.id.imageView1);
	            // contentView.setBackground();  // We will do this to show the image.
	            contentView.setText(post.getText());
	            count.setText(""+post.no_of_empathizes());
	            usernameView.setText(post.getUser().getUsername());
	            
	            final ImageButton b = (ImageButton) view.findViewById(R.id.favourite);
	            b.setOnClickListener(new OnClickListener(){
	            	
	            	public void onClick(View v){
	            	    	b.setImageResource(drawable.star_big_on);
	            	    		            		  
	            	}
	            });
	            
	            return view;
	          }
	        };
	        
	     // Attach the query adapter to the view
	        
	        	ListView postsView = (ListView) this.findViewById(R.id.postsView);
		        postsView.setAdapter(posts);
		        
		        //posts.clear();
	}
	

	//Post button clicked. This button will navigate the user to a new Activity where he will be able to post.
	public void post_function(View v)
	  {
		 // Intent i = new Intent(MainActivity.class, BuzzFeedPost.class);
		 // MainActivity.this.startActivity(i);				
	  }
	
	public void change_radius(View v){
		
		 //change radius
		 
		//pop up a dialog box
	 final Dialog dialog = new Dialog(this);
	 dialog.setContentView(R.layout.change_radius);
	 dialog.setTitle("Change Radius");	
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
			 if(!(loc.isEmpty())){
				 SEARCH_RADIUS = Integer.parseInt(loc);
				 setQuery(p);	// Update the List.
	
			 }
			 else{
	 			 Toast mtoast = Toast.makeText(MainActivity.this, "Please enter a valid Radius.", Toast.LENGTH_LONG);
	 		 	 mtoast.show();
			 }
		 dialog.dismiss();


		 }

	 });

	 dialog.show();
	}
	
	
	// If user want to create a new post
	  public void new_post_function(View v)
	  {
	  	 final Dialog dialog = new Dialog(this);
		 dialog.setContentView(R.layout.new_post);
		 dialog.setTitle("New Post");	
		 Button choose_bg = (Button) dialog.findViewById(R.id.choose_bg);
		 Button done_but = (Button) dialog.findViewById(R.id.done);
		 final EditText message = (EditText)dialog.findViewById(R.id.message);
		 ImageView bg = (ImageView)dialog.findViewById(R.id.bg);
		 
		 	//done button clicked
			 done_but.setOnClickListener(new OnClickListener() {

				 @Override
				 public void onClick(View v) {
					 //post function
					 if(message.getText().toString().trim().length()<1)
					 {
						 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
					 }
					 else
					 {
						 BuzzboxPost new_post = new BuzzboxPost();
						 new_post.setUser(ParseUser.getCurrentUser());
						 new_post.setText(message.getText().toString().trim());
						 new_post.set_no_of_empathizes(0);
						 new_post.setLocation(geoPointFromLocation(currentLocation));
						 new_post.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e==null)
								{
									Toast.makeText(con, "Successfully posted", Toast.LENGTH_SHORT).show();
									setQuery(p);	// Update the List.
								}
								else
								Toast.makeText(con, "Posting failed. Please check internet connection", Toast.LENGTH_SHORT).show();
								
							}
						});
					 }
					 dialog.dismiss();
				 }
			 });

			 //Choose background button clicked
//			 choose_bg.setOnClickListener(new OnClickListener() {
//				 @Override
//				 public void onClick(View v) {
//					 //choose_bg function
//					 Intent i = new Intent(con,Choose_bg.class);
//					 startActivity(i);
//				 }
//
//			 });

			 dialog.show();	
	  }
	  
	  //Menu configuration
	  public boolean onCreateOptionsMenu(Menu menu){
			
			
			MenuInflater inf=new MenuInflater(this);
				inf.inflate(R.menu.main_activity_menu, menu);
	    	return true;
	     }
	  
	  // This method will simply sort the Posts on the basis of number of highest empathizes.
	  public void featured(View v){
		  
		  flag=1;
		  setQuery(p);
	  }
	  
	  
	  // This method will simply enable user to Empathize a Post.
	  public void empathize(View v){
		  
		  
	  }
	  
	  
	  // This method will simply enable user to mark this post as his favourite.
//	  public void favourite(View v){
//		  
//		  ImageButton b = (ImageButton) v.getTag();
//		  
//		  b.setImageResource(R.drawable.background);
//	  }
	  
	  
	  /*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private static ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
	  }
	  
	  
	  //This inner class will be used in some other versions of the Application.
	  
	  // This class will search for the new Location in a background thread when the user sets another location.
/*	  private class FindPlace extends AsyncTask<String,Void, JSONObject> {
			 
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
		     
		     
		    	 
		     }*/
	  @Override
		public boolean onOptionsItemSelected
									    (MenuItem item) {
		 if(item.getItemId()==R.id.refresh){
			  setQuery(p);
		 }
		 else if(item.getItemId()==R.id.changeradius){
			 //change radius
			 
				//pop up a dialog box
			 final Dialog dialog = new Dialog(this);
			 dialog.setContentView(R.layout.change_radius);
			 dialog.setTitle("Change Radius");	
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
					 if(!(loc.isEmpty())){
						 SEARCH_RADIUS = Integer.parseInt(loc);
						 setQuery(p);	// Update the List.
			
					 }
					 else{
			 			 Toast mtoast = Toast.makeText(MainActivity.this, "Please enter a valid Radius.", Toast.LENGTH_LONG);
			 		 	 mtoast.show();
					 }
					 dialog.dismiss();
	
	
				 }

			 });

			 dialog.show();	
		 }
		 return true;
		}
	  
}
