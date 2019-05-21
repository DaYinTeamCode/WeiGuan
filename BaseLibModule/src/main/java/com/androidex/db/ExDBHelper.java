package com.androidex.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class ExDBHelper{
	
	public static final String FIELD_ID = "id";
	public static final String FIELD__ID = "_id";	
	
	public static final int OPTION_SUCCESS = 0;
	public static final int OPTION_FAILED = -1;
	public static final int OPTION_REPEAT = -2;
	public static final int OPTION_EXCEPTION = -3;
	public static final int OPTION_NO_FOUND = -4;

	private DBHelper mDBHelper;

	protected ExDBHelper(Context context, String dbName, int version) {
		
		mDBHelper = new DBHelper(context, dbName, version);
	}

	public SQLiteDatabase getWritableDatabase() {
		
		return mDBHelper.getWritableDatabase();
	}

	public SQLiteDatabase getReadableDatabase() {
		
		return mDBHelper.getReadableDatabase();
	}

	protected SQLiteDatabase getTransactionDatabase() {
		
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		db.beginTransaction();
		return db;
	}

	public void closeCusrsor(Cursor cursor) {
		
		try{
			
			if (cursor != null)
				cursor.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void closeStatement(SQLiteStatement statement) {
		
		try{
			
			if (statement != null)
				statement.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	protected void endTransaction(SQLiteDatabase db) {
		
		try{
			
			if (db != null) {
				db.endTransaction();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void closeDB(SQLiteDatabase db) {
		
		try{
			
			if (db != null)
				db.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void closeDataBase() {
		
		try {
			
			mDBHelper.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeCursorAndDB(Cursor cursor, SQLiteDatabase db) {
		
		try{
			
			if (cursor != null)
				cursor.close();

			if (db != null)
				db.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void closeStatementAndDB(SQLiteStatement statement, SQLiteDatabase db) {
		
		try{
			
			if (statement != null)
				statement.close();

			if (db != null)
				db.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void endTransactionAndCloseDB(SQLiteDatabase db) {
		
		try{
			
			if (db != null) {
				db.endTransaction();
				db.close();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onCreate(SQLiteDatabase db) {
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private class DBHelper extends SQLiteOpenHelper {

		protected DBHelper(Context context, String dbName, int version) {
			super(context, dbName, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			ExDBHelper.this.onCreate(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			ExDBHelper.this.onUpgrade(db, oldVersion, newVersion);
		}
	}
}
