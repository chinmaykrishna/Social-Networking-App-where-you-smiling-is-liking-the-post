package com.parse.buzzbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.drawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends Activity {
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static int SEARCH_RADIUS=100,flag=0, Postflag=0;
	private Location lastLocation = null;
    private Location currentLocation = null;
    protected double Latitude,Longitude;
    private LocationManager locationManager;
    private static ParseGeoPoint p;
    private Context con;
    private static String Post;
	private ParseQueryAdapter<BuzzboxPost> posts;
	private SlidingMenu menu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//configure slider for comments
		config_slider();
		
		con = this;
		
		locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
	    
		currentLocation = this.getLastKnownLocation();
		
		if(currentLocation!=null) lastLocation= currentLocation;
		
		Log.d("current loc", currentLocation.toString());
		Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
		p = geoPointFromLocation(myLoc);
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
		setQuery(p);
		
	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
		
		super.onResume();
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
	            
	            else query.orderByDescending("NoOfEmpathizes");	// If the user has pressed the Featured Button.
	            
	            flag=0;
	            
	            query.whereWithinKilometers("location", pgp, SEARCH_RADIUS);
	            
	            query.setLimit(MAX_POST_SEARCH_RESULTS);
	            
	            try {
					if(query.count()==0){
						AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
						alert.setTitle("No Posts Found!!");
						alert.setMessage("Try changing Radius or search for Posts in a different Region..");
						alert.setButton(alert.BUTTON_NEUTRAL, "Exit", new DialogInterface.OnClickListener() {
									
							public void onClick(DialogInterface dialog, int which) {
								finish();

							}
						});
						alert.setButton(alert.BUTTON_POSITIVE, "Okay!", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
											
							}
						});
						alert.show();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            return query;
	          }
	        };
	        
	        
	        // Set up the query adapter
	        posts = new ParseQueryAdapter<BuzzboxPost>(this, factory) {
	          @Override
	          public View getItemView(final BuzzboxPost post, View view, ViewGroup parent) {
	            
	            view = View.inflate(getContext(), R.layout.buzzbox_post_item, null);
	            
	            TextView contentView = (TextView) view.findViewById(R.id.contentView);
	            TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
	            final TextView count = (TextView) view.findViewById(R.id.Count_of_Empathizes);
	            
	            // ImageView im = (ImageView) view.findViewById(R.id.imageView1);
	            // contentView.setBackground();  // We will do this to show the image.
	            
	            contentView.setText(post.getText());
	            count.setText(""+post.no_of_empathizes());
	            usernameView.setText(post.getUser().getUsername());
	            
	            //comment button pressed
	            final Button comment_but = (Button)view.findViewById(R.id.comment);
	            
	            comment_but.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//toggle slider
						menu.toggle();
						
						//retrieve comments
						final ListView comments_list = (ListView)menu.getMenu().findViewById(R.id.comments_list);
						retrieve_comments(post, comments_list);
					        //sending a new comment
					        
					        final EditText ed = (EditText)menu.getMenu().findViewById(R.id.edit_comments);
					        Button ok = (Button)menu.getMenu().findViewById(R.id.ok_button);
					        ok.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									if(ed.getText().toString().trim().length()<1)
									 {
										 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
									 }
									 else
									 {
										 
										 CommentsObject new_comment = new CommentsObject();
										 new_comment.toPost(post.getObjectId());
										 new_comment.setText(ed.getText().toString().trim());
										 new_comment.saveInBackground(new SaveCallback() {
											
											@Override
											public void done(ParseException e) {
												// TODO Auto-generated method stub
												if(e==null)
												{
													retrieve_comments(post, comments_list);
													Toast.makeText(con, "Comment Successful", Toast.LENGTH_SHORT).show();
												}
												else
												{
													Log.d("error while sending", e.getMessage().toString());
													Toast.makeText(con, "Sending failed. Please check internet connection", Toast.LENGTH_SHORT).show();
												}
												
											}
										});
										 ed.setText("");
									 }
								}
							});
					}
				});
	            
	            //Favorite button
	            final ImageButton bfav = (ImageButton) view.findViewById(R.id.favourite);
	            
	            if(ParseUser.getCurrentUser().getInt(post.getObjectId())==1){
	            	bfav.setImageResource(drawable.star_big_on);
	            }
	            
	            bfav.setOnClickListener(new OnClickListener(){
	            	
	            	public void onClick(View v){
	            	    	
	            		ParseUser.getCurrentUser().put(post.getObjectId(), 1);
	            		ParseUser.getCurrentUser().saveInBackground();
	            		bfav.setImageResource(drawable.star_big_on);
	            	    		            		  
	            	}
	            });
	            
	            // Empathize Button.
	            final ImageButton bemp = (ImageButton) view.findViewById(R.id.btnEmpathize);
	            
	            bemp.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!(ParseUser.getCurrentUser().getInt(post.getObjectId()+"emp")==1)){
							ParseQuery<BuzzboxPost> query = BuzzboxPost.getQuery();
							count.setText(""+(post.no_of_empathizes()+1));
	            	    	// Retrieve the object by id
	            	    	query.getInBackground(post.getObjectId(), new GetCallback<BuzzboxPost>() {
	            	    	  public void done(BuzzboxPost newquery, ParseException e) {
	            	    	    if (e == null) {
	            	    	      
	            	    	    int temp = post.no_of_empathizes()+1;  
	            	    	    newquery.put("NoOfEmpathizes",temp);
	            	    	    count.setText(""+temp);
	            	    	    newquery.saveInBackground();
	            	    	    ParseUser.getCurrentUser().put(post.getObjectId()+"emp", 1);
	            	    	    ParseUser.getCurrentUser().saveInBackground();
	            	    	   
	            	    	    }
	            	    	  }
	            	    	});
						
						}
						else{
							Toast mtoast = Toast.makeText(MainActivity.this, "This post is Already Empathized.", Toast.LENGTH_SHORT);
				 		 	 mtoast.show();
						}
					}
	            	
	            });
	            
	            //Send private message
	            final ImageButton message = (ImageButton) view.findViewById(R.id.privatemessage);
	            
	            message.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						final Dialog dialog = new Dialog(con);
						 dialog.setContentView(R.layout.new_message);
						 dialog.setTitle("New Message");
						 Button done_but = (Button) dialog.findViewById(R.id.done);
						 final EditText message = (EditText)dialog.findViewById(R.id.message);
						 
						 	//send button clicked
							 done_but.setOnClickListener(new OnClickListener() {

								 @Override
								 public void onClick(View v) {
									 //send function
									 if(message.getText().toString().trim().length()<1)
									 {
										 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
									 }
									 else
									 {
										 MessageObject new_Message = new MessageObject();
										 new_Message.toUser(post.getUser());
										 new_Message.setText(message.getText().toString().trim());
										 new_Message.setType("via_post");
										 new_Message.setViaPost(post.getText());
										 new_Message.saveInBackground(new SaveCallback() {
											
											@Override
											public void done(ParseException e) {
												// TODO Auto-generated method stub
												if(e==null)
												{
													Toast.makeText(con, "Successfully Sent", Toast.LENGTH_SHORT).show();
												}
												else
												{
													Log.d("error while sending", e.getMessage().toString());
													Toast.makeText(con, "Sending failed. Please check internet connection", Toast.LENGTH_SHORT).show();
												}
												
											}
										});
									 }
									 dialog.dismiss();
								 }
							 });
							 dialog.show();
