package com.parse.buzzbox;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.parse.buzzbox.FetchLocation.LocationResult;

public class MainActivity extends Activity {
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static int SEARCH_RADIUS=100,flag=0, Postflag=0, index=0;
	private Location lastLocation = null;
    private Location currentLocation = null;
    protected double Latitude,Longitude;
    private LocationManager locationManager;
    private static ParseGeoPoint p;
    private Context con;
    private static String Post;
	private ParseQueryAdapter<BuzzboxPost> posts;
	private SlidingMenu menu, menuleft, menuright , menuBottom;
	private static boolean logout=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			 finish();
			}
		setContentView(R.layout.activity_main);
		if(ParseUser.getCurrentUser()!=null)
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
		
		//configure slider for comments
		config_slider();
		
		con = this;
		
		locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
	    
		currentLocation = this.getLastKnownLocation();
		
		if(currentLocation!=null)
		{
			if(currentLocation!=null) lastLocation= currentLocation;
			
			Log.d("current loc", currentLocation.toString());
			Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
			
			p = geoPointFromLocation(myLoc);
			
			setQuery(p);
//			ParseQueryAdapter.QueryFactory<BuzzboxPost> factory =
//			        new ParseQueryAdapter.QueryFactory<BuzzboxPost>() {
//			          public ParseQuery<BuzzboxPost> create() {
//			            
//			            ParseQuery<BuzzboxPost> query = BuzzboxPost.getQuery();
//			            query.include("user");
//			            
//			            if(flag==0) query.orderByDescending("createdAt");	// If the user has not pressed the Featured Button.
//			            
//			            else query.orderByDescending("NoOfEmpathizes");	// If the user has pressed the Featured Button.
//			            
//			            flag=0;
//			            
//			            query.whereWithinKilometers("location", p, SEARCH_RADIUS);
//			            
//			            query.setLimit(MAX_POST_SEARCH_RESULTS);
//			            
//			            return query;
//			          }
//			        };
//			
//			        setContentView(R.layout.buzzbox_post_item);
//			        posts = new ParseQueryAdapter<BuzzboxPost>(this, factory);
//			        final BuzzboxPost currentPost = posts.getItem(index);
//			        ImageView im = (ImageView) findViewById(R.id.imageView1);
//			        im.setImageResource(currentPost.getUser().getInt("Avatar"));
//			        TextView username = (TextView) findViewById(R.id.usernameView);
//			        username.setText(currentPost.getText());
//			        
//			        View view = View.inflate(con, R.layout.buzzbox_post_item, null);
//			        view.setOnTouchListener(new OnSwipeTouchListener(con){
//			        	
//			        	public void onSwipeTop() {
//			        		
//			        		index++;
//			        		
//		                    Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
//		                }
//		                public void onSwipeRight() {
//		                	menuleft.toggle();
//		                	ImageView im = (ImageView)menuleft.getMenu().findViewById(R.id.avatar);
//		                	im.setImageResource(ParseUser.getCurrentUser().getInt("Avatar"));
//		                	TextView tv = (TextView)menuleft.getMenu().findViewById(R.id.Nick);
//		                	tv.setText(ParseUser.getCurrentUser().getUsername());
//		                	TextView tv2 = (TextView)menuleft.getMenu().findViewById(R.id.NoOfPosts);
//		                	tv2.setText(""+currentPost.getNoofPosts());
//		                	
//		                	View view2 = (View)menuleft.getMenu();
//		                	view2.setOnTouchListener(new OnSwipeTouchListener(con){
//		                		public void onSwipeLeft() {
//		                			onCustomBackPressed();
//		    	                    //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
//		    	                }
//		                		
//		                		public void onSwipeBottom() {
//		    	                    //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//		    	                }
//		                		
//		                		public boolean onTouch(View v, MotionEvent event) {
//		        	                return gestureDetector.onTouchEvent(event);
//		        	            }
//		                	});
//		                    //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
//		                }
//		                public void onSwipeLeft() {
//		                	
//		                    Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
//		                }
//		                public void onSwipeBottom() {
//		                    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
//		                }
//
//		            public boolean onTouch(View v, MotionEvent event) {
//		                return gestureDetector.onTouchEvent(event);
//		            }
//			        });
		}
				
	}
	
	
	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
		
		if(ParseUser.getCurrentUser()!=null)
			if(!(ParseUser.getCurrentUser().isDataAvailable()))
				finish();
		
	}



	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		if(ParseUser.getCurrentUser()!=null)
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
		            
		            return query;
		          }
		        };
		        
		        
		        
		        // Set up the query adapter
		        posts = new ParseQueryAdapter<BuzzboxPost>(this, factory) {
		          @SuppressWarnings("deprecation")
				@Override
		          public View getItemView(final BuzzboxPost post, View view, ViewGroup parent) {
		            
		            view = View.inflate(getContext(), R.layout.buzzbox_post_item, null);
		            
		            TextView contentView = (TextView) view.findViewById(R.id.contentView);
		            TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
		            final TextView count = (TextView) view.findViewById(R.id.Count_of_Empathizes);
		            ImageView im = (ImageView) view.findViewById(R.id.imageView1);
		            TextView date = (TextView) view.findViewById(R.id.date);
		            TextView time = (TextView) view.findViewById(R.id.time);
		            
		            // ImageView im = (ImageView) view.findViewById(R.id.imageView1);
		            // contentView.setBackground();  // We will do this to show the image.
		            
		            contentView.setText(post.getText());
		            Date dtime = post.getCreatedAt();
		            Date ddate = dtime;
		            SimpleDateFormat dateFormattime = new SimpleDateFormat("hh:mm");
		            SimpleDateFormat dateFormatdate = new SimpleDateFormat("dd-MM-yyyy");
		            dateFormattime.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		            dateFormatdate.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		            String timeString = dateFormattime.format(dtime);
		            String dateString = dateFormatdate.format(ddate);
		            		            
		            date.setText(dateString);
		            time.setText(timeString);
		            view.setOnTouchListener(new OnSwipeTouchListener(con){
		            	
		            	public void onSwipeTop() {
		                    Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
		                }
		                public void onSwipeRight() {
		                	menuleft.toggle();
		                	ImageView im = (ImageView)menuleft.getMenu().findViewById(R.id.avatar);
		                	im.setImageResource(ParseUser.getCurrentUser().getInt("Avatar"));
		                	TextView tv = (TextView)menuleft.getMenu().findViewById(R.id.Nick);
		                	tv.setText(ParseUser.getCurrentUser().getUsername());
		                	TextView tv2 = (TextView)menuleft.getMenu().findViewById(R.id.NoOfPosts);
		                	tv2.setText(""+post.getNoofPosts());
		                	
		                	View view2 = (View)menuleft.getMenu();
		                	view2.setOnTouchListener(new OnSwipeTouchListener(con){
		                		public void onSwipeLeft() {
		                			onCustomBackPressed();
		    	                    //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
		    	                }
		                		
		                		public void onSwipeBottom() {
		    	                    //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
		    	                }
		                		
		                		public boolean onTouch(View v, MotionEvent event) {
		        	                return gestureDetector.onTouchEvent(event);
		        	            }
		                	});
		                    //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
		                }
		                public void onSwipeLeft() {
		                	
		                    menuright.toggle();
		                    //ParseQueryAdapter<MessageObject> Messages;
		                	ListView list = (ListView)menuright.getMenu().findViewById(R.id.messages);
		                 // Set up a customized query
		        		    ParseQueryAdapter.QueryFactory<MessageObject> factory =
		        		        new ParseQueryAdapter.QueryFactory<MessageObject>() {
		        		          public ParseQuery<MessageObject> create() {
		        		            
		        		            ParseQuery<MessageObject> query = MessageObject.getQuery();
		        		            query.orderByDescending("createdAt");
		        		            query.whereEqualTo("toobjectid", ParseUser.getCurrentUser().getObjectId());
		        		            return query;
		        		          }
		        		        };
		        		   
		        		        // Set up the query adapter
		        		        ParseQueryAdapter<MessageObject> Messages = new ParseQueryAdapter<MessageObject>(con, factory) {
		        		        	@Override
		        		          public View getItemView(final MessageObject message, View view, ViewGroup parent) {
		        		            
		        		            view = View.inflate(getContext(), R.layout.my_messages_element, null);
		        		            
		        		            TextView message_text = (TextView) view.findViewById(R.id.message);
		        		            TextView via_post = (TextView) view.findViewById(R.id.via_post);
		        		            
		        		            message_text.setText(message.getText());
		        		            if(message.getType().equals("via_post"))
		        		            via_post.setText("Via Post: "+message.getViaPost());
		        		            return view;
		        		          }
		        		        };
		        			    list.setAdapter(Messages);
		        			    
		        			    View view2 = (View)menuright.getMenu();
			                	view2.setOnTouchListener(new OnSwipeTouchListener(con){
			                		public void onSwipeRight() {
			                			onCustomBackPressed();
			    	                    //Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
			    	                }
			                		
			                		public boolean onTouch(View v, MotionEvent event) {
			        	                return gestureDetector.onTouchEvent(event);
			        	            }
			                	});
		        			    
		                	//Toast.makeText(MainActivity.this, "left", Toast.LENGTH_SHORT).show();
		                }
		                public void onSwipeBottom() {
		                    Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
		                }

		            public boolean onTouch(View v, MotionEvent event) {
		                return gestureDetector.onTouchEvent(event);
		            }
		            });
		            
		            
		            usernameView.setText(post.getUser().getUsername());
		            im.setImageResource(post.getUser().getInt("Avatar"));
		            
		            //comment button pressed
		            final Button comment_but = (Button)view.findViewById(R.id.comment);
		            
//		            comment_but.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							//toggle slider
//							menu.toggle();
//							
//							//retrieve comments
//							final ListView comments_list = (ListView)menu.getMenu().findViewById(R.id.comments_list);
//							retrieve_comments(post, comments_list);
//						        //sending a new comment
//						        
//						        final EditText ed = (EditText)menu.getMenu().findViewById(R.id.edit_comments);
//						        Button ok = (Button)menu.getMenu().findViewById(R.id.ok_button);
//						        ok.setOnClickListener(new OnClickListener() {
//									
//									@Override
//									public void onClick(View v) {
//										if(ed.getText().toString().trim().length()<1)
//										 {
//											 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
//										 }
//										 else
//										 {
//											 
//											 CommentsObject new_comment = new CommentsObject();
//											 new_comment.toPost(post.getObjectId());
//											 new_comment.setText(ed.getText().toString().trim());
//											 new_comment.saveInBackground(new SaveCallback() {
//												
//												@Override
//												public void done(ParseException e) {
//													// TODO Auto-generated method stub
//													if(e==null)
//													{
//														retrieve_comments(post, comments_list);
//														Toast.makeText(con, "Comment Successful", Toast.LENGTH_SHORT).show();
//													}
//													else
//													{
//														Log.d("error while sending", e.getMessage().toString());
//														Toast.makeText(con, "Sending failed. Please check internet connection", Toast.LENGTH_SHORT).show();
//													}
//													
//												}
//											});
//											 ed.setText("");
//										 }
//									}
//								});
//						}
//					});
		            
		            //Favorite button
		            final ImageButton bfav = (ImageButton) view.findViewById(R.id.favourite);
		            
		            if(ParseUser.getCurrentUser().getInt(post.getObjectId())==1){
		            	bfav.setImageResource(drawable.star_big_on);
		            }
		            
		            bfav.setOnClickListener(new OnClickListener(){
		            	
		            	public void onClick(View v){
		            	    
		            		if(ParseUser.getCurrentUser().getInt(post.getObjectId())==1){
		            			ParseUser.getCurrentUser().put(post.getObjectId(), 0);
			            		ParseUser.getCurrentUser().saveInBackground();
		            			bfav.setImageResource(drawable.star_big_off);
				            }
		            		else{
			            		ParseUser.getCurrentUser().put(post.getObjectId(), 1);
			            		ParseUser.getCurrentUser().saveInBackground();
			            		bfav.setImageResource(drawable.star_big_on);
		            		}
		            	    		            		  
		            	}
		            });
		            
		            // Empathize Button.
		            final ImageButton bemp = (ImageButton) view.findViewById(R.id.btnEmpathize);
		            count.setText(""+post.no_of_empathizes());
		            bemp.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(!(ParseUser.getCurrentUser().getInt(post.getObjectId()+"emp")==1)){
								ParseQuery<BuzzboxPost> query = BuzzboxPost.getQuery();
								count.setText(""+(post.no_of_empathizes()+1));
								
								post.put("NoOfEmpathizes",(post.no_of_empathizes()+1));
								post.saveInBackground();
		            	    	
		            	    	ParseUser.getCurrentUser().put(post.getObjectId()+"emp", 1);
	            	    	    ParseUser.getCurrentUser().saveInBackground();
							
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
											 new_Message.toUserObjectID(post.getUser().getObjectId());
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
		 //final EditText locat = (EditText)dialog.findViewById(R.id.locat);
		 
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
						 //Postflag=1;
						 Post = message.getText().toString();
						 PostBuzz(p);
						 //String loc = (locat.getText().toString()).replace(" ","+");
						 //new FindPlace().execute(loc);						 
						
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
		  
		  MyProfile my = new MyProfile(this);
		  Intent i = new Intent(this,my.getClass());
		  startActivity(i);
		  if(logout){
			  finish();
		  }
	  }
	  
	  
	  public void PostBuzz(final ParseGeoPoint par){
		  
		  	// Postflag=0;
		  	 BuzzboxPost new_post = new BuzzboxPost();
			 new_post.setUser(ParseUser.getCurrentUser());
			 
			 new_post.setText(Post);
			 new_post.set_no_of_empathizes(0);
			 new_post.Init(ParseUser.getCurrentUser().getUsername());
			 
			 int temp = new_post.getNoofPosts()+1;
			 ParseUser.getCurrentUser().put("noOfPosts",temp);
			 ParseUser.getCurrentUser().saveInBackground();
			 
			 new_post.setLocation(par);
			 final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
			 pdLoading.setMessage("\tPosting your Buzz...\n \t Please Wait!!");
			 new_post.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if(e==null)
					{
						pdLoading.dismiss();
						Toast.makeText(con, "Successfully posted.. Updating your List now!", Toast.LENGTH_SHORT).show();
						setQuery(par);	// Update the List.
					}
					else
					{
						Log.d("error post", e.getMessage().toString());
						Toast.makeText(con, "Posting failed. Please check internet connection"+e.toString(), Toast.LENGTH_LONG).show();
					}
					
				}
			});
			 
			 pdLoading.show();
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
//						else{
//							PostBuzz(pa);
//							Toast mtoast = Toast.makeText(MainActivity.this,"Posting through: " +add, Toast.LENGTH_SHORT);
//				     		mtoast.show();
//						}
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
	  
	  
	  // This class will detect user's gestures of swiping left/Right.
	  public class OnSwipeTouchListener implements OnTouchListener {

		    protected final GestureDetector gestureDetector;

		    public OnSwipeTouchListener (Context ctx){
		        gestureDetector = new GestureDetector(ctx, new GestureListener());
		    }

		    private final class GestureListener extends SimpleOnGestureListener {

		        private static final int SWIPE_THRESHOLD = 100;
		        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		        @Override
		        public boolean onDown(MotionEvent e) {
		            return true;
		        }

		        @Override
		        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		            boolean result = false;
		            try {
		                float diffY = e2.getY() - e1.getY();
		                float diffX = e2.getX() - e1.getX();
		                if (Math.abs(diffX) > Math.abs(diffY)) {
		                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
		                        if (diffX > 0) {
		                            onSwipeRight();
		                        } else {
		                            onSwipeLeft();
		                        }
		                    }
		                } else {
		                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
		                        if (diffY > 0) {
		                            onSwipeBottom();
		                        } else {
		                            onSwipeTop();
		                        }
		                    }
		                }
		            } catch (Exception exception) {
		                exception.printStackTrace();
		            }
		            return result;
		        }
		    }

		    public void onSwipeRight() {
		    }

		    public void onSwipeLeft() {
		    }

		    public void onSwipeTop() {
		    }

		    public void onSwipeBottom() {
		    }

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
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
		 else if(item.getItemId()==R.id.private_message){
			  Intent i = new Intent(this,Private_message.class);
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
		 else if(item.getItemId()==R.id.exclusive){
			 
			 flag=1;
			 setQuery(p);
		 }
		 
		 else if(item.getItemId()==R.id.search){
			 
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
		 
		 else if(item.getItemId()==R.id.post){
			 
			 final Dialog dialog = new Dialog(this);
			 dialog.setContentView(R.layout.new_post);
			 dialog.setTitle("New Post");
			 Button done_but = (Button) dialog.findViewById(R.id.done);
			 currentLocation = this.getLastKnownLocation();
			 System.out.println(currentLocation.toString());
			 p=geoPointFromLocation(currentLocation);
			 final EditText message = (EditText)dialog.findViewById(R.id.message);
			 //final EditText locat = (EditText)dialog.findViewById(R.id.locat);
			 
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
							 //Postflag=1;
							 Post = message.getText().toString();
							 PostBuzz(p);
							 //String loc = (locat.getText().toString()).replace(" ","+");
							 //new FindPlace().execute(loc);						 
							
						 }
						 dialog.dismiss();
					 }
				 });

				 //Choose background button clicked
