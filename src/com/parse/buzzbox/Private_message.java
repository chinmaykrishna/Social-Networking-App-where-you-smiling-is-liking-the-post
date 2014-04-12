package com.parse.buzzbox;

import java.util.LinkedList;
import java.util.List;

import phone_numbers.To_international;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Private_message extends SherlockActivity{
	
	private int find_threads = 0;
	private Context con;
	private LinkedList<String> valid_name = new LinkedList<String>();
	private LinkedList<String> valid_number = new LinkedList<String>();
	String names[], numbers[], object_ids[];
	SharedPreferences sp_parse;
	SharedPreferences.Editor prefEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.private_message);
		con = this;
		
		//Update contacts database First time the app launches
		sp_parse = getSharedPreferences("App_data", Activity.MODE_PRIVATE);
		prefEdit = sp_parse.edit();
		String str = sp_parse.getString("first_time","shubham");
		  if(str.equals("shubham"))
		  {
			  prefEdit.putString("first_time", "blah");
			  prefEdit.commit();
			  setSupportProgressBarIndeterminateVisibility(true);
			  chekcAllPhoneNumbers();
		  }
		  load_contacts();
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

		people.moveToFirst();
		do {
		    String name   = people.getString(indexName);
		    String number = people.getString(indexNumber);
		    number = (new To_international(con)).change_to_international(number);
		    if(number!=null)
		    {
		    	valid_name.add(name);
		    	valid_number.add(number);
		    }
		} while (people.moveToNext());
		
		Log.d("size", ""+valid_name.size());
		for(int i=0;i<valid_name.size();i++)
		{
			
			find_threads++;
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			query.whereEqualTo("phone_number", valid_number.get(i));
			query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> objects, ParseException e) {
				// TODO Auto-generated method stub
				find_threads--;
				if(find_threads==0)
					{
						setSupportProgressBarIndeterminateVisibility(false);
						load_contacts();
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
			    } else {
			        // Something went wrong.
			    	
			    }
			}
		  });
		}
		
	}
	
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu){
		
		
		com.actionbarsherlock.view.MenuInflater inf=new com.actionbarsherlock.view.MenuInflater(this);
			inf.inflate(R.menu.private_message_menu, menu);
    	return true;
     }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.update)
		{
			setSupportProgressBarIndeterminateVisibility(true);
			chekcAllPhoneNumbers();
		}
		 return true;
	}
	
	public void load_contacts()
	{
		//listview contacts
				final ListView lv = (ListView)findViewById(R.id.contact_list);
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
				//Setting array adapter to show all contacts that are using buzzbox
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				          android.R.layout.simple_list_item_1, android.R.id.text1, names);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(new OnItemClickListener() {
					 
		            
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
				               final int position, long id) {
						//Send message when user clicks on a contact
						final Dialog dialog = new Dialog(con);
						 dialog.setContentView(R.layout.new_message);
						 dialog.setTitle("New Message");
						 Button done_but = (Button) dialog.findViewById(R.id.done);
						 final EditText message = (EditText)dialog.findViewById(R.id.message);
						 
						 	//send button clicked
							 done_but.setOnClickListener(new OnClickListener() {

								 @Override
								 public void onClick(View v) {
									 //send function
									 if(message.getText().toString().trim().length()<1)
									 {
										 Toast.makeText(con, "Please enter a valid text", Toast.LENGTH_SHORT).show();
									 }
									 else
									 {
										 final ProgressDialog pdLoading = new ProgressDialog(con);
										 pdLoading.setMessage("Sending. please wait...");
									     pdLoading.show();
										 MessageObject new_Message = new MessageObject();
										 new_Message.toUserObjectID(object_ids[position]);
										 new_Message.setText(message.getText().toString().trim());
										 new_Message.setType("personal");
										 new_Message.setViaPost("blah");
										 new_Message.saveInBackground(new SaveCallback() {
											
											@Override
											public void done(ParseException e) {
												// TODO Auto-generated method stub
												pdLoading.dismiss();
												if(e==null)
												{
													Toast.makeText(con, "Successfully Sent", Toast.LENGTH_SHORT).show();
												}
												else
												{
													
													Log.d("error while sending", e.getMessage().toString());
													Toast.makeText(con, "Sending failed. Please check internet connection", Toast.LENGTH_SHORT).show();
												}
												
											}
										});
									 }
									 dialog.dismiss();
								 }
							 });
							 dialog.show();
//						
					}

		       }); 

	}
}
