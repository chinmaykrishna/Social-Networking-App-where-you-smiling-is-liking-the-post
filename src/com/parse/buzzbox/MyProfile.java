package com.parse.buzzbox;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    MainActivity act;
	
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

	public MyProfile()
	{
		 
	}
	public MyProfile(MainActivity act)
	{
		this.act =act; 
	}
	
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
        selectedImage.setImageResource(ParseUser.getCurrentUser().getInt("Avatar"));
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
		pdLoading.setMessage("\tUpdating...\n \t Please Wait!!");
		
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
		 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialog.setContentView(R.layout.changenick);
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
		 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = dialog.getWindow();
			lp.copyFrom(window.getAttributes());
			//This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
	}
	
	public void myPass(View v){
		
		 final Dialog dialog = new Dialog(this);
		 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 
		 dialog.setContentView(R.layout.changepass);
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
		 WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			Window window = dialog.getWindow();
			lp.copyFrom(window.getAttributes());
			//This makes the dialog take up the full width
			lp.width = WindowManager.LayoutParams.MATCH_PARENT;
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			window.setAttributes(lp);
	}
	
	public void logout(View v){
		ParseUser.getCurrentUser().logOut();
		Log.d("asdasd", "logout");
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
		
	}
	@Override
	  protected void attachBaseContext(Context newBase) {
	      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	  }
	
}
