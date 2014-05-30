package com.parse.buzzbox;

import java.util.LinkedList;
import java.util.List;

import phone_numbers.To_international;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DispatchActivity extends SherlockActivity{
	public DispatchActivity() {
	  }

	private Context con;
	private LinkedList<String> valid_name = new LinkedList<String>();
	private LinkedList<String> valid_number = new LinkedList<String>();
	String names[], numbers[], object_ids[];
	private int index;
	AlertDialog alertDialog;
	private int flag;
	private SharedPreferences sp_parse;
	private SharedPreferences.Editor prefEdit;
	private int no_of_errors = 0;
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    con = this;
	    sp_parse = getSharedPreferences("App_data", Activity.MODE_PRIVATE);
		prefEdit = sp_parse.edit();
		
		//retrieving contacts from local db
		Contacts_database db = new Contacts_database(con);
		List<Contact> already_present = db.getAllContacts();
		
		names = new String[already_present.size()]; 
		numbers = new String[already_present.size()];
		object_ids = new String[already_present.size()];
		
		for(int i=0;i<already_present.size();i++)
		{
			names[i] = already_present.get(i).getName();
			numbers[i] = already_present.get(i).getPhoneNumber();
			object_ids[i] = already_present.get(i).getObjectID();
		}
	    if (ParseUser.getCurrentUser() != null) {
	    	
	    	String str = sp_parse.getString("first_time","shubham");
	    	//check if it is the first time launch of app
			  if(str.equals("shubham"))
			  {
				  setContentView(R.layout.looking_for_friends);
				  setSupportProgressBarIndeterminateVisibility(true);
				  chekcAllPhoneNumbers();
				  
			  }
			  else
			  {
				// Start an intent for the logged in activity
			      startActivity(new Intent(this, MainActivity.class));
			      finish();
			  }
				  
	      
	    } else {
	      // Start and intent for the logged out activity
	      startActivity(new Intent(this, SignupOrLogin.class));
	      finish();
	    }

	  }
	//this method checks all phone numbers 
		//and add new users who are registered on app to database
		public void chekcAllPhoneNumbers() {
			
			
				Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
				String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				                ContactsContract.CommonDataKinds.Phone.NUMBER};

				Cursor people = getContentResolver().query(uri, projection, null, null, null);

				int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);


								
				int total_size = people.getCount();
				if(total_size<=0)
				{
					//if the total contacts are 0
						Toast.makeText(con, "Friends checking complete.", Toast.LENGTH_SHORT).show();
						prefEdit.putString("first_time", "blah");
						prefEdit.commit();
						startActivity(new Intent(con, DispatchActivity.class));
					      finish();
				}
				else
				{
					
					people.moveToFirst();
					do {
						    String name   = people.getString(indexName);
						    String number = people.getString(indexNumber);
						    //to check phone number is valid or not
						    number = (new To_international(con)).change_to_international(number);
						    if(number!=null)
						    {
						    	valid_name.add(name);
						    	valid_number.add(number);
						    }
					} while (people.moveToNext());

					//check each contact
					if(valid_name.size()>10)
					{
						flag = valid_name.size();
						index = -1;
						for(int i=0;i<valid_name.size();i++)
						{
							parse_phone_number_query();
						}
					}
					else
					{
						flag = valid_name.size();
						index = -1;
						for(int i=0;i<valid_name.size();i++)
						{
							parse_phone_number_query();
						}
					}

				}
								
		}
		
		//makes query for each phone number
		public void parse_phone_number_query()
		{
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			index++;
			query.whereEqualTo("phone_number", valid_number.get(index));
			query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				// TODO Auto-generated method stub
				
				
				flag--;
				if(flag==0)
				{
					setSupportProgressBarIndeterminateVisibility(false);
					// if no of errors are less than 10 percent we can let user move in main activity
					if(no_of_errors<valid_name.size()*10/100)
					{
						Toast.makeText(con, "Friends checking complete.", Toast.LENGTH_SHORT).show();
						prefEdit.putString("first_time", "blah");
						prefEdit.commit();
						startActivity(new Intent(con, DispatchActivity.class));
					      finish();
					}
					else
					{
						Toast.makeText(con, "Error while checking. Please check your internet connection.", Toast.LENGTH_SHORT).show();
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								con);
				 
								// create alert dialog
					  			alertDialog = alertDialogBuilder.setTitle("Error while checking.")
									.setMessage("Do you want to retry?")
									.setCancelable(false)
									.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											alertDialog.dismiss();
											startActivity(new Intent(con, DispatchActivity.class));
										      finish();
										}
									  })
									  .setNegativeButton("Cancel", new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											finish();
										}
									}).create();
				 
								// show it or
								alertDialog.show();
					}
				}
				if (e == null) {
			        // The query was successful.
					//new user found
					
					if(objects.size()!=0)
					{
						
						int flag=0;
						//got the number using buzzbox
						String phone_number = objects.get(0).getString("phone_number");
						String object_id = objects.get(0).getObjectId();
						for(int temp = 0;temp <numbers.length;temp++)
						{
							if(numbers[temp].equals(phone_number))
								{
									flag=1;
									Log.d("User", "Already Present");
								}
						}
						if(flag==0)
						{
							Contacts_database db = new Contacts_database(con);
							int index = valid_number.indexOf(phone_number);
							Contact contact = new Contact(valid_number.get(index), valid_name.get(index),object_id);
							db.addContact(contact);
						}
						
					}
					else
					{
						Log.d("user not present","asdsa");
					}
			    } else {
			        Log.d("error", e.getMessage());
			        no_of_errors++;
			    }
			}
		  });
		}
}
