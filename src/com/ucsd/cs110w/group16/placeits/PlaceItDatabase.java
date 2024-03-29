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
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORY, false);
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORIES, (String)null);
		
		// Open the database
		open();
		
		// Insert values into database and get the ID.
		Integer insertID = (int) database.insert(PlaceItDatabaseHelper.TABLE_NAME, null, values);
		
		// Retrieve the new entry from the database.
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME,
				PlaceItDatabaseHelper.ALL_COLUMNS, PlaceItDatabaseHelper.COLUMN_ID + "=" + insertID,
				null, null, null, null);
		
		// Convert the new entry to a PlaceIt object.
		cursor.moveToFirst();
		PlaceIt placeIt = cursorToPlaceIt(cursor);
		cursor.close();
		
		// Close the database - should be in activity's onDelete instead?
		close();
		
		return placeIt;
	}
	
	// Create a category place it
	public PlaceIt createPlaceIt(String name,
			String description, boolean active, String categories) {
		// Add values to key/value pair object.
		ContentValues values = new ContentValues();
		values.put(PlaceItDatabaseHelper.COLUMN_LATITUDE, 0.);
		values.put(PlaceItDatabaseHelper.COLUMN_LONGITUDE, 0.);
		values.put(PlaceItDatabaseHelper.COLUMN_NAME, name);
		values.put(PlaceItDatabaseHelper.COLUMN_DESCRIPTION, description);
		values.put(PlaceItDatabaseHelper.COLUMN_STATUS, active);
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORY, true);
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORIES, categories);
		
		// Open the database
		open();
		
		// Insert values into database and get the ID.
		Integer insertID = (int) database.insert(PlaceItDatabaseHelper.TABLE_NAME, null, values);
		
		// Retrieve the new entry from the database.
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME,
				PlaceItDatabaseHelper.ALL_COLUMNS, PlaceItDatabaseHelper.COLUMN_ID + "=" + insertID,
				null, null, null, null);
		
		// Convert the new entry to a PlaceIt object.
		cursor.moveToFirst();
		PlaceIt placeIt = cursorToPlaceIt(cursor);
		cursor.close();
		
		// Close the database - should be in activity's onDelete instead?
		close();
		
		return placeIt;
	}
	
	public void updatePlaceIt(PlaceIt placeIt) {
		// Fill-in the key/value pairs, including the ID.
		ContentValues values = new ContentValues();
		values.put(PlaceItDatabaseHelper.COLUMN_ID, placeIt.getId());
		values.put(PlaceItDatabaseHelper.COLUMN_LATITUDE, placeIt.getLatitude());
		values.put(PlaceItDatabaseHelper.COLUMN_LONGITUDE, placeIt.getLongitude());
		values.put(PlaceItDatabaseHelper.COLUMN_NAME, placeIt.getTitle());
		values.put(PlaceItDatabaseHelper.COLUMN_DESCRIPTION, placeIt.getDesc());
		values.put(PlaceItDatabaseHelper.COLUMN_STATUS, placeIt.isActive());
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORY, placeIt.isCategory());
		values.put(PlaceItDatabaseHelper.COLUMN_CATEGORIES, placeIt.getCategories());
		
		// Update the values in the database.
		open();
		database.replace(PlaceItDatabaseHelper.TABLE_NAME, null, values);
		close(); // should be in activity's onDelete instead?
	}
	
	public void deletePlaceIt(PlaceIt placeIt) {
		Integer id = placeIt.getIntId();
		
		// Delete the object with matching ID from the database.
		open();
		database.delete(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.COLUMN_ID + " = " + id, null);
		close(); // should be in activity's onDelete instead?
	}
	
	public PlaceIt getPlaceIt(Long id) {
		// Open the database
		open();
		
		// Retrieve the object with matching ID from the database.
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
				PlaceItDatabaseHelper.COLUMN_ID + "=" + id, null, null, null, null);
		
		cursor.moveToFirst();
		PlaceIt placeIt = cursorToPlaceIt(cursor);
		cursor.close();

		close(); // should be in activity's onDelete instead?
		
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
		
		// Open the database
		open();
		
		// Return all rows with matching status value.
		int value = status ? 1 : 0;
		Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
				PlaceItDatabaseHelper.COLUMN_STATUS + "=" + value, null, null, null, PlaceItDatabaseHelper.COLUMN_ID+" DESC");
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			PlaceIt placeIt = cursorToPlaceIt(cursor);
			placeIts.add(placeIt);
			cursor.moveToNext();
		}
		cursor.close();

		close(); // should be in activity's onDelete instead?
		
		return placeIts;
	}
	
	public List<PlaceIt> getAllCategory() {
List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
        
        // Open the database
        open();
        
        // Return all rows with matching status value.
        Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
                PlaceItDatabaseHelper.COLUMN_CATEGORY + "= 1", null, null, null, PlaceItDatabaseHelper.COLUMN_ID+" DESC");
        
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PlaceIt placeIt = cursorToPlaceIt(cursor);
            placeIts.add(placeIt);
            cursor.moveToNext();
        }
        cursor.close();

        close(); // should be in activity's onDelete instead?
        
        return placeIts;
	}
	
	private PlaceIt cursorToPlaceIt(Cursor cursor) {
		// Create new object and fill with values from the database.
		PlaceIt placeIt = new PlaceIt(
		        cursor.getInt(0),
		        cursor.getString(3),
		        cursor.getString(4),
		        cursor.getDouble(1),
		        cursor.getDouble(2),
		        cursor.getInt(5) == 1,
		        cursor.getInt(6) == 1,
		        cursor.getString(7)
		        );
		return placeIt;
	}
	
	public void clear()
	{
		open();
		database.delete(PlaceItDatabaseHelper.TABLE_NAME, null, null);
		close();
	}
	
	public boolean exists(String t)
	{
		boolean status = false;
		// Open the database
		open();
		
		try
		{
		   // Retrieve the object with matching ID from the database.
		   Cursor cursor = database.query(PlaceItDatabaseHelper.TABLE_NAME, PlaceItDatabaseHelper.ALL_COLUMNS,
				PlaceItDatabaseHelper.COLUMN_NAME + "=" + t, null, null, null, null);
		
		   status = cursor.moveToFirst();
		   cursor.close();
		}
		catch(Exception e){}
		close(); // should be in activity's onDelete instead?
		
		return status;
	}
}

