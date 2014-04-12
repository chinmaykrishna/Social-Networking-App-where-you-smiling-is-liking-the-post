package com.parse.buzzbox;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.R.drawable;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class My_messages extends Activity{
	
	
	private ParseQueryAdapter<MessageObject> Messages;
	private ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_messages);
		list  = (ListView)findViewById(R.id.messages);
		setQuery();
	}
	
	// This method will retrieve messages from server
		public void setQuery(){
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
		        Messages = new ParseQueryAdapter<MessageObject>(this, factory) {
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
			        
		}
}
