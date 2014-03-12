package com.ucsd.cs110w.group16.placeits;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlaceItDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_NAME = "placeits";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_CATEGORY = "category";
	public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE,
		COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_STATUS, COLUMN_CATEGORY};
	
	private static final String DATABASE_NAME = "placeits.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_LATITUDE + " REAL, "
			+ COLUMN_LONGITUDE + " REAL, "
			+ COLUMN_NAME + " TEXT NOT NULL, "
			+ COLUMN_DESCRIPTION + " TEXT NOT NULL, "
			+ COLUMN_STATUS + " INTEGER,"
			+ COLUMN_CATEGORY+ " TEXT NOT NULL)";
	private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	
	public PlaceItDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Upgrading db from old version; this deletes contents of the old table, but
		// we could transfer the old data into the new table if we wanted to.
		db.execSQL(SQL_DELETE_TABLE);
		onCreate(db);
	}
}

