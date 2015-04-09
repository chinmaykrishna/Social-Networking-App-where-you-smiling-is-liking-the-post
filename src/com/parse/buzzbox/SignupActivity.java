package com.parse.buzzbox;

import java.util.HashMap;
import java.util.Random;

import phone_numbers.To_international;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.app.Activity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


//signup activity
public class SignupActivity extends Activity{
	//all editTexts
	private EditText usernameView;
	  private EditText passwordView;
	  private EditText passwordAgainView;
	  private EditText phoneNumberView;
	  
	  private Context con;
	  //it will be 1 if request for sending sms if going on so that
	  //user cant make many requests at a time
	  private int busy = 0;
	  private String number;
	  private int verification_code;
	  private ParseUser user;
	  private EditText verificatio_ed;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//fullscreen 
		
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup);
		setTitle("Sign up");
		con = this;
		
	    // Set up the signup form.
	    usernameView = (EditText) findViewById(R.id.username);
	    passwordView = (EditText) findViewById(R.id.password);
	    passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
	    phoneNumberView = (EditText) findViewById(R.id.phone_number);
	    
	 // Set up the submit button click handler
	    findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
	      public void onClick(View view) {

	        // Validate the sign up data
	        boolean validationError = false;
	        StringBuilder validationErrorMessage =
	            new StringBuilder(getResources().getString(R.string.error_intro));
	        if (isEmpty(usernameView)) {
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
	        }
	        if (isEmpty(phoneNumberView)) {
		          if (validationError) {
		            validationErrorMessage.append(getResources().getString(R.string.error_join));
		          }
		          validationError = true;
		          validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
		        }
	        if (isEmpty(passwordView)) {
	          if (validationError) {
	            validationErrorMessage.append(getResources().getString(R.string.error_join));
	          }
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(R.string.error_blank_phone_number));
	        }
	        if (!isMatching(passwordView, passwordAgainView)) {
	          if (validationError) {
	            validationErrorMessage.append(getResources().getString(R.string.error_join));
	          }
	          validationError = true;
	          validationErrorMessage.append(getResources().getString(
	              R.string.error_mismatched_passwords));
	        }
	        validationErrorMessage.append(getResources().getString(R.string.error_end));
	        
	        if (validationError) {
	            Toast.makeText(SignupActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
	                .show();
	            return;
	          }
	     // Set up a progress dialog
	        

	        //To convert phone number to international format
	        To_international ti = new To_international(con);
	        
	        // Set up a new Parse user
	        user = new ParseUser();
	        user.setUsername(usernameView.getText().toString());
	        user.setPassword(passwordView.getText().toString());
	        user.put("Avatar", R.drawable.avatar1);
	        user.put("noOfPosts", 0);
	        if(ti.change_to_international(phoneNumberView.getText().toString())==null)
	        {
	        	//wrong phone number
	        	// Show the error message
	            Toast.makeText(con, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
	        }
	        else
	        {
	        	//everything is set. now we will verify phone number
	        	 number = ti.change_to_international(phoneNumberView.getText().toString());
	        	 /*Random r = new Random();
	        	 //generate a six digit verification code
	        	 verification_code = (int) (100000 + r.nextFloat() * 900000);
	        	 setTitle("Number verification");
	        	 //change content layout
	        	setContentView(R.layout.sms_check);
	        	TextView tv = (TextView)findViewById(R.id.tv);
	        	verificatio_ed = (EditText)findViewById(R.id.veri_ed);
	        	tv.setText("You will receive a verification code on: "+number+" via text message shortly");
	        	busy =1;
	        	HashMap<String, String> ha = new HashMap<String, String>();
	    		 ha.put("number", number);
	    		 ha.put("message", "Cloak Verification Code: "+verification_code);
	    		ParseCloud.callFunctionInBackground("send_sms", ha, new FunctionCallback<String>() {
	    			  public void done(String result, ParseException e) {
	    				  busy = 0;
	    			    if (e == null) {
	    			    	
	    			    	//message sending successful
	    			      Log.d("%%%%%%%%%%%%%%%%%%%%%%", result);
	    			    }
	    			    else{
	    			    	Log.d("%%%%%%%%%%%%%%%%%%%%%%", e.getMessage());
	    				      
	    				}
	    			  }
	    			});*/
	        	final ProgressDialog dlg = new ProgressDialog(SignupActivity.this);
 		        dlg.setTitle("Please wait.");
 		        dlg.setMessage("Signing up.");
 		        dlg.show();
 		        
 		        user.put("phone_number", number);
 		        // Call the Parse signup method
 		        user.signUpInBackground(new SignUpCallback() {

 		          @Override
 		          public void done(ParseException e) {
 		            dlg.dismiss();
 		            if (e != null) {
 		              // Show the error message
 		              Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
 		            } else {
 		              // Start an intent for the dispatch activity
 		              Intent intent = new Intent(SignupActivity.this, DispatchActivity.class);
 		              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
 		              startActivity(intent);
 		            }
 		          }
 		        });
	        }


	}

	    });  
}
	
	    private boolean isEmpty(EditText etText) {
	        if (etText.getText().toString().trim().length() > 0) {
	          return false;
	        } else {
	          return true;
	        }
	      }

	      private boolean isMatching(EditText etText1, EditText etText2) {
	        if (etText1.getText().toString().equals(etText2.getText().toString())) {
	          return true;
	        } else {
	          return false;
	        }
	      }
	      
	      @Override
	      protected void attachBaseContext(Context newBase) {
	          super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	      }
	      
	      // to send verification code again
	     public void resend_fun(View v)
	     {
	    	 if(busy==0)
	    	 {
	    		 busy =1;
	    		 HashMap<String, String> ha = new HashMap<String, String>();
	    		 ha.put("number", number);
	    		 ha.put("message", "Cloak Verification Code: "+verification_code);
	    		ParseCloud.callFunctionInBackground("send_sms", ha, new FunctionCallback<String>() {
	    			  public void done(String result, ParseException e) {
	    				  busy =0;
	    			    if (e == null) {
	    			    	Toast.makeText(con, "Successfully sent.", Toast.LENGTH_LONG).show();
	    			      Log.d("%%%%%%%%%%%%%%%%%%%%%%", result);
	    			    }
	    			    else{
	    			    	Log.d("%%%%%%%%%%%%%%%%%%%%%%", e.getMessage());
	    				      
	    				}
	    			  }
	    			});
	    	 }
	    	 else
	    	 {
	    		 Toast.makeText(con, "Please wait. Already sending another request.", Toast.LENGTH_LONG).show();
	    	 }
	     }
	     
	  // Done button after entering the received code code
	     public void done_fun(View v)
	     {
	    	 
	    	 if(verificatio_ed.getText().toString().trim().length()<1)
	    	 {
	    		 Toast.makeText(con, "Please enter a valid verification code", Toast.LENGTH_LONG).show();
	    	 }
	    	 else
	    	 {
	    		 if(verificatio_ed.getText().toString().trim().equals(Integer.toString(verification_code)))
	    		 {
	    			 //Authentication successful
		    			final ProgressDialog dlg = new ProgressDialog(SignupActivity.this);
		 		        dlg.setTitle("Please wait.");
		 		        dlg.setMessage("Signing up.");
		 		        dlg.show();
		 		        
		 		        user.put("phone_number", number);
		 		        // Call the Parse signup method
		 		        user.signUpInBackground(new SignUpCallback() {
		
		 		          @Override
		 		          public void done(ParseException e) {
		 		            dlg.dismiss();
		 		            if (e != null) {
		 		              // Show the error message
		 		              Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
		 		            } else {
		 		              // Start an intent for the dispatch activity
		 		              Intent intent = new Intent(SignupActivity.this, DispatchActivity.class);
		 		              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		 		              startActivity(intent);
		 		            }
		 		          }
		 		        });
	    		 }
	    		 else
	    			 Toast.makeText(con, "Verification code mismatch. Please try again.", Toast.LENGTH_LONG).show();
	    	 }
	    	 
	     }
}
