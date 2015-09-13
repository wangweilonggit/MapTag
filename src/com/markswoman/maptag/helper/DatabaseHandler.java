package com.markswoman.maptag.helper;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.markswoman.maptag.model.CustomMarker;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "map_tag";

	// Table Name
	private static final String TABLE_NAME = "custom_tags";

	/** Maximum Logging Count **/
	private static final int MAXIMUM_LOG_COUNT = 200;

	// Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
				+ KEY_DESCRIPTION + " TEXT," + KEY_LATITUDE + " TEXT,"
				+ KEY_LONGITUDE + " TEXT)";
		db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new Row
	public long addRow(CustomMarker marker) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = valuesFromObject(marker);

		// Inserting Row
		long new_id = db.insert(TABLE_NAME, null, values);
		db.close(); // Closing database connection
		return new_id;
	}

	/**
	 * Check whether the marker table size is bigger than
	 * {@link #MAXIMUM_LOG_COUNT}. If it's bigger, then delete the oldest one
	 * and add new one.
	 * 
	 * @param marker
	 *            CustomMarker object to be deleted
	 */
	public long addNewRow(CustomMarker marker) {
		if (getRowsCount() >= MAXIMUM_LOG_COUNT)
			deleteRow(getOldestRow());
		return addRow(marker);
	}

	// Getting the oldest task
	public CustomMarker getOldestRow() {

		String query = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_ID
				+ " ASC" + " LIMIT 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null)
			cursor.moveToFirst();

		CustomMarker marker = convertCursorToObject(cursor);
		cursor.close();
		db.close();

		return marker;
	}

	// Getting All Logged Tasks
	public ArrayList<CustomMarker> getAllRows() {
		ArrayList<CustomMarker> custom_markers = new ArrayList<CustomMarker>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				CustomMarker marker = convertCursorToObject(cursor);
				// Adding task to list
				custom_markers.add(marker);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return task list
		return custom_markers;
	}

	// Updating single task
	public int updateRow(CustomMarker marker) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = valuesFromObject(marker);
		// updating row
		return db.update(TABLE_NAME, values, KEY_ID + " = ?",
				new String[] { String.valueOf(marker.getID()) });
	}

	// Deleting single task
	public void deleteRow(CustomMarker marker) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KEY_ID + " = ?",
				new String[] { String.valueOf(marker.getID()) });
		db.close();
	}

	// Getting tasks Count
	public int getRowsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	private CustomMarker convertCursorToObject(Cursor cursor) {
		CustomMarker marker = new CustomMarker();
		marker.setID(cursor.getString(0));
		marker.setName(cursor.getString(1));
		marker.setDescription(cursor.getString(2));
		marker.setLatitude(Double.parseDouble(cursor.getString(3)));
		marker.setLongitude(Double.parseDouble(cursor.getString(4)));
		return marker;
	}

	private ContentValues valuesFromObject(CustomMarker marker) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, marker.getName());
		values.put(KEY_DESCRIPTION, marker.getDescription());
		values.put(KEY_LATITUDE, marker.getLatitude());
		values.put(KEY_LONGITUDE, marker.getLongitude());
		return values;
	}

	public void upgradeTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		db.close();
	}
}
