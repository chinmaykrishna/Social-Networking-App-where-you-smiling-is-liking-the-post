package com.parse.buzzbox;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseObject;

//Our first activity if user is not logged in
public class SignupOrLogin extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		    
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.login_or_signup);
				ParseAnalytics.trackAppOpened(getIntent());
		((ImageButton) findViewById(R.id.loginButton)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(SignupOrLogin.this,LoginActivity.class));
			}
		});
		
		((ImageButton) findViewById(R.id.signupButton)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(SignupOrLogin.this,SignupActivity.class));
			}
		});
	}
	
	@Override
	  protected void attachBaseContext(Context newBase) {
	      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	  }
}
