package com.parse.buzzbox;

import android.app.Application;

import com.parse.Parse;

public class BuzzboxApplication extends Application {
	
	 // Debugging tag for the application
	  public static final String TAG = "Buzzbox";

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "7teCmwjmSQIPiZ3yX3UHK93b8dJeu9CDjYOHIS6K", "YCI5fdYh8Wh6wGCGjMq04G5ZSXR2xs3DLlQhYqAq");

		
	}

}
