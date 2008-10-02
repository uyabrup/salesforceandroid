/**
 * Copyright (C) 2008 Dai Odahara.
 */
package com.android.salesforce.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.salesforce.util.StaticInformation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * This class is responsible for managing SQLite. At present, it does simply insert/update/delte.
 * 
 * TODO handle salesforce object relations
 * 
 * @author Dai Odahara
 * 
 */
public class SObjectSQLite {
	private static final String TAG = "SOjbectSQLite";
	private static final String DB_NAME = "SObject";
	private static final int DB_VERSION = 1;
	
	private static SQLiteDatabase db = null;
	
	public SObjectSQLite(){};
	
	/** create table */
	public void create (Context context, String table, String sobject) {
		try {

			db = context.openOrCreateDatabase(DB_NAME, DB_VERSION, null);
			db.execSQL("drop table if exists " + sobject);
			db.execSQL(table);
		} catch (Exception ex) {
			db = null;
			Log.v(TAG, ex.toString());
		}
	}
	
	/** create table */
	public void createKeyprefixSObject (Context context) {
		try {
			String tableName = "KeyPrefix_SObject";
			String table = "create table " + tableName + " (" 
				+ "keyPrefix text(" + StaticInformation.SOBJECT_PREFIX_SIZE + ") not null primary key, "
				+ "SObject text not null"
				+ ");";
			
			db = context.openOrCreateDatabase(DB_NAME, DB_VERSION, null);
			db.execSQL("drop table if exists " + tableName);
			db.execSQL(table);
		} catch (Exception ex) {
			db = null;
			Log.v(TAG, ex.toString());
		}
	}
	
	/** create table */
	public long insert(ContentValues insertData, String table) {
		return db.insert(table, null, insertData);
	}
	
	/** update table */
	public int update(ContentValues updateData, String table, String rowId){
		return db.update(table, updateData, "rowid = " + rowId, null);
	}
	
	/** read colmun - test */
	/**
	public List<Schedule> selectAll(String table) {
		List<Schedule> result = new ArrayList<Schedule>();
		Cursor cursor = db.query(table, null, null, null, null, null, "Name");
		while(cursor.moveToNext()) {
			Schedule s = new Schedule();
			s.Id = cursor.getString(0);
			s.Name = cursor.getString(1);
			s.Importance = cursor.getInt(2);
			result.add(s);
		}
		return result;
	}
	*/
	
	/** close the db */
	public void close() {
		db.close();
	}
}
