package com.parse.buzzbox;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class BuzzboxApplication extends Application {
	
	 // Debugging tag for the application
	  public static final String TAG = "Buzzbox";

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "7teCmwjmSQIPiZ3yX3UHK93b8dJeu9CDjYOHIS6K", "YCI5fdYh8Wh6wGCGjMq04G5ZSXR2xs3DLlQhYqAq");

		
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
	}

}
