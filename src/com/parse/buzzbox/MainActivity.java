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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class MainActivity extends Activity implements LocationListener {
	
	private static final int MAX_POST_SEARCH_RESULTS= 50;
	private static int SEARCH_RADIUS=100,flag=0, Postflag=1;
	private static Location lastLocation = null;
    private static Location currentLocation = null;
    protected double Latitude,Longitude;
    private LocationManager locationManager;
    private static ParseGeoPoint p;
    private Context con;
    private ParseQueryAdapter<BuzzboxPost> posts;
	private SlidingMenu menu, menuleft, menuright;
	private static boolean logout=false;
	private ListView post_list; 
	private SlidingUpPanelLayout comment_slider;
	private com.parse.buzzbox.HorizontalListView hori_list;
	private int height_actual;
	private Handler hm;
	private int no_of_post = 0;
	private MainActivity main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		main = this;
		if (getIntent().getBooleanExtra("EXIT", false)) {
			 finish();
			}
		setContentView(R.layout.activity_main);
		
		
		// to get actual screen size excluding paralax
		final LinearLayout layout = (LinearLayout) findViewById(R.id.main_screen);
		
		
		final ViewTreeObserver observer= layout.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(
		    new ViewTreeObserver.OnGlobalLayoutListener() {
		        @Override
		            public void onGlobalLayout() {
		                height_actual = layout.getHeight();
		            }
		        });
		
		//finish if user has been logged out
		if(ParseUser.getCurrentUser()!=null)
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
		
		
		// Configure gestures over post_list element.
		post_list = (ListView)findViewById(R.id.postsView);
		post_list.setOnTouchListener(new OnSwipeTouchListener(con){
			
			public void onSwipeRight() {
            	menuleft.toggle();
            	ImageView im = (ImageView)menuleft.getMenu().findViewById(R.id.avatar);
            	im.setImageResource(ParseUser.getCurrentUser().getInt("Avatar"));
            	TextView tv = (TextView)menuleft.getMenu().findViewById(R.id.Nick);
            	tv.setText(ParseUser.getCurrentUser().getUsername());
            	TextView tv2 = (TextView)menuleft.getMenu().findViewById(R.id.NoOfPosts);
            	tv2.setText(""+no_of_post);
            	
            	View view2 = (View)menuleft.getMenu();
            	view2.setOnTouchListener(new OnSwipeTouchListener(con){
            		public void onSwipeLeft() {
            			onCustomBackPressed();
	                }
            		
            		public void onSwipeBottom() {
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
    		@Override
    		public void onSwipeBottom() {
    			scroll_function(1);
    		}
    		@Override
    		public void onSwipeTop() {
    			scroll_function(0);
    		}
    		public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
    	});
		
		
		//comment slider configuration
		comment_slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_up);
		
		hori_list = (com.parse.buzzbox.HorizontalListView)findViewById(R.id.horizaontal_comments);
		comment_slider.setPanelSlideListener(new PanelSlideListener() {
			
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPanelExpanded(View panel) {
				int pos = post_list.getFirstVisiblePosition();
				int size = post_list.getCount();
				if(size>=1)
				{
					
						BuzzboxPost post = posts.getItem(pos);
						retrieve_comments(post, hori_list);
										
				}
			}
			
			@Override
			public void onPanelCollapsed(View panel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPanelAnchored(View panel) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//configure slider for comments
		config_slider();
		
		con = this;
		hm = new Handler() {
            public void handleMessage(Message m) {
            	Toast.makeText(con, "Can not find location. Please check your network provider or GPS.", Toast.LENGTH_LONG).show();
            	AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
	  			  alert.setTitle("GPS Settings");
	  			  alert.setMessage("GPS in not enabled. Please enable the GPS from settings to use this Application.");
	  			  alert.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
	  				  public void onClick(DialogInterface dialog , int which){
	  					  Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	  					  MainActivity.this.startActivity(i);
	  					  finish();
	  				  }
	  			  });
	  			  
	  			  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	  				  public void onClick(DialogInterface dialog , int which){
	  					  finish();
	  				  }
	  			  });
	  			  
	  			  alert.show();
            }
        };
		
		locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
	    
		currentLocation = this.getLastKnownLocation();
		
		if(currentLocation!=null)
		{
			if(currentLocation!=null) lastLocation= currentLocation;
			
			Log.d("current loc", currentLocation.toString());
			Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
			
			p = geoPointFromLocation(myLoc);
			
			setQuery(p);

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
		            
		            view = new PostItem_custom(con, height_actual);
		            
		            no_of_post = post.getNoofPosts();
		            TextView contentView = (TextView) view.findViewById(R.id.contentView);
		            TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
		            final TextView count = (TextView) view.findViewById(R.id.Count_of_Empathizes);
		            ImageView im = (ImageView) view.findViewById(R.id.imageView1);
		            TextView date = (TextView) view.findViewById(R.id.date);
		            TextView time = (TextView) view.findViewById(R.id.time);
		            
		            // ImageView im = (ImageView) view.findViewById(R.id.imageView1);
		            // contentView.setBackground();  // We will do this to show the image.
		            
		            contentView.setText(post.getText());
		            
		            contentView.setBackgroundResource(post.getInt("mood"));
		            
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
		            
		            
		            
		            usernameView.setText(post.getUser().getUsername());
		            im.setImageResource(post.getUser().getInt("Avatar"));
		            
		          //comment button pressed
		            final Button comment_but = (Button)view.findViewById(R.id.comment);
		            
		            comment_but.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						
							
							final Dialog dialog = new Dialog(con);
							 dialog.setContentView(R.layout.new_message);
							 dialog.setTitle("Comment");
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
											 CommentsObject new_comment = new CommentsObject();
											 new_comment.toPost(post.getObjectId());
											 new_comment.setText(message.getText().toString().trim());
											 new_comment.setAuthor(ParseUser.getCurrentUser());
											 new_comment.saveInBackground(new SaveCallback() {
												
												@Override
												public void done(ParseException e) {
													// TODO Auto-generated method stub
													if(e==null)
													{
														
														Toast.makeText(con, "Comment Successful", Toast.LENGTH_SHORT).show();
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
						}
					});
		            
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
								BuzzboxPost.getQuery();
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
						 message.getText().toString();
						
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
	  
	  
//	  public void PostBuzz(final ParseGeoPoint par){
//		  
//		  	// Postflag=0;
//		  	 BuzzboxPost new_post = new BuzzboxPost();
//			 new_post.setUser(ParseUser.getCurrentUser());
//			 
//			 new_post.setText(Post);
//			 new_post.set_no_of_empathizes(0);
//			 new_post.Init(ParseUser.getCurrentUser().getUsername());
//			 
//			 int temp = new_post.getNoofPosts()+1;
//			 ParseUser.getCurrentUser().put("noOfPosts",temp);
//			 ParseUser.getCurrentUser().saveInBackground();
//			 
//			 new_post.setLocation(par);
//			 final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
//			 pdLoading.setMessage("\tPosting your Buzz...\n \t Please Wait!!");
//			 new_post.saveInBackground(new SaveCallback() {
//				
//				@Override
//				public void done(ParseException e) {
//					// TODO Auto-generated method stub
//					if(e==null)
//					{
//						pdLoading.dismiss();
//						Toast.makeText(con, "Successfully posted.. Updating your List now!", Toast.LENGTH_SHORT).show();
//						setQuery(par);	// Update the List.
//					}
//					else
//					{
//						Log.d("error post", e.getMessage().toString());
//						Toast.makeText(con, "Posting failed. Please check internet connection"+e.toString(), Toast.LENGTH_LONG).show();
//					}
//					
//				}
//			});
//			 
//			 pdLoading.show();
//	  }
	  
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
			     		
						
							currentLocation.setLatitude(Latitude);
							currentLocation.setLongitude(Longitude);
							p=geoPointFromLocation(currentLocation);
							Postflag=0;
							setQuery(pa);
							add = ((JSONArray)result.get("results")).getJSONObject(0).getString("formatted_address");
							Toast mtoast = Toast.makeText(MainActivity.this,"Location set to " +add, Toast.LENGTH_LONG);
				     		mtoast.show();
						
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

		        private static final int SWIPE_THRESHOLD = 50;
		        private static final int SWIPE_VELOCITY_THRESHOLD = 50;

		        
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
			 
			 if(Postflag==0){
				 currentLocation = this.getLastKnownLocation();
				 System.out.println(currentLocation.toString());
				 p=geoPointFromLocation(currentLocation);
				 Postflag=1;
			 }
			 
			 new newPost(p);
			 Intent intent = new Intent(MainActivity.this, newPost.class);
			 MainActivity.this.startActivity(intent);
			 
			 setQuery(p);
			 
//			 final Dialog dialog = new Dialog(this);
//			 dialog.setContentView(R.layout.new_post);
//			 dialog.setTitle("New Post");
//			 Button done_but = (Button) dialog.findViewById(R.id.done);
//			 
//			 

//			 
//			 final EditText message = (EditText)dialog.findViewById(R.id.message);
//			 //final EditText locat = (EditText)dialog.findViewById(R.id.locat);
//			 
//			 	//done button clicked
//				 done_but.setOnClickListener(new OnClickListener() {
//
//					 @Override
//					 public void onClick(View v) {
//						 //post function
//						 if(message.getText().toString().trim().length()<1)
//						 {
//							 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
//						 }
//						 else
//						 {
//							 //Postflag=1;
////							 Post = message.getText().toString();
////							 PostBuzz(p);
//							 dialog.dismiss();
//							 AlertDialog.Builder builder = new AlertDialog.Builder(con);
//						        TextView title = new TextView(con);
//						        title.setText("Select your mood:");
//						        title.setPadding(10, 10, 10, 10);
//						        title.setGravity(Gravity.CENTER);
//						        title.setTextColor(Color.rgb(0, 153, 204));
//						        title.setTextSize(23);
//						        builder.setCustomTitle(title);
//						        
//						        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//						        View layout_spinners = inflater.inflate(R.layout.spinner_layout,null);
//						        builder.setView(layout_spinners);
//						        builder.setCancelable(false);
//						        builder.show();
//						        
//						         Spinner moods = (Spinner) dialog.findViewById(R.id.moods);
//								 final ArrayList<String> listmoods = new ArrayList<String>();
//								 listmoods.add("No Feelings!");
//								 listmoods.add("Happy");
//								 listmoods.add("Sad");
//								 
//								 ArrayAdapter<String> aa = new ArrayAdapter<String>(con,   android.R.layout.simple_spinner_item, listmoods);
//								 aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
//								 moods.setAdapter(aa);
//								 
//								 moods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//								        @Override
//								        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//								        	
//								        	TextView finalmood = (TextView) dialog.findViewById(R.id.finalmood);
//								        	//finalmood.setText(listmoods.get(position));
//								        	
//								        }
//
//								        @Override
//								        public void onNothingSelected(AdapterView<?> parent) {
//
//								        }
//								    });
//							 
//							 //String loc = (locat.getText().toString()).replace(" ","+");
//							 //new FindPlace().execute(loc);						 
//							
//						 }
//						 dialog.dismiss();
//					 }
//				 });
//
//				 //Choose background button clicked
////				 choose_bg.setOnClickListener(new OnClickListener() {
////					 @Override
////					 public void onClick(View v) {
////						 //choose_bg function
////						 Intent i = new Intent(con,Choose_bg.class);
////						 startActivity(i);
////					 }
//	//
////				 });
//
//				 dialog.show();	
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
		  
//		  locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
//		  boolean netwrkenabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
//		  boolean gpsenabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
//		  
//		  if(!gpsenabled){
//			  System.out.println("=======> GPS not enabled me aya");
//			  AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
//			  alert.setTitle("GPS Settings");
//			  alert.setMessage("GPS in not enabled. Please enable the GPS from settings to use this Application.");
//			  alert.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//				  public void onClick(DialogInterface dialog , int which){
//					  Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//					  MainActivity.this.startActivity(i);
//					  finish();
//				  }
//			  });
//			  
//			  alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				  public void onClick(DialogInterface dialog , int which){
//					  finish();
//				  }
//			  });
//			  
//			  alert.show();
//		  
//			  
//		  }
//		  
//		  if(netwrkenabled){
//			  locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 0, 0, this);
//			  if(locationManager != null){
//				  currentLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
//				  System.out.println("Network me aya" + currentLocation.getLatitude());
//			  }
//			  
//		  }
//		  else if(gpsenabled){
//			  locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
//			  if(locationManager != null){
//				  currentLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//				  System.out.println("Gps me aya"+currentLocation.getLatitude());
//			  }
//		  }
//		  
//		  return currentLocation;
		  
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
		    	    		main.returnHandler().sendEmptyMessage(0);
		    	    		//finish();
		    	    		
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
		public void retrieve_comments(final BuzzboxPost post, com.parse.buzzbox.HorizontalListView comments_list)
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
			            
			            TextView date = (TextView) view.findViewById(R.id.date);
			            TextView time = (TextView) view.findViewById(R.id.time);
			            final ImageView im = (ImageView) view.findViewById(R.id.avatar);
			            
			            
			            message.getAuthor().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
		                    public void done(ParseUser object, ParseException e) {
		                         if(e==null)
		                         {
		                        	 im.setImageResource(object.getInt("Avatar"));
		                         }
		                         else
		                         {
		                        	 //im.setImageResource(R.drawable.error);
		                         }
		                      }
		                  });
			            
			            final TextView username = (TextView) view.findViewById(R.id.username);
			            TextView message_text = (TextView) view.findViewById(R.id.comment_text);
			            Date dtime = message.getCreatedAt();
			            Date ddate = dtime;
			            SimpleDateFormat dateFormattime = new SimpleDateFormat("hh:mm");
			            SimpleDateFormat dateFormatdate = new SimpleDateFormat("dd-MM-yyyy");
			            dateFormattime.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
			            dateFormatdate.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
			            String timeString = dateFormattime.format(dtime);
			            String dateString = dateFormatdate.format(ddate);
			            		            
			            date.setText(dateString);
			            time.setText(timeString);
			            
			            
		            	message.getAuthor().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
		                    public void done(ParseUser object, ParseException e) {
		                         if(e==null)
		                         {
		                        	 username.setText(object.getUsername());
		                        	 username.setGravity(Gravity.CENTER_HORIZONTAL);
		                         }
		                         else
		                         {
		                        	 username.setText("");
		                        	 
		                         }
		                      }
		                  });
						
			            message_text.setText(message.getText());
			           
			            return view;
			          }
			        };
			        comments_list.setAdapter(Comments_adapter);
		}
		
		public Handler returnHandler(){
	        return hm;
	    }




		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}




		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}




		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}




		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		// scroll list on gesture
		//1 = up scroll
		//0 = down scroll
		public void scroll_function(int up_or_down)
		{
			if(up_or_down==1)
			{
				Log.d("blag", "down");
					post_list.smoothScrollToPosition(post_list.getLastVisiblePosition()-1);
				
			}
			else
			{
				Log.d("blag", "up");
					post_list.smoothScrollToPosition(post_list.getFirstVisiblePosition()+1);
			}
		}
		
		
		
}
