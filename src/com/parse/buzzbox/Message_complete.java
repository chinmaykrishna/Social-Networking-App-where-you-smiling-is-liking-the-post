package com.parse.buzzbox;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.twotoasters.jazzylistview.JazzyListView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class Message_complete extends Activity{
	
	String message_id;
	MessageObject message;
	View viewToLoad;
	Context con;
	String receipent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_message);
		con = this;
		viewToLoad = LayoutInflater.from(
	            getApplicationContext()).inflate(
	            R.layout.complete_message_element, null);
		viewToLoad.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((LinearLayout) findViewById(R.id.main_screen)).addView(viewToLoad);
		
		TextView contentView = (TextView) viewToLoad.findViewById(R.id.contentView);
        final TextView usernameView = (TextView) viewToLoad.findViewById(R.id.usernameView);
        final ImageView im = (ImageView) viewToLoad.findViewById(R.id.imageView1);
        TextView date = (TextView) viewToLoad.findViewById(R.id.date);
        TextView time = (TextView) viewToLoad.findViewById(R.id.time);
        
        receipent = this.getIntent().getExtras().getString("receipent");
        contentView.setText(this.getIntent().getExtras().getString("text"));
        contentView.setBackgroundResource(this.getIntent().getExtras().getInt("mood"));
        
        Date dtime = (Date)this.getIntent().getSerializableExtra("date");
        Date ddate = dtime;
        SimpleDateFormat dateFormattime = new SimpleDateFormat("hh:mm");
        SimpleDateFormat dateFormatdate = new SimpleDateFormat("dd-MM-yyyy");
        dateFormattime.setTimeZone(Calendar.getInstance().getTimeZone());
        dateFormatdate.setTimeZone(Calendar.getInstance().getTimeZone());
        String timeString = dateFormattime.format(dtime);
        String dateString = dateFormatdate.format(ddate);
        date.setText(dateString);
        time.setText(timeString);
        
        
        if(this.getIntent().getExtras().getString("author_name")!=null)
        {
        	usernameView.setText(this.getIntent().getExtras().getString("author_name"));
        }
        if(this.getIntent().getExtras().getString("author_avatar")!=null)
        {
        	im.setImageResource(Integer.parseInt(this.getIntent().getExtras().getString("author_avatar")));
        }
        
        
        if(this.getIntent().getExtras().getStringArray("comments_list")!=null)
        {
        	JazzyListView comments = (JazzyListView)findViewById(R.id.message_comments);
            yourAdapter adapter = new yourAdapter(con, Arrays.asList(this.getIntent().getExtras().getStringArray("comments_list")), Arrays.asList(this.getIntent().getExtras().getStringArray("comments_authors")));
            comments.setAdapter(adapter);
        }
        
		message_id = this.getIntent().getExtras().getString("Message_object");
		ParseQuery<MessageObject> q = MessageObject.getQuery();
		q.getInBackground(message_id, new GetCallback<MessageObject>() {
			
			@Override
			public void done(MessageObject me, ParseException e) {
				// TODO Auto-generated method stub
				if(e==null)
				{
					message = me;
					load_screen();
				}
				else
				{
					
				}
			}
		});
