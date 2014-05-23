package com.parse.buzzbox;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class Message_complete extends Activity{
	
	String message_id;
	MessageObject message;
	View viewToLoad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_message);
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
        
        if((List<String>)this.getIntent().getSerializableExtra("comments_list")!=null)
        {
        	ListView comments = (ListView)findViewById(R.id.message_comments);
            yourAdapter adapter = new yourAdapter(getApplicationContext(), (List<String>)this.getIntent().getSerializableExtra("comments_list"), (List<ParseUser>)this.getIntent().getSerializableExtra("comments_list"));
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
		
		
//		load_screen();
//		
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
        	ListView comments = (ListView)findViewById(R.id.message_comments);
            yourAdapter adapter = new yourAdapter(getApplicationContext(), message.getCommentList(), message.getCommentAuthors());
            comments.setAdapter(adapter);
        }
        
	}
	
	
	class yourAdapter extends BaseAdapter {

	    Context context;
	    List<String> comments;
	    List<ParseUser> author;
	    private LayoutInflater inflater = null;

	    
	    public yourAdapter(Context context, List<String> comments, List<ParseUser> author) {
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
	        if(author.get(position)==ParseUser.getCurrentUser())
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
}
