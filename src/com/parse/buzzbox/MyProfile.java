package com.parse.buzzbox;


import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyProfile extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	TextView currnick;
	
	private Integer[] mImageIds = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
            R.drawable.avatar9
    };
	
	ImageView selectedImage=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.profilechanger);
		
		currnick = (TextView) findViewById(R.id.currentnick);
		currnick.setText(currnick.getText()+ParseUser.getCurrentUser().getUsername());
		
		@SuppressWarnings("deprecation")
		Gallery gallery = (Gallery) findViewById(R.id.gallery);
        selectedImage=(ImageView)findViewById(R.id.avatar);
        gallery.setSpacing(1);
        gallery.setAdapter(new GalleryImageAdapter(this));

         // clicklistener for Gallery
        gallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	
            	// Toast.makeText(MyProfile.this, "Your selected position = " + position, Toast.LENGTH_SHORT).show();
                // show the selected Image
                selectedImage.setImageResource(mImageIds[position]);
                ParseUser.getCurrentUser().put("Avatar",mImageIds[position] );
                //int temp=selectedImage.getId();
                //Toast.makeText(MyProfile.this, "Your selected position = " + , Toast.LENGTH_SHORT).show();
            }
        });
		
	}
	
	public void update(View v){
		
		final ProgressDialog pdLoading = new ProgressDialog(MyProfile.this);
		pdLoading.setMessage("\tLoading...");
		
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
        	       	
        	
        	
			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e==null)
				{
					Toast.makeText(MyProfile.this, "Profile updated Successfully :)", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Log.d("error while sending", e.getMessage().toString());
					Toast.makeText(MyProfile.this, "Updating profile failed. Please check internet connection", Toast.LENGTH_SHORT).show();
				}
				
				pdLoading.cancel();
				
			}
		});
        pdLoading.show();
        
	}
	
	public void myNick(View v){
		
		 final Dialog dialog = new Dialog(this);
		 final ProgressDialog pdLoading = new ProgressDialog(MyProfile.this);
		 dialog.setContentView(R.layout.changenick);
		 dialog.setTitle("Change Nick");	
		 Button dialogButtonA = (Button) dialog.findViewById(R.id.dialogButtonOK);
		 Button dialogButtonC = (Button) dialog.findViewById(R.id.dialogButtonCancel);

		 //cancel button clicked
		 dialogButtonC.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 dialog.cancel();
			 }
		 });

		 //Change button clicked
		 dialogButtonA.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 EditText newnick = (EditText)dialog.findViewById(R.id.nick);
				 //newnick.setHint(ParseUser.getCurrentUser().getUsername());
				 
				 String nick = newnick.getText().toString();
				 if(!(nick.isEmpty())){
					 ParseUser.getCurrentUser().setUsername(nick);
//					 try {
//						ParseUser.getCurrentUser().save();
//						currnick.setText("Hi, "+nick);
//						Toast mtoast = Toast.makeText(MyProfile.this, "Nick successfully changed :)", Toast.LENGTH_LONG);
//			 		 	mtoast.show();
//					} catch (ParseException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Toast mtoast = Toast.makeText(MyProfile.this, "Nick changing failed :(", Toast.LENGTH_LONG);
//			 		 	mtoast.show();
//					}
					 	
				 }
				 else{
		 			 Toast mtoast = Toast.makeText(MyProfile.this, "Please enter a valid Nick.", Toast.LENGTH_LONG);
		 		 	 mtoast.show();
				 }
			 dialog.dismiss();


			 }

		 });

		 dialog.show();
	}
	
	public void myPass(View v){
		
		 final Dialog dialog = new Dialog(this);
		 dialog.setContentView(R.layout.changepass);
		 dialog.setTitle("Change Password");	
		 Button dialogButtonA = (Button) dialog.findViewById(R.id.dialogButtonOK);
		 Button dialogButtonC = (Button) dialog.findViewById(R.id.dialogButtonCancel);

		 //cancel button clicked
		 dialogButtonC.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 dialog.cancel();
			 }
		 });

		 //Change button clicked
		 dialogButtonA.setOnClickListener(new OnClickListener() {
			 @Override
			 public void onClick(View v) {
				 EditText newpass = (EditText)dialog.findViewById(R.id.pass);
				 //newnick.setHint(ParseUser.getCurrentUser().getUsername());
				 
				 String pass = newpass.getText().toString();
				 if(!(pass.isEmpty())){
					 ParseUser.getCurrentUser().setPassword(pass);
					 try {
						ParseUser.getCurrentUser().save();
						Toast mtoast = Toast.makeText(MyProfile.this, "Password successfully changed :)", Toast.LENGTH_LONG);
			 		 	 mtoast.show();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast mtoast = Toast.makeText(MyProfile.this, "Password changing failed :(", Toast.LENGTH_LONG);
			 		 	 mtoast.show();
					}
					 
		
				 }
				 else{
		 			 Toast mtoast = Toast.makeText(MyProfile.this, "Please enter a valid Password.", Toast.LENGTH_LONG);
		 		 	 mtoast.show();
				 }
			 dialog.dismiss();


			 }

		 });

		 dialog.show();
	}
	
	public void logout(View v){
		ParseUser.getCurrentUser().logOut();
		finish();
	}
	
	
}
