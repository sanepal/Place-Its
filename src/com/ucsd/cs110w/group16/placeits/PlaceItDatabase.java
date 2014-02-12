package com.ucsd.cs110w.group16.placeits;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PlaceItDatabase {

	private SQLiteDatabase database;
	private PlaceItDatabaseHelper dbHelper;
	
	public PlaceItDatabase(Context context) {
		dbHelper = new PlaceItDatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public PlaceIt createPlaceIt(double latitude, double longitude, String name,
			String description, boolean active) {
		
		// Add values to key/value pair object.
		ContentValues values = new ContentValues();
		values.put(PlaceItDatabaseHelper.COLUMN_LATITUDE, latitude);
		values.put(PlaceItDatabaseHelper.COLUMN_LONGITUDE, longitude);
		values.put(PlaceItDatabaseHelper.COLUMN_NAME, name);
		values.put(PlaceItDatabaseHelper.COLUMN_DESCRIPTION, description);
		values.put(PlaceItDatabaseHelper.COLUMN_STATUS, active);
		
		// Insert values into database and get the ID.
		long insertID = database.insert(PlaceItDatabaseHelper.TABLE_NAME, null, values);
		
		// Retrieve the new entry from the database.
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME,
				PlaceItDatabaseHelper.ALL_COLUMNS, PlaceItDatabaseHelper.COLUMN_ID + "=" + insertID,
				null, null, null, null);
		
		// Convert the new entry to a PlaceIt object.
		cursor.moveToFirst();
		PlaceIt placeIt = cursorToPlaceIt(cursor);
		cursor.close();
		
		return placeIt;
	}
	
	public void updatePlaceIt(PlaceIt placeIt) {
		// Fill-in the key/value pairs, including the ID.
		ContentValues values = new ContentValues();
		values.put(PlaceItDatabaseHelper.COLUMN_ID, placeIt.getID());
		values.put(PlaceItDatabaseHelper.COLUMN_LATITUDE, placeIt.getLatitude());
		values.put(PlaceItDatabaseHelper.COLUMN_LONGITUDE, placeIt.getLongitude());
		values.put(PlaceItDatabaseHelper.COLUMN_NAME, placeIt.getName());
		values.put(PlaceItDatabaseHelper.COLUMN_DESCRIPTION, placeIt.getDescription());
		values.put(PlaceItDatabaseHelper.COLUMN_STATUS, placeIt.isActive());
		
		// Update the values in the database.
		database.replace(PlaceItDatabaseHelper.TABLE_NAME, null, values);
	}
	
	public void deletePlaceIt(PlaceIt placeIt) {
		long id = placeIt.getID();
		
		// Delete the object with matching ID from the database.
		database.delete(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.COLUMN_ID + " = " + id, null);
	}
	
	public PlaceIt getPlaceIt(long id) {
		// Retrieve the object with matching ID from the database.
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
				PlaceItDatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null);
		
		cursor.moveToFirst();
		PlaceIt placeIt = cursorToPlaceIt(cursor);
		cursor.close();
		
		return placeIt;
	}
	
	public List<PlaceIt> getAllActive() {
		return getAllWithStatus(true);
	}
	
	public List<PlaceIt> getAllInactive() {
		return getAllWithStatus(false);
	}
	
	private List<PlaceIt> getAllWithStatus(boolean status) {
		List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		
		// Return all rows with matching status value.
		int value = status ? 1 : 0;
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
				PlaceItDatabaseHelper.COLUMN_STATUS + "=" + value, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PlaceIt placeIt = cursorToPlaceIt(cursor);
			placeIts.add(placeIt);
			cursor.moveToNext();
		}
		cursor.close();
		
		return placeIts;
	}
	
	private PlaceIt cursorToPlaceIt(Cursor cursor) {
		// Create new object and fill with values from the database.
		PlaceIt placeIt = new PlaceIt();
		placeIt.setID(cursor.getLong(0));
		placeIt.setPosition(cursor.getDouble(1), cursor.getDouble(2));
		placeIt.setName(cursor.getString(3));
		placeIt.setDescription(cursor.getString(4));
		placeIt.setStatus(cursor.getInt(5) == 1);
		return placeIt;
	}
}

