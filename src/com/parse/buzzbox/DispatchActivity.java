package com.parse.buzzbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;

public class DispatchActivity extends Activity{
	public DispatchActivity() {
	  }

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    // Check if there is current user info
	    if (ParseUser.getCurrentUser() != null) {
	      // Start an intent for the logged in activity
	      startActivity(new Intent(this, MainActivity.class));
	      finish();
	    } else {
	      // Start and intent for the logged out activity
	      startActivity(new Intent(this, LoginActivity.class));
	      finish();
	    }

}
}
