package com.parse.buzzbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.manuelpeinado.refreshactionitem.ProgressIndicatorType;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.buzzbox.FetchLocation.LocationResult;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends SherlockActivity implements LocationListener,RefreshActionListener {
	
	//radius of nearby posts default 10km.
	private static int SEARCH_RADIUS=10,Postflag=1;
	//self descriptors variables
	private static Location lastLocation = null;
    private static Location currentLocation = null;
    protected double Latitude,Longitude;
    private LocationManager locationManager;
    private static ParseGeoPoint p;
    private Context con;
    
    //this adapter will load all posts
    private ParseQueryAdapter<BuzzboxPost> posts;
    
    //left n right sliding menu
	private SlidingMenu  menuleft, menuright;
	
	// friends loaded from db
	private String names[], numbers[], object_ids[];
	private static boolean logout=false;
	
	//fancy effect listview
	private JazzyListView post_list;
	
	//sliding up panel for showing comments
	private SlidingUpPanelLayout comment_slider;
	
	//horizontal listview
	private com.parse.buzzbox.HorizontalListView hori_list;
	
	//actual height of post listview above comments part
	private int height_actual;
	
	//handler to pass the message if location can not be found
	private Handler hm;
	
	//self descriptor
	private int no_of_post = 0;
	
	private int temp =0;
	
	//this activity
	private MainActivity main;
	
	//the action bar item used to refresh posts list
	private RefreshActionItem mRefreshActionItem;
	
	//comments progress bar to show comments are loading
	private ProgressBar comments_loader;
	
	//variables that will keep information of user touch/fling in my message layout to toggle it
	int x,y,x1,y1;
	
	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getIntent().getBooleanExtra("EXIT", false)) {
			 finish();
			}
		setContentView(R.layout.activity_main);
		main = this;
		con = this;
		// to get actual screen size excluding paralax
		final LinearLayout layout = (LinearLayout) findViewById(R.id.main_screen);
		final JazzyListView ja = (JazzyListView)findViewById(R.id.postsView);
		
		//calculate actual distance above comments layout
		final ViewTreeObserver observer= layout.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(
		    new ViewTreeObserver.OnGlobalLayoutListener() {
		        @Override
		            public void onGlobalLayout() {
		        		if(temp==0)
		                {
		        			height_actual = layout.getHeight();
		        			temp++;
		        			SlidingUpPanelLayout up = (SlidingUpPanelLayout)findViewById(R.id.sliding_up);
		        			up.setOverlayed(true);
		        			ja.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height_actual));
		                }

		            }
		        });
		
				 
		// need to fetch parse user to load the friends and friends of friends.
		if(ParseUser.getCurrentUser()!=null)
		ParseUser.getCurrentUser().fetchInBackground(null);

		
		//retrieving friends contacts from local db
				Contacts_database db = new Contacts_database(con);
				List<Contact> already_present = db.getAllContacts();
				
				names = new String[already_present.size()]; 
				numbers = new String[already_present.size()];
				object_ids = new String[already_present.size()];
				
				for(int i=0;i<already_present.size();i++)
				{
					names[i] = already_present.get(i).getName();
					numbers[i] = already_present.get(i).getPhoneNumber();
					object_ids[i] = already_present.get(i).getObjectID();
				}
				
		//finish if user has been logged out
		if(ParseUser.getCurrentUser()!=null)
		if(!(ParseUser.getCurrentUser().isDataAvailable()))
			finish();
		
		
		// Configure gestures over post_list element.
		post_list = (JazzyListView)findViewById(R.id.postsView);
		post_list.setOnTouchListener(new OnSwipeTouchListener(con){
			
			
			//opens my profile
			public void onSwipeRight() {
            	menuleft.toggle();
            	ImageView im = (ImageView)menuleft.getMenu().findViewById(R.id.avatar);
            	im.setImageResource(ParseUser.getCurrentUser().getInt("Avatar"));
            	
            	TextView tv = (TextView)menuleft.getMenu().findViewById(R.id.Nick);
            	tv.setText(ParseUser.getCurrentUser().getUsername());
            	TextView tv2 = (TextView)menuleft.getMenu().findViewById(R.id.NoOfPosts);
            	tv2.setText(""+no_of_post);
            	
            	im.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						MyProfile my = new MyProfile(main);
						  Intent i = new Intent(con,my.getClass());
						  startActivity(i);
						  if(logout){
							  finish();
						  }
					}
				});
            	
            	//to toggle it
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
			
			
			//to toggle my messages
			public void onSwipeLeft() {
            	
                menuright.toggle();
            	ListView list = (ListView)menuright.getMenu().findViewById(R.id.messages);
            	
            	//to toggle when user flings over empty part of list
            	list.setOnTouchListener(new OnSwipeTouchListener(con)
            	{
            		@Override
            		public void onSwipeRight() {
            			menuright.toggle();
            		}
            		public boolean onTouch(View v, MotionEvent event) {
                        return gestureDetector.onTouchEvent(event);
                    }
            	});
            	
            	//to show progress of message loading
            	final ProgressBar pb = (ProgressBar) menuright.getMenu().findViewById(R.id.progressBar1);
            	
            	// Set up a customized query
    		    ParseQueryAdapter.QueryFactory<MessageObject> factory =
    		        new ParseQueryAdapter.QueryFactory<MessageObject>() {
    		          public ParseQuery<MessageObject> create() {
    		        	  
    		        	  //loading messages to user and from user
    		        	  pb.setVisibility(View.VISIBLE);
    		        	  ParseQuery<MessageObject> query = MessageObject.getQuery();
    		        	  query.whereEqualTo("receipent", ParseUser.getCurrentUser().getObjectId());
    		        	   
    		        	  ParseQuery<MessageObject> query2 = MessageObject.getQuery();
    		        	  query2.whereEqualTo("user", ParseUser.getCurrentUser());
    		        	   
    		        	  List<ParseQuery<MessageObject>> queries = new ArrayList<ParseQuery<MessageObject>>();
    		        	  queries.add(query);
    		        	  queries.add(query2);
    		        	   
    		        	  ParseQuery<MessageObject> mainQuery = ParseQuery.or(queries);
    		        	  
    		        	  mainQuery.orderByDescending("createdAt");
    		            return mainQuery;
    		            
    		          }
    		        };
    		        
    		   
    		        // Set up the query adapter
    		        ParseQueryAdapter<MessageObject> Messages = new ParseQueryAdapter<MessageObject>(con, factory) {
    		        	@Override
    		          public View getItemView(final MessageObject message, View view, ViewGroup parent) {
    		            
    		        	//set up the item views
    		            view = View.inflate(getContext(), R.layout.my_messages_element, null);
    		            TextView message_text = (TextView) view.findViewById(R.id.message_text);
    		            final TextView message_author = (TextView) view.findViewById(R.id.author);
    		            TextView via_post = (TextView) view.findViewById(R.id.via_post);
    		            final ImageView author_avatar = (ImageView)view.findViewById(R.id.author_avatar);
    		            final ImageView thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
    		            thumbnail.setImageResource(message.getInt("mood"));
    		            message_text.setText(message.getText());
    		            
    		            if(message.getAuthorName()!=null)
    		            {
    		            	message_author.setText(message.getAuthorName());
    		            }
    		            if(message.getAuthorAvatar()!=null)
    		            {
    		            	author_avatar.setImageResource(Integer.parseInt(message.getAuthorAvatar()));
    		            }
    		            
    		            //viapost is set to "" if it is private message
    		            if(message.getViaPost().equals(""))
    		            {
    		            	via_post.setText("Private message");
    		            	//private message
    		            	via_post.setText("Private message");
    		            	if(ParseUser.getCurrentUser().getObjectId().equals(message.getReceipentObjID()))
    		            	{
    		            		message_author.setText("to you");
    		            	}
    		            	else
    		            	{
    		            		message_author.setText("from you");
    		            	}
    		            }
    		            else
    		            {
    		            	
    		            	//via post message
    		            	via_post.setText("Via Post: "+message.getViaPost());
    		            	
    		            	if(message.getViaPostReceipentName()!=null)
    		            	{
    		            		Log.d("asdasd", message.getViaPostReceipentName());
    		            		if(message.getReceipentObjID().equals(ParseUser.getCurrentUser().getObjectId()))
    		            		message_author.setText("from "+ message.getAuthorName() +" to you");
    		            		else
    		            		message_author.setText("from you to"+ message.getViaPostReceipentName());
    		            	}
    		            }
    		            
    		            //we are loading author after message; not with message because it will make loading query fast smooth
    		            //to refresh message author in case if he has changed the nick or avatar
    		            message.getAuthor().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
    		            	  public void done(ParseUser object, ParseException e) {
    		            		    if (e == null) {
    		            		    	message_author.setText(object.getUsername());
    		            		    	author_avatar.setImageResource(object.getInt("Avatar"));
    		            		    	
    		            		    	
    		            		    	// to set again all the view where message author information was used
    		            		    	if(message.getViaPost().equals(""))
    		        		            {
    		        		            	//private message
    		        		            	if(ParseUser.getCurrentUser().getObjectId().equals(message.getReceipentObjID()))
    		        		            	{
    		        		            		message_author.setText("to you");
    		        		            	}
    		        		            	else
    		        		            	{
    		        		            		message_author.setText("from you");
    		        		            	}
    		        		            }
    		        		            else
    		        		            {
    		        		            	
    		        		            	
    		        		            	if(message.getViaPostReceipentName()!=null)
    		        		            	{
    		        		            		Log.d("asdasd", message.getViaPostReceipentName());
    		        		            		if(message.getReceipentObjID().equals(ParseUser.getCurrentUser().getObjectId()))
    		        		            		message_author.setText("from "+ object.getUsername() +" to you");
    		        		            		else
    		        		            		message_author.setText("from you to"+ message.getViaPostReceipentName());
    		        		            	}
    		        		            }
    		            		    } else {
    		            		    	
    		            		      // Failure!
    		            		    }
    		            		  }
    		            		});
    		            
    		            //function to detect fling over item and toggle menuRight or to open corresponding messag\e 
    		            view.setOnTouchListener(new OnSwipeTouchListener(con){
    		            	@Override
    		            	public boolean onTouch(View v, MotionEvent event) {
    		            		
    		            		x1= (int) event.getX();
		            			y1= (int) event.getY();
    		            		if(event.getAction()==MotionEvent.ACTION_DOWN)
    		            		{
    		            			x= (int) event.getX();
    		            			y= (int) event.getY();
    		            			
    		            		}
    		            		else if(event.getAction()==MotionEvent.ACTION_MOVE)
    		            		{
    		            			if((x1-x)>50&&(x1-x)>Math.abs((y1-y)))
    		            			{
    		            				menuright.toggle();
    		            			}
    		            			
    		            		}
    		            		else if(event.getAction()==MotionEvent.ACTION_UP)
    		            		{
    		            			if((x1-x)<20&&mod(y1-y)<20)
    		            			{
    		            				Intent intent = new Intent(con, Message_complete.class);
    									intent.putExtra("text", message.getText());
    									intent.putExtra("mood", message.getMood());
    									intent.putExtra("author_obj_id", message.getAuthor().getObjectId());
    									intent.putExtra("author_name", message.getAuthorName());
    									intent.putExtra("author_avatar", message.getAuthorAvatar());
    									intent.putExtra("author_obj_id", message.getAuthor().getObjectId());
    									if(message.getCommentList()!=null)
    									{
    										String[] comments = message.getCommentList().toArray(new String[message.getCommentList().size()]);
    										intent.putExtra("comments_list", comments);
    										
    										String[] authors = message.getCommentAuthors().toArray(new String[message.getCommentAuthors().size()]);
    										intent.putExtra("comments_authors", authors);
    									}
    									
    									intent.putExtra("date", message.getCreatedAt());
    									intent.putExtra("Message_object", message.getObjectId());
    									startActivity(intent);
    		            			}
    		            		}
    		            		
    		            		return true;
    		            	}
    		            });
    		            
    		            return view;
    		          }
    		        };
    		        
    		        //manage progress bar 
    		        Messages.addOnQueryLoadListener(new OnQueryLoadListener<MessageObject>() {

						@Override
						public void onLoaded(List<MessageObject> arg0,
								Exception arg1) {
							// TODO Auto-generated method stub
							pb.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoading() {
							// TODO Auto-generated method stub
							
						}
					});
    		        
    		        //setting messages adapter
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
    			    
            }
			
			
			//on swipe bottom load current visible element -1 element.
    		@Override
    		public void onSwipeBottom() {
    			scroll_function(1);
    		}
    		
    		//on swipe bottom load current visible element +1 element.
    		@Override
    		public void onSwipeTop() {
    			scroll_function(0);
    		}
    		
    		public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
    	});
		
		/*
		 * 
		 * Now WE ARE CONFIGURING COMMENTS UP_SLIDER
		 * when it is expanded we are setting panel to transparent 
		 * and resetting as it is collapsed
		 */
		final LinearLayout drag = (LinearLayout)findViewById(R.id.drag_layout);
		final TextView tv = (TextView)findViewById(R.id.comments_tv);
		//comment slider configuration
		comment_slider = (SlidingUpPanelLayout)findViewById(R.id.sliding_up);
		
		hori_list = (com.parse.buzzbox.HorizontalListView)findViewById(R.id.horizaontal_comments);
		comment_slider.setPanelSlideListener(new PanelSlideListener() {
			
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				// TODO Auto-generated method stub
				panel.setBackgroundColor(Color.TRANSPARENT);
				tv.setText("");
				drag.setBackgroundColor(Color.TRANSPARENT);
			}
			
			@Override
			public void onPanelExpanded(View panel) {
				comments_loader = (ProgressBar) findViewById(R.id.comments_loader);
				tv.setText("");
				drag.setBackgroundColor(Color.TRANSPARENT);
				panel.setBackgroundColor(Color.TRANSPARENT);
				int pos = post_list.getFirstVisiblePosition();
				int size = post_list.getCount();
				if(size>=1)
				{
						BuzzboxPost post = posts.getItem(pos);
						
						//function to retrive comments
						retrieve_comments(post, hori_list);
						
						//to detect fling to toggle slider
						hori_list.setOnTouchListener(new OnSwipeTouchListener(con){
				        	
							@Override
				        	public void onSwipeBottom() {
				        		// TODO Auto-generated method stub
				        		
				        		Log.d("bottom", "bottom");
				        		comment_slider.collapsePane();
				        	}
							
							public boolean onTouch(View v, MotionEvent event) {
				                return gestureDetector.onTouchEvent(event);
				            }
				        });
				}
			}
			
			@Override
			public void onPanelCollapsed(View panel) {
				panel.setBackgroundColor(Color.TRANSPARENT);
				tv.setText("Comments");
				drag.setBackgroundResource(R.drawable.comments);
			}
			
			@Override
			public void onPanelAnchored(View panel) {
				panel.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		
		//configure left and right slider as per standard library rules
		config_slider();
		
		
		
		// handler will receive message if app fails to retrive location
		//it can prompt a dialog box to make user turn on GPS
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
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
		
		
		
	}
	
	
	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d("onRestart", "onRestart");
		super.onRestart();
		
		
		if(ParseUser.getCurrentUser()!=null)
			if(!(ParseUser.getCurrentUser().isDataAvailable()))
				finish();
		
	}



	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		Log.d("onResume", "onResume");
		if(ParseUser.getCurrentUser()!=null)
			if(!(ParseUser.getCurrentUser().isDataAvailable()))
				finish();

		
		super.onResume();
	}



	// This method will Set up our customized query and will then update the List View.
	public void setQuery(final ParseGeoPoint pgp){
		// Set up a customized query
		
		mRefreshActionItem.showProgress(true);
		ParseQueryAdapter.QueryFactory<BuzzboxPost> factory =
		        new ParseQueryAdapter.QueryFactory<BuzzboxPost>() {
		          public ParseQuery<BuzzboxPost> create() {
		            
		        	//this query will load posts within radius, friends and friends of friends
		        	//and sort them by time 
		        	List<ParseQuery<BuzzboxPost>> queries = new ArrayList<ParseQuery<BuzzboxPost>>();
		            ParseQuery<BuzzboxPost> query1 = BuzzboxPost.getQuery();
		            query1.whereWithinKilometers("location", pgp, SEARCH_RADIUS);
		            queries.add(query1);
		            for(int i =0;i<object_ids.length;i++)
		            {
		            	ParseQuery<BuzzboxPost> query2 = BuzzboxPost.getQuery();
		            	query2.whereEqualTo("user_id", object_ids[i]);
		            	queries.add(query2);
		            }
		            if(ParseUser.getCurrentUser()!=null)
		            {
		            	if(ParseUser.getCurrentUser().getList("friends_of_friends")!=null)
			            {
			            	for(int i=0;i<ParseUser.getCurrentUser().getList("friends_of_friends").size();i++)
				            {
				            	ParseQuery<BuzzboxPost> query2 = BuzzboxPost.getQuery();
				            	query2.whereEqualTo("user_id", ParseUser.getCurrentUser().getList("friends_of_friends").get(i).toString());
				            	queries.add(query2);
				            }
			            }
		            }
		            
		            
		            
		            ParseQuery<BuzzboxPost> mainQuery = ParseQuery.or(queries);
		            
		            mainQuery.include("user");
		            
		            mainQuery.orderByDescending("createdAt");	
		            
		            //mainQuery.setLimit(MAX_POST_SEARCH_RESULTS);
		            
		            return mainQuery;
		          }
		        };
		        
		        
		        
		        // Set up the query adapter
		        posts = new ParseQueryAdapter<BuzzboxPost>(this, factory) {
		          @Override
		          public View getItemView(final BuzzboxPost post, View view, ViewGroup parent) {
		            
		        	  
		        	//configuring post card element views 
			        view = View.inflate(con, R.layout.buzzbox_post_item, null);
			        
			        LinearLayout lin = (LinearLayout)view.findViewById(R.id.complete_item);
			        lin.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height_actual));
			        
			        //to set no of posts on left menu
		            no_of_post = post.getNoofPosts();
		            TextView contentView = (TextView) view.findViewById(R.id.contentView);
		            contentView.setTextColor(Color.WHITE);
		            TextView usernameView = (TextView) view.findViewById(R.id.usernameView);
		            usernameView.setTextColor(Color.DKGRAY);
		            final TextView count = (TextView) view.findViewById(R.id.Count_of_Empathizes);
		            ImageView im = (ImageView) view.findViewById(R.id.imageView1);
		            TextView date = (TextView) view.findViewById(R.id.date);
		            date.setTextColor(Color.DKGRAY);
		            TextView time = (TextView) view.findViewById(R.id.time);
		            time.setTextColor(Color.DKGRAY);
		            ImageView im2 = (ImageView) view.findViewById(R.id.post_source);
		            
		            
		            //set friends_of_friends image if user is friends_of_friends
		            if(ParseUser.getCurrentUser()!=null)
		            {
		            	if(ParseUser.getCurrentUser().getList("friends_of_friends")!=null)
		            	{
		            		for(int i=0;i<ParseUser.getCurrentUser().getList("friends_of_friends").size();i++)
			            	{
			            		if(post.getUserId().equals(ParseUser.getCurrentUser().getList("friends_of_friends").get(i).toString())&&!post.getUserId().equals(ParseUser.getCurrentUser().getObjectId()))
			            		{
			            			im2.setImageResource(R.drawable.friends_of_friends);
			            		}
			            	}
		            	}
		            }
		            	
		            	
		            //set friends image if user is friend
		            if(post.getUserId()!=null)
		            {
		            	for(int i=0;i<object_ids.length;i++)
		            	{
		            		if(post.getUserId().equals(object_ids[i])&&!post.getUserId().equals(ParseUser.getCurrentUser().getObjectId()))
		            		{
		            			im2.setImageResource(R.drawable.friends);
		            		}
		            	}
		            }
		            
		            
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
		            final ImageView comment_but = (ImageView)view.findViewById(R.id.comment);
		            
		            comment_but.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						
							//dialog box to enter a comment and a done button
							final Dialog dialog = new Dialog(con);
							dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							
							 dialog.setContentView(R.layout.comment_layout);
							 
							 
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
											 
											 //create comment object associated with current shown post
											 CommentsObject new_comment = new CommentsObject();
											 new_comment.toPost(post.getObjectId());
											 new_comment.setText(message.getText().toString().trim());
											 new_comment.setAuthor(ParseUser.getCurrentUser());
											 new_comment.setAuthorAvatar(String.valueOf(ParseUser.getCurrentUser().getInt("Avatar")));
											 new_comment.setAuthorName(ParseUser.getCurrentUser().getUsername());
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
								 
								 
								 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
									Window window = dialog.getWindow();
									lp.copyFrom(window.getAttributes());
									//This makes the dialog take up the full width
									lp.width = WindowManager.LayoutParams.MATCH_PARENT;
									lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
									window.setAttributes(lp);
						}
					});
		            
		           
		            
		            // Empathize Button.
		            final ImageView bemp = (ImageView) view.findViewById(R.id.btnEmpathize);
		            count.setText(""+post.no_of_empathizes());
		            bemp.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if(!(ParseUser.getCurrentUser().getInt(post.getObjectId()+"emp")==1)){
								//BuzzboxPost.getQuery();
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
		            final ImageView message = (ImageView) view.findViewById(R.id.privatemessage);
		            
		            message.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							
							if(post.getUserId().equals(ParseUser.getCurrentUser().getObjectId()))
							{
								Toast.makeText(con, "Not allowed to message yourself.", Toast.LENGTH_SHORT).show();
							}
							else
							{
								//start activity that will create a private messsage to post author
								// TODO Auto-generated method stub
								Intent i = new Intent(MainActivity.this, Create_Message.class);
								 i.putExtra("obj_id", post.getUser().getObjectId());
								 i.putExtra("viaPost", post.getText());
								 i.putExtra("viaPostReceipent", post.getUser().getUsername());
								 startActivity(i);
							}
							
						}
		            	
		            });
		            return view;
		          }
		        };
	        
		        //to manage progress bar refresh element
		        posts.addOnQueryLoadListener(new OnQueryLoadListener<BuzzboxPost>() {

					@Override
					public void onLoaded(List<BuzzboxPost> arg0, Exception arg1) {
						// TODO Auto-generated method stub
						mRefreshActionItem.showProgress(false);
					}

					@Override
					public void onLoading() {
						// TODO Auto-generated method stub
						
					}
				});
	        setList(posts);
		        
	}
	
	// Attach the query Adapter to the View.
	public void setList(ParseQueryAdapter<BuzzboxPost> Po){
		
		JazzyListView postsView = (JazzyListView) this.findViewById(R.id.postsView);
        postsView.setAdapter(Po);
        postsView.setTransitionEffect(JazzyHelper.GROW);
	}
	
	
	
	
	// If user want to create a new post
	  public void new_post_function(View v)
	  {
	  	 final Dialog dialog = new Dialog(this);
		 dialog.setContentView(R.layout.new_post);
		 dialog.setTitle("New Post");
		 Button done_but = (Button) dialog.findViewById(R.id.done);
		 final EditText message = (EditText)dialog.findViewById(R.id.message);
		 
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
	  
	  

	  
	  //Menu configuration
	  public boolean onCreateOptionsMenu(Menu menu){
			
			//configuring refresh button in action bar as per standard rules given in library
		  getSupportMenuInflater().inflate(R.menu.main_activity_menu, menu);
	        MenuItem item = menu.findItem(R.id.refreshbutton);
	        mRefreshActionItem = new RefreshActionItem(getApplicationContext());
	        item.setActionView(mRefreshActionItem);
	        mRefreshActionItem.setMenuItem(item);
	        mRefreshActionItem.setProgressIndicatorType(ProgressIndicatorType.INDETERMINATE);
	        mRefreshActionItem.setRefreshActionListener(this);
			   
	        currentLocation = this.getLastKnownLocation();
				
				if(currentLocation!=null)
				{
					if(currentLocation!=null) lastLocation= currentLocation;
					
					Log.d("current loc", currentLocation.toString());
					Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
					
					p = geoPointFromLocation(myLoc);
					
					setQuery(p);

				}
	    	return true;
	     }
	  
	  
	  
	  
	  /*
	   * Helper method to get the Parse GEO point representation of a location
	   */
	  private static ParseGeoPoint geoPointFromLocation(Location loc) {
	    return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
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
		 //private message selected
		 if(item.getItemId()==R.id.private_message){
			  Intent i = new Intent(this,Private_message.class);
			  startActivity(i);
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
			 
		 }
		 
		 
		 return true;
		}
	  
	  private Location getLastKnownLocation() {
		 
		  
		  //ashish totlas code to derive location
//		  locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		  boolean netwrkenabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//		  boolean gpsenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
//			  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//			  if(locationManager != null){
//				  currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//				  System.out.println("Network me aya" + currentLocation.getLatitude());
//			  }
//			  
//		  }
//		  else if(gpsenabled){
//			  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//			  if(locationManager != null){
//				  currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//				  System.out.println("Gps me aya"+currentLocation.getLatitude());
//			  }
//		  }
//		  
//		  return currentLocation;
		  
		  
		  	//Derive loaction from best possible available resource 
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
		    	    		//call handler if no location if found
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
			//to toggle menuRight or left
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
			 if (menuleft != null
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
		
		//to retrive comment associated with given post
		public void retrieve_comments(final BuzzboxPost post, com.parse.buzzbox.HorizontalListView comments_list)
		{
			
			comments_loader.setVisibility(View.VISIBLE);
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
			            
			            
			            if(message.getAuthorName()!=null)
    		            {
    		            	username.setText(message.getAuthorName());
    		            }
    		            if(message.getAuthorAvatar()!=null)
    		            {
    		            	im.setImageResource(Integer.parseInt(message.getAuthorAvatar()));
    		            }
    		            
		            	message.getAuthor().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
		                    public void done(ParseUser object, ParseException e) {
		                         if(e==null)
		                         {
		                        	 im.setImageResource(object.getInt("Avatar"));
		                        	 username.setText(object.getUsername());
		                        	 username.setGravity(Gravity.CENTER_HORIZONTAL);
		                         }
		                      }
		                  });
						
		            	
			            message_text.setText(message.getText());
			           
			            return view;
			          }
			        };
			        Comments_adapter.addOnQueryLoadListener(new OnQueryLoadListener<CommentsObject>() {

						@Override
						public void onLoaded(List<CommentsObject> arg0,
								Exception arg1) {
							// TODO Auto-generated method stub
							
							comments_loader.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoading() {
							// TODO Auto-generated method stub
							
						}
					});
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
		//to scroll post card one by one
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



		//refresh posts list
		@Override
		public void onRefreshButtonClick(RefreshActionItem sender) {
			// TODO Auto-generated method stub
			currentLocation= this.getLastKnownLocation(); 
			 p=geoPointFromLocation(currentLocation);
			 setQuery(p);
		}
		
		
		//change font
		@Override
		  protected void attachBaseContext(Context newBase) {
		      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
		  }
		
		
		int mod(int x)
		{
			if(x<0)
				return x*-1;
			else
				return x;
		}
		
		
}
