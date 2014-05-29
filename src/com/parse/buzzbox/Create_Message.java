package com.parse.buzzbox;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

public class Create_Message extends Activity{
	
	private String parse_user_obj_id;
	private String viaPost;
	private String viaPostReceipentName;
	private Integer[] moodIds = {
            R.drawable.noemotion,
            R.drawable.anger,
            R.drawable.fear,
            R.drawable.love,
            R.drawable.depression,
            R.drawable.sadness,
            R.drawable.curious,
            R.drawable.irritation,
            R.drawable.worry,
            R.drawable.happy,
            R.drawable.sympathy,
            R.drawable.relief,
            R.drawable.hyper,
            R.drawable.secritive,
            R.drawable.sick,
            R.drawable.nervous,
            R.drawable.sleepy
    };
	private static int mood_id;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_post);
			
		
		viaPost = this.getIntent().getExtras().getString("viaPost");
		
		parse_user_obj_id = this.getIntent().getExtras().getString("obj_id");
		
		if(this.getIntent().getExtras().getString("viaPostReceipent")!=null)
		{
			viaPostReceipentName =  this.getIntent().getExtras().getString("viaPostReceipent");
		}
		else
			viaPostReceipentName = "";
		
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
		 
		 ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listmoods);
		 aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		 moods.setAdapter(aa);
		 
		 final TextView finalmood = (TextView)findViewById(R.id.finalmood);
		 finalmood.setText("You are currently feeling: "+listmoods.get(0));
		 
		 moods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		        @Override
		        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		        			        	
		        	finalmood.setText(listmoods.get(position));
		        	mood_id = moodIds[position];
		        			        	
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
					 Toast.makeText(Create_Message.this, "Please enter a valid text", Toast.LENGTH_SHORT).show();
				 }
				 else
				 {
					 
						 String message_text = message.getText().toString().trim();
						 
						 MessageObject mo = new MessageObject();
						 
						 mo.setAuthor(ParseUser.getCurrentUser());
						 mo.setAuthorAvatar(String.valueOf(ParseUser.getCurrentUser().getInt("Avatar")));
						 mo.setAuthorName(ParseUser.getCurrentUser().getUsername());
						 mo.setEmpathised(false);
						 mo.setMood(mood_id);
						 mo.setReceipentObjID(parse_user_obj_id);
						 mo.setText(message_text);
						 mo.setViaPost(viaPost);
						 mo.setViaPostReceipentName(viaPostReceipentName);
						 
						 final ProgressDialog pdLoading = new ProgressDialog(Create_Message.this);
						 pdLoading.setMessage("Sending Message...");
						 
						 
						 
						 mo.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException e) {
								// TODO Auto-generated method stub
								if(e==null)
								{
									pdLoading.dismiss();
									Toast.makeText(Create_Message.this, "Successfully Sent", Toast.LENGTH_SHORT).show();
									finish();
								}
								else
								{
									pdLoading.dismiss();
									Log.d("error post", e.getMessage().toString());
									Toast.makeText(Create_Message.this, "Sending failed. Please check internet connection"+e.toString(), Toast.LENGTH_LONG).show();
								}
								
							}
						});
						 
						 pdLoading.show();

					 
					 					 
				 }
				 
			 }
		 });
	}

	@Override
	  protected void attachBaseContext(Context newBase) {
	      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	  }
}