//		
		final ImageView comment_but = (ImageView)viewToLoad.findViewById(R.id.comment);
        
        comment_but.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				
				final Dialog dialog = new Dialog(con);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				 dialog.setContentView(R.layout.comment_layout);
				 Button done_but = (Button) dialog.findViewById(R.id.done);
				 final EditText ed = (EditText)dialog.findViewById(R.id.message);
				 
				 	//send button clicked
					 done_but.setOnClickListener(new OnClickListener() {

						 @Override
						 public void onClick(View v) {
							 //send function
							 
							 if(ed.getText().toString().trim().length()<1)
							 {
								 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
							 }
							 else
							 {
								 
								 if(message==null)
								 {
									 ParseQuery<MessageObject> q = MessageObject.getQuery();
										q.getInBackground(message_id, new GetCallback<MessageObject>() {
											
											@Override
											public void done(final MessageObject me, ParseException e) {
												// TODO Auto-generated method stub
												if(e==null)
												{
													
													me.addComment(ed.getText().toString().trim(), ParseUser.getCurrentUser().getObjectId());
													me.saveInBackground(new SaveCallback() {
														
														@Override
														public void done(ParseException arg0) {
															// TODO Auto-generated method stub
															if(arg0==null)
															{
																Toast.makeText(con, "Comment Successful", Toast.LENGTH_SHORT).show();
																message = me;
																load_screen();
															}
															else
															{
																Toast.makeText(con, "Comment unsuccessful. Please check your internet connection.", Toast.LENGTH_SHORT).show();
															}
														}
													});
												}
												else
												{
													
												}
											}
										});

								 }
								 else
								 {
									 message.addComment(ed.getText().toString().trim(), ParseUser.getCurrentUser().getObjectId());
										message.saveInBackground(new SaveCallback() {
											
											@Override
											public void done(ParseException arg0) {
												// TODO Auto-generated method stub
												Toast.makeText(con, "Comment Successful", Toast.LENGTH_SHORT).show();
												load_screen();
											}
										});
										 

								 }
								 
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
        
        ((ImageView)viewToLoad.findViewById(R.id.privatemessage)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(con, Create_Message.class);
				 i.putExtra("obj_id", receipent);
				 i.putExtra("viaPost", "");
				 startActivity(i);
			}
		});
        
		
	}
	
	private void load_screen()
	{
		TextView contentView = (TextView) viewToLoad.findViewById(R.id.contentView);
        final TextView usernameView = (TextView) viewToLoad.findViewById(R.id.usernameView);
        final ImageView im = (ImageView) viewToLoad.findViewById(R.id.imageView1);
        TextView date = (TextView) viewToLoad.findViewById(R.id.date);
        TextView time = (TextView) viewToLoad.findViewById(R.id.time);
        
        contentView.setText(message.getText());
        
        contentView.setBackgroundResource(message.getInt("mood"));
        
        if(message.getCreatedAt()!=null)
        {
        	Date dtime = message.getCreatedAt();
            Date ddate = dtime;
            SimpleDateFormat dateFormattime = new SimpleDateFormat("hh:mm");
            SimpleDateFormat dateFormatdate = new SimpleDateFormat("dd-MM-yyyy");
            dateFormattime.setTimeZone(Calendar.getInstance().getTimeZone());
            dateFormatdate.setTimeZone(Calendar.getInstance().getTimeZone());
            String timeString = dateFormattime.format(dtime);
            String dateString = dateFormatdate.format(ddate);
            date.setText(dateString);
            time.setText(timeString);
        }
        		            
        
        
        if(message.getAuthorName()!=null)
        {
        	usernameView.setText(message.getAuthorName());
        }
        if(message.getAuthorAvatar()!=null)
        {
        	im.setImageResource(Integer.parseInt(message.getAuthorAvatar()));
        }
        
        message.getAuthor().fetchIfNeededInBackground(new GetCallback<ParseUser>() {
      	  public void done(ParseUser object, ParseException e) {
      		    if (e == null) {
      		    	usernameView.setText(object.getUsername());
      		    	im.setImageResource(object.getInt("Avatar"));
      		    } else {
      		      // Failure!
      		    }
      		  }
      		});
        if(message.getCommentList()!=null)
        {
        	JazzyListView comments = (JazzyListView)findViewById(R.id.message_comments);
            yourAdapter adapter = new yourAdapter(getApplicationContext(), message.getCommentList(), message.getCommentAuthors());
            comments.setAdapter(adapter);
        }
        
	}
	
	
	class yourAdapter extends BaseAdapter {

	    Context context;
	    List<String> comments;
	    List<String> author;
	    private LayoutInflater inflater = null;

	    
	    public yourAdapter(Context context, List<String> comments, List<String> author) {
	        // TODO Auto-generated constructor stub
	        this.context = context;
	        this.comments = comments;
	        this.author = author;
	        inflater = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }

	    @Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return comments.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        // TODO Auto-generated method stub
	        return comments.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        // TODO Auto-generated method stub
	        return position;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        // TODO Auto-generated method stub
	        View vi = convertView;
	        if (vi == null)
	            vi = inflater.inflate(R.layout.message_comment, null);
	        	
	        TextView text = (TextView) vi.findViewById(R.id.comment_text);
	        text.setText(comments.get(position));
	        
	        	if(!author.get(position).equals(ParseUser.getCurrentUser().getObjectId()))
		        {
		        	vi.setBackgroundColor(Color.parseColor("#8B7D7B"));
		        	text.setTextColor(Color.WHITE);
		        }
		        else
		        {
		        	vi.setBackgroundColor(Color.parseColor("#F3F2F1"));
		        	text.setTextColor(Color.BLACK);
		        }
	        
	        
	        
	        return vi;
	    }
	}
	
	@Override
	  protected void attachBaseContext(Context newBase) {
	      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	  }
}
