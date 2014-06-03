package com.parse.buzzbox;

import java.util.LinkedList;
import java.util.List;

import phone_numbers.To_international;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Quite simple activity to create private message object as per the defined message object..
public class Private_message extends SherlockActivity{
	
	private Context con;
	private LinkedList<String> valid_name = new LinkedList<String>();
	private LinkedList<String> valid_number = new LinkedList<String>();
	String names[], numbers[], object_ids[];
	SharedPreferences sp_parse;
	SharedPreferences.Editor prefEdit;
	private int remaining= -9999999;
	private int index;
	AlertDialog alertDialog;
	private int flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setTitle("Private Message");
		setContentView(R.layout.private_message);
		con = this;
		load_contacts();
}
	
	//this method checks all phone numbers 
	//and add new users who are registered on app to database
	public void chekcAllPhoneNumbers() {
		
		if(remaining!=-9999999 && remaining!=0)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					con);
	 
					// create alert dialog
		  			alertDialog = alertDialogBuilder.setTitle("Please wait")
						.setMessage("Already an update is going on.")
						.setCancelable(false)
						.setPositiveButton("Ok.",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								alertDialog.dismiss();
							}
						  }).create();
	 
					// show it
					alertDialog.show();
		}
		else
		{
			
			Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
			                ContactsContract.CommonDataKinds.Phone.NUMBER};

			Cursor people = getContentResolver().query(uri, projection, null, null, null);

			int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
			int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

			int total_size = people.getCount();
			if(total_size<=0)
			{
				setSupportProgressBarIndeterminateVisibility(false);
				Toast.makeText(con, "No contacts found.", Toast.LENGTH_SHORT).show();
			}
			else
			{
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
				
				if(valid_name.size()>0)
				{
					if(valid_name.size()>10)
					{
						flag = valid_name.size();
						remaining = valid_name.size();
						index = -1;
						for(int i=0;i<valid_name.size();i++)
						{
							parse_phone_number_query();
						}
					}
					else
					{
						flag = valid_name.size();
						remaining = valid_name.size();
						index = -1;
						for(int i=0;i<valid_name.size();i++)
						{
							parse_phone_number_query();
						}
					}
				}
				else
				{
					setSupportProgressBarIndeterminateVisibility(false);
					Toast.makeText(con, "No friends found in contacts.", Toast.LENGTH_LONG).show();
					
				}
			}
			

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
		//list view contacts
				final ListView lv = (ListView)findViewById(R.id.contact_list);
				//retrieving contacts from local db
				Contacts_database db = new Contacts_database(con);
				List<Contact> already_present = db.getAllContacts();
				
				if(already_present.size()<=0)
				{
					Toast.makeText(con, "No friends found in contacts. 'Update friends' option present in option menu.", Toast.LENGTH_LONG).show();
				}
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
				
				// contact list item clicked
				lv.setOnItemClickListener(new OnItemClickListener() {
					 
		            
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
				               final int position, long id) {
						
							 Intent i = new Intent(Private_message.this, Create_Message.class);
							 Log.d("obj id", object_ids[position]);
							 i.putExtra("obj_id", object_ids[position]);
							 i.putExtra("viaPost", "");
							 startActivity(i);
					}

		       }); 

	}
	
	public void parse_phone_number_query()
	{
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		index++;
		remaining--;
		Log.d("remaining and index", remaining+" "+index);
		query.whereEqualTo("phone_number", valid_number.get(index));
		query.findInBackground(new FindCallback<ParseUser>() {
		@Override
		public void done(List<ParseUser> objects, ParseException e) {
			// TODO Auto-generated method stub
			
			if(remaining==0)
				{
					//setSupportProgressBarIndeterminateVisibility(false);
					load_contacts();
				}
			else
			{
				parse_phone_number_query();
			}
			flag--;
			if(flag==0)
			{
				setSupportProgressBarIndeterminateVisibility(false);
				load_contacts();
			}
			if (e == null) {
		        // The query was successful.
				//new user found
				
				if(objects.size()!=0)
				{
					
					if(objects.get(0).getList("friends")!=null)
					{
						ParseUser.getCurrentUser().addAllUnique("friends_of_friends", objects.get(0).getList("friends"));
						ParseUser.getCurrentUser().saveEventually();
					}
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
						load_contacts();
					}
					
				}
		    } else {
		        Log.d("error", e.getMessage());
		    }
			if(flag==0)
			{
				
				//to save all friends in list in parse user
				for(int i=0;i<object_ids.length;i++)
					ParseUser.getCurrentUser().addUnique("friends", object_ids[i]);
					ParseUser.getCurrentUser().saveEventually();
			}
		}
	  });
	}
	
	@Override
	  protected void attachBaseContext(Context newBase) {
	      super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	  }
}