//						
					}
	            	
	            });
	            return view;
	          }
	        };

		        setList(posts);
		        
	}
	
	// Attach the query Adapter to the View.
	public void setList(ParseQueryAdapter<BuzzboxPost> Po){
		
		ListView postsView = (ListView) this.findViewById(R.id.postsView);
        postsView.setAdapter(Po);
	}
	
	public void change_location(View v){
				 
		//pop up a dialog box
	 final Dialog dialog = new Dialog(this);
	 dialog.setContentView(R.layout.change_loc);
	 dialog.setTitle("Change Location");	
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
			 location.setInputType(InputType.TYPE_CLASS_TEXT);
			 
			 String loc = location.getText().toString();
			 if(!(loc.isEmpty())){
				 loc = loc.replace(" ", "+");
				 new FindPlace().execute(loc);
	
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
		 Button done_but = (Button) dialog.findViewById(R.id.done);
		 final EditText message = (EditText)dialog.findViewById(R.id.message);
		 final EditText locat = (EditText)dialog.findViewById(R.id.locat);
		 
		 	//done button clicked
			 done_but.setOnClickListener(new OnClickListener() {

				 @Override
				 public void onClick(View v) {
					 //post function
					 if(message.getText().toString().trim().length()<1 || locat.getText().toString().trim().length()<1)
					 {
						 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
					 }
					 else
					 {
						 Postflag=1;
						 Post = message.getText().toString();
						 String loc = (locat.getText().toString()).replace(" ","+");
						 new FindPlace().execute(loc);						 
						
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
	  
	  public void myProfile(View v){
		  Intent i = new Intent(MainActivity.this,MyProfile.class);
		  MainActivity.this.startActivity(i);
		  
		  //finish();
	  }
	  
	  public void PostBuzz(final ParseGeoPoint par){
		  	 Postflag=0;
		  	 BuzzboxPost new_post = new BuzzboxPost();
			 new_post.setUser(ParseUser.getCurrentUser());
			 
			 new_post.setText(Post);
			 new_post.set_no_of_empathizes(0);
			 new_post.Init(ParseUser.getCurrentUser().getUsername());
			 
			 new_post.setLocation(par);
			 new_post.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if(e==null)
					{
						Toast.makeText(con, "Successfully posted", Toast.LENGTH_SHORT).show();
						setQuery(par);	// Update the List.
					}
					else
					{
						Log.d("error post", e.getMessage().toString());
						Toast.makeText(con, "Posting failed. Please check internet connection"+e.toString(), Toast.LENGTH_LONG).show();
					}
					
				}
			});
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
	  
	  
	  /*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private static ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
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
						ParseGeoPoint pa = new ParseGeoPoint(Latitude,Longitude);
						add = ((JSONArray)result.get("results")).getJSONObject(0).getString("formatted_address");
			     		
						if(Postflag==0){
							currentLocation.setLatitude(Latitude);
							currentLocation.setLongitude(Longitude);
							p=geoPointFromLocation(currentLocation);
							setQuery(pa);
							add = ((JSONArray)result.get("results")).getJSONObject(0).getString("formatted_address");
							Toast mtoast = Toast.makeText(MainActivity.this,"Location set to " +add, Toast.LENGTH_LONG);
				     		mtoast.show();
						}
						else{
							PostBuzz(pa);
							Toast mtoast = Toast.makeText(MainActivity.this,"Posting through: " +add, Toast.LENGTH_SHORT);
				     		mtoast.show();
						}
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
	  @Override
		public boolean onOptionsItemSelected
									    (MenuItem item) {
		 if(item.getItemId()==R.id.refresh){
			 currentLocation= this.getLastKnownLocation(); 
			 p=geoPointFromLocation(currentLocation);
			 setQuery(p);
		 }
		 else if(item.getItemId()==R.id.MyMessages){
			  Intent i = new Intent(this,My_messages.class);
			  startActivity(i);
		 }
		 else if(item.getItemId()==R.id.changeradius){
			 //change radius
			 
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
	  private Location getLastKnownLocation() {
		    List<String> providers = locationManager.getProviders(true);
		    Location bestLocation = null;
		    for (String provider : providers) {
		        Location l = locationManager.getLastKnownLocation(provider);

		        if (l == null) {
		            continue;
		        }
		        if (bestLocation == null
		                || l.getAccuracy() < bestLocation.getAccuracy()) {
		            bestLocation = l;
		        }
		    }
		    if (bestLocation == null) {
		        return null;
		    }
		    return bestLocation;
		}
	  
	  //Function to configure slider
	  private void config_slider()
	  {
		  	//configuring slider for comments
			menu = new SlidingMenu(this);
	        menu.setMode(SlidingMenu.RIGHT);
	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	        menu.setShadowWidthRes(R.dimen.shadow_length);
	        menu.setShadowDrawable(R.drawable.shadow);
	        menu.setBehindOffsetRes(R.dimen.behind_offset);
	        menu.setFadeDegree(0.35f);
	        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
	        menu.setMenu(R.layout.slider_layout);
	  }
	  
	  @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onCustomBackPressed();
				return true;
			default:
				return super.onKeyDown(keyCode, event);
			}
		}

		// If sliding menu is showing, we need to hide it on the first back button
		// press.
		private void onCustomBackPressed() {
			if (menu != null
					&& menu.isMenuShowing()) {
				menu.toggle();
			} else {
				this.onBackPressed();
			}
		}
		public void retrieve_comments(final BuzzboxPost post, ListView comments_list)
		{
			ParseQueryAdapter.QueryFactory<CommentsObject> factory1 =
			        new ParseQueryAdapter.QueryFactory<CommentsObject>() {
			          public ParseQuery<CommentsObject> create() {
			            
			            ParseQuery<CommentsObject> query = CommentsObject.getQuery();
			            query.orderByDescending("createdAt");
			            query.whereEqualTo("topost", post.getObjectId());
			            return query;
			          }
			        };
			    ParseQueryAdapter<CommentsObject> Comments_adapter = new ParseQueryAdapter<CommentsObject>(con, factory1) {
		        	@Override
			          public View getItemView(final CommentsObject message, View view, ViewGroup parent) {
			            
			            view = View.inflate(con, R.layout.comments_element, null);
			            
			            TextView message_text = (TextView) view.findViewById(R.id.comment_text);
			            
			            message_text.setText(message.getText());
			           
			            return view;
			          }
			        };
			        comments_list.setAdapter(Comments_adapter);
		}
}