//				 choose_bg.setOnClickListener(new OnClickListener() {
//					 @Override
//					 public void onClick(View v) {
//						 //choose_bg function
//						 Intent i = new Intent(con,Choose_bg.class);
//						 startActivity(i);
//					 }
	//
//				 });

				 dialog.show();	
		 }
		 
		 else if(item.getItemId()==R.id.profile){
			 
			 MyProfile my = new MyProfile(this);
			  Intent i = new Intent(this,my.getClass());
			  startActivity(i);
			  if(logout){
				  finish();
			  }
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
		    
		    if(bestLocation==null)
		    {
		    	final ProgressDialog pdLoading = new ProgressDialog(con);
				 pdLoading.setMessage("Please wait while retreiving location...");
			     pdLoading.show();
			     pdLoading.setCancelable(false);
		    	LocationResult locationResult = new LocationResult(){
		    	    @Override
		    	    public void gotLocation(Location location){
		    	    	
		    	    	pdLoading.dismiss();
		    	    	if(location==null)
		    	    	{
		    	    		Toast.makeText(con, "Failed to retreive location. Please check network connections.", Toast.LENGTH_SHORT).show();
		    	    		finish();
		    	    		
		    	    	}
		    	    	else
		    	    	{
		    	    		//got location :)
			    			p = geoPointFromLocation(location);
			    			setQuery(p);
		    	    	}
		    	    	
		    	    }
		    	};
		    	
		    	
		    	
		    	FetchLocation myLocation = new FetchLocation();
		    	myLocation.getLocation(this, locationResult);
		    	myLocation.execute();
		    }	
		    return bestLocation;
		}
	  
	  //Function to configure slider
	  private void config_slider()
	  {
//		  	//configuring slider for comments
//			menu = new SlidingMenu(this);
//	        menu.setMode(SlidingMenu.RIGHT);
//	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//	        menu.setShadowWidthRes(R.dimen.shadow_length);
//	        menu.setShadowDrawable(R.drawable.shadow);
//	        menu.setBehindOffsetRes(R.dimen.behind_offset);
//	        menu.setFadeDegree(0.35f);
//	        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
//	        menu.setMenu(R.layout.slider_layout);
	        
	        menuleft = new SlidingMenu(this);
	        menuleft.setMode(SlidingMenu.LEFT);
	        menuleft.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	        menuleft.setShadowWidthRes(R.dimen.shadow_length);
	        menuleft.setShadowDrawable(R.drawable.shadow);
	        menuleft.setBehindOffsetRes(R.dimen.behind_offset);
	        menuleft.setFadeDegree(0.35f);
	        menuleft.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
	        menuleft.setMenu(R.layout.profile);
	        
	        menuright = new SlidingMenu(this);
	        menuright.setMode(SlidingMenu.RIGHT);
	        menuright.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
	        menuright.setShadowWidthRes(R.dimen.shadow_length);
	        menuright.setShadowDrawable(R.drawable.shadow);
	        menuright.setBehindOffsetRes(R.dimen.behind_offset);
	        menuright.setFadeDegree(0.35f);
	        menuright.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
	        menuright.setMenu(R.layout.my_messages);
		    
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
			} else if (menuleft != null
					&& menuleft.isMenuShowing()) {
				menuleft.toggle();
			}
			else if (menuright != null
					&& menuright.isMenuShowing()) {
				menuright.toggle();
			}
			 else {
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
