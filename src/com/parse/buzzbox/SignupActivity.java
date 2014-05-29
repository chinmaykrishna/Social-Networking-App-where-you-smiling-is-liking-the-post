package com.parse.buzzbox;

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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignupActivity extends Activity{
	private EditText usernameView;
	  private EditText passwordView;
	  private EditText passwordAgainView;
	  private EditText phoneNumberView;
	  private Context con; 
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//fullscreen 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup);

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
	        final ProgressDialog dlg = new ProgressDialog(SignupActivity.this);
	        dlg.setTitle("Please wait.");
	        dlg.setMessage("Signing up.");
	        dlg.show();

	        //To convert phone number to international format
	        To_international ti = new To_international(con);
	        
	        // Set up a new Parse user
	        ParseUser user = new ParseUser();
	        user.setUsername(usernameView.getText().toString());
	        user.setPassword(passwordView.getText().toString());
	        user.put("Avatar", R.drawable.avatar1);
	        user.put("noOfPosts", 0);
	        if(ti.change_to_international(phoneNumberView.getText().toString())==null)
	        {
	        	//wrong phone number
	        	// Show the error message
	        	dlg.dismiss();
	            Toast.makeText(con, "Please enter a valid phone number", Toast.LENGTH_LONG).show();
	        }
	        else
	        {
	        	Log.d("phone number checking", ti.change_to_international(phoneNumberView.getText().toString()));
	        	user.put("phone_number", ti.change_to_international(phoneNumberView.getText().toString()));
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

	    }
