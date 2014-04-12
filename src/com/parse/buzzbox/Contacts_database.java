package com.parse.buzzbox;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Contacts_database extends SQLiteOpenHelper {

	
	
	private static final int DATABASE_VERSION = 2;
	
	private static final String DATABASE_NAME = "contacts_db";
	
	private static final String TABLE = "contacts";
	
	private static final String KEY_ID = "id";
	private static final String PHONE_NO = "phone_number";
	private static final String NAME = "name";
	private static final String OBJECT_ID = "object_id";
	public Contacts_database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + PHONE_NO + " TEXT,"
				+ NAME + " TEXT," +OBJECT_ID + " TEXT" +")";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);

		// Create tables again
		onCreate(db);
	}

	
	void addContact(Contact contact) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(PHONE_NO, contact.getPhoneNumber()); // phone_number
		values.put(NAME, contact.getName());// name
		values.put(OBJECT_ID, contact.getObjectID());// Object ID
		// Inserting Row
		db.insert(TABLE, null, values);
		db.close(); // Closing database connection
	}

	
//	Contact getContact(int id) {
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		String selectQuery = "SELECT * FROM animals where animalId='"+id+"'";
//	    Cursor cursor = db.rawQuery(selectQuery, null);
//		if (cursor != null)
//			cursor.moveToFirst();
//
//		Simple_notification noti = new Simple_notification(Integer.parseInt(cursor.getString(0)),
//				cursor.getString(1), cursor.getString(2),  cursor.getString(4), cursor.getString(3), cursor.getInt(5), cursor.getString(6));
//		// return noti
//		cursor.close();
//		db.close();
//		return noti;
//	}
	
	public List<Contact> getAllContacts() {
		List<Contact> contacts = new ArrayList<Contact>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Contact cont = new Contact(
						cursor.getString(1), cursor.getString(2),cursor.getString(3));
				
				// Adding noti to list
				contacts.add(cont);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		// return notis list
		return contacts;
	}


//	// Deleting single noti
//	public void deleteContact(Simple_notification noti) {
//		SQLiteDatabase db = this.getWritableDatabase();
//		String deleteQuery = "DELETE FROM  animals where animalId='"+ noti.getID() +"'";
//	    db.execSQL(deleteQuery);
//	    db.close();
//	}


//	// Getting contacts Count
//	public int getContactsCount() {
//		String countQuery = "SELECT  * FROM " + TABLE;
//		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor = db.rawQuery(countQuery, null);
//		int count = cursor.getCount();
//		cursor.close();
//		db.close();
//		// return count
//		return count;
//	}

}


