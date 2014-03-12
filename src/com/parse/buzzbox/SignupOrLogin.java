package com.parse.buzzbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseAnalytics;

public class SignupOrLogin extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
				ParseAnalytics.trackAppOpened(getIntent());
		((Button) findViewById(R.id.loginButton)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(SignupOrLogin.this,LoginActivity.class));
			}
		});
		
		((Button) findViewById(R.id.signupButton)).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				startActivity(new Intent(SignupOrLogin.this,SignupActivity.class));
			}
		});
	}
}
