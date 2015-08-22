package com.example.hello.inspirationboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by admin on 8/22/15.
 */

public class DatabaseManager {


	private final String TAG = "DATABASE MANAGER";

	private Context mContext;
	private static SQLHelper helper;
	private static SQLiteDatabase db;
	private static final String DB_NAME = "inspiration_items";
	private static final int DB_VERSION = 1;

	//Schema stuff - table names

	protected static final String NOTES_TABLE = "notes_table";
	protected static final String PICTURE_TABLE = "picture_table";

	//Schema stuff - column names for notes

	protected static final String NOTE_ID_COL = "note_id";
	protected static final String NOTE_TEXT_COL = "note_text";
	protected static final String NOTE_DATE_CREATE_COL = "note_date_create";
	protected static final String NOTE_DATE_LAST_MOD_COL = "note_date_last_mod";

	//column names for pictures

	protected static final String PICTURE_ID_COL = "picture_id";
	protected static final String PICTURE_DATE_CREATE_COL = "picture_date_create";
	protected static final String PICTURE_DATE_LAST_MOD_COL = "picture_date_last_mod";
	protected static final String PICTURE_URI_COL = "picture_uri";
	protected static final String PICTURE_HASHTAGS_COL = "picture_hashtags";


	public DatabaseManager(Context c) {
		mContext = c;
		helper = new SQLHelper(mContext);
		this.db = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	//Inner class
	class SQLHelper extends SQLiteOpenHelper {

		public SQLHelper(Context c) {
			super(c, DB_NAME, null, DB_VERSION); {

			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			//Create two tables... first for text

			//Example sql syntax... CREATE TABLE notes (id INT, notetext string) ;

			//String formatting makes life easier
			String createNotesSQL = String.format("CREATE TABLE %s ( %s int, %s string, %s string, %s string",
					NOTES_TABLE, NOTE_ID_COL, NOTE_TEXT_COL, NOTE_DATE_CREATE_COL, NOTE_DATE_LAST_MOD_COL);

			db.execSQL(createNotesSQL);


			String createPicturesSQL = String.format("CREATE TABLE %s ( %s int, %s string, %s string, %s string, %s string",
					PICTURE_TABLE, PICTURE_ID_COL, PICTURE_URI_COL, PICTURE_DATE_CREATE_COL, PICTURE_DATE_LAST_MOD_COL, PICTURE_HASHTAGS_COL);

			db.execSQL(createPicturesSQL);


		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			//Used to delete and re-create table

			db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + PICTURE_TABLE);

			onCreate(db);   //re-create all

			Log.i(TAG, "Deleted and re-recreated database tables");


		}
	}
}