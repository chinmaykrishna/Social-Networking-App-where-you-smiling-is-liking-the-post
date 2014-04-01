package com.parse.buzzbox;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class BuzzboxApplication extends Application {
	
	 // Debugging tag for the application
	  public static final String TAG = "Buzzbox";

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "nOuI36TTrBv426lobvsaE63UhV3XR7Av2397Ga2j", "y6XtplZWQToWEKJcSW8QKy1t1hz2Z6eKjdaFMpZU");
		ParseObject.registerSubclass(BuzzboxPost.class);
		
	}

}
