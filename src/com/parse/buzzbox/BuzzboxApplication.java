package com.parse.buzzbox;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class BuzzboxApplication extends Application {
	
	 // Debugging tag for the application
	  public static final String TAG = "Buzzbox";

	@Override
	public void onCreate() {
		super.onCreate();

		//changing fonts of all activity to robot regular
		CalligraphyConfig.initDefault("fonts/robotoregular.ttf");
		
		//initializing parse here. 
		Parse.initialize(this, "D6T3mYwlm8bIh8Fc8bzXGmSI80u0b4CXjdvNY2cV","gzy3Q6OH0CsTVxdd7cYGiZCTeMu7bj4SD5OiOow0");
		//need to define all object created here
		ParseObject.registerSubclass(BuzzboxPost.class);
		ParseObject.registerSubclass(MessageObject.class);
		ParseObject.registerSubclass(CommentsObject.class);
		
	}

}
