package com.parse.buzzbox;

import java.util.ArrayList;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class newPost extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	static ParseGeoPoint pgp=null;
	
	public newPost(){
		
	}
	
	public newPost(ParseGeoPoint p){
		pgp=p;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_post);
		
		Spinner moods = (Spinner)findViewById(R.id.moods);
		 final ArrayList<String> listmoods = new ArrayList<String>();
		 listmoods.add("No Feelings!");
		 listmoods.add("Anger");
		 listmoods.add("Fear");
		 listmoods.add("Love/Lust");
		 listmoods.add("Depression");
		 listmoods.add("Sadness");
		 listmoods.add("Curious");
		 listmoods.add("Irritation");
		 listmoods.add("Worried");
		 listmoods.add("Happy");
		 listmoods.add("Sympathy");
		 listmoods.add("Relief");
		 listmoods.add("Hyper");
		 listmoods.add("Secritive");
		 listmoods.add("Disgusted/Sick");
		 listmoods.add("Nervous");
		 listmoods.add("Sleepy/Dizzy");
		 
		 ArrayAdapter<String> aa = new ArrayAdapter<String>(newPost.this,android.R.layout.simple_spinner_item, listmoods);
		 aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		 moods.setAdapter(aa);
		 
		 final TextView finalmood = (TextView)findViewById(R.id.finalmood);
		 finalmood.setText("You are currently feeling: "+listmoods.get(0));
		 
		 moods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        @Override
		        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		        			        	
		        	finalmood.setText(listmoods.get(position));
		        			        	
		        }

		        @Override
		        public void onNothingSelected(AdapterView<?> parent) {

		        }
		    });
		 
		 Button done_but = (Button)findViewById(R.id.done);
		 final EditText message = (EditText)findViewById(R.id.message);
		 
		 done_but.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 //post function
				 if(message.getText().toString().trim().length()<1)
				 {
					 Toast.makeText(newPost.this, "Please enter a valid text", Toast.LENGTH_SHORT).show();
				 }
				 else
				 {
					 //Postflag=1;
					 String Post = message.getText().toString();
//					 PostBuzz(p);
					 
					 BuzzboxPost new_post = new BuzzboxPost();
					 new_post.setUser(ParseUser.getCurrentUser());
					 
					 new_post.setText(Post);
					 new_post.set_no_of_empathizes(0);
					 new_post.Init(ParseUser.getCurrentUser().getUsername(),R.drawable.hidden);
					 
					 int temp = new_post.getNoofPosts()+1;
					 ParseUser.getCurrentUser().put("noOfPosts",temp);
					 ParseUser.getCurrentUser().saveInBackground();
					 
					 new_post.setLocation(pgp);
					 final ProgressDialog pdLoading = new ProgressDialog(newPost.this);
					 pdLoading.setMessage("\tPosting your Buzz...\n \t Please Wait!!");
					 new_post.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if(e==null)
							{
								pdLoading.dismiss();
								Toast.makeText(newPost.this, "Successfully posted.. Updating your List now!", Toast.LENGTH_SHORT).show();
								//setQuery(pgp);	// Update the List.
								finish();
							}
							else
							{
								Log.d("error post", e.getMessage().toString());
								Toast.makeText(newPost.this, "Posting failed. Please check internet connection"+e.toString(), Toast.LENGTH_LONG).show();
							}
							
						}
					});
					 
					 pdLoading.show();
					 
				 }
				 
			 }
		 });
		 
	}
	
	
	
	
}
