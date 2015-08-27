package com.example.hello.inspirationboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by admin on 8/22/15.
 */

public class DatabaseManager {


	private final String TAG = "DATABASE MANAGER";

	private Context mContext;
	private static SQLHelper helper;
	private static SQLiteDatabase db;
	private static final String DB_NAME = "inspiration_items";
	private static final int DB_VERSION = 3;

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


	private boolean cacheValid = false;
	ArrayList<InspirationItem> allInspirationsCache = new ArrayList<>();


	public DatabaseManager(Context c) {
		mContext = c;
		helper = new SQLHelper(mContext);
		this.db = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}


	public InspirationItem getItemForPosition(int position) {


		this.db = helper.getWritableDatabase();


		if (cacheValid == false) {    //if cache is NOT valid...


			Log.i(TAG, "cache invalid, recreating arraylist");

			allInspirationsCache = new ArrayList<>();  //wipe the cache

			//Formulate a query. Fetch everything from all tables, sort in date order, return position-th item.

			//Query: select * from notes; select * from notes table

			Cursor noteCursor = db.query(NOTES_TABLE, null, null, null, null, null, null);

			int id; String text, create, mod;
			noteCursor.moveToFirst();
			while (noteCursor.isAfterLast() == false) {
				id = noteCursor.getInt(0);
				text = noteCursor.getString(1);
				create = noteCursor.getString(2);
				mod = noteCursor.getString(3);
				Note note = new Note(id, text, create, mod);
				Log.i(TAG, "adding Note to cache " + note.toString());
				allInspirationsCache.add(note);
				noteCursor.moveToNext();
			}


			Cursor pictureCursor = db.query(PICTURE_TABLE, null, null, null, null, null, null);

			String tags, uri;
			pictureCursor.moveToFirst();
			while (pictureCursor.isAfterLast() == false) {
				id = pictureCursor.getInt(0);
				uri = pictureCursor.getString(1);
				create = pictureCursor.getString(2);
				mod = pictureCursor.getString(3);
				tags = pictureCursor.getString(4);

				Picture picture = new Picture(id, uri, create, mod, tags);
				allInspirationsCache.add(picture);

				pictureCursor.moveToNext();
			}


			Collections.sort(allInspirationsCache);   //Sort by date order

			cacheValid = true;

		}


		close();

		try {
			return allInspirationsCache.get(position);
		}  catch (ArrayIndexOutOfBoundsException ae) {
			Log.e(TAG, "requesting item not in cache" + position + allInspirationsCache.size() ,  ae);
			return null;
		}
	}



	public void addInspirationItem() {
		//TODO
		//What table to add it to? Need two separate methods for picture, note?

		cacheValid = false;  //need to rebuild cache

	}


	public void addNote(Note note){

		//SQL query to add new note

		this.db = helper.getWritableDatabase();



		ContentValues newNote = new ContentValues();
		newNote.put(NOTE_TEXT_COL, note.getText());
		newNote.put(NOTE_DATE_CREATE_COL, note.getDateCreatedAsString());
		newNote.put(NOTE_DATE_LAST_MOD_COL, note.getDateModifiedAsString());

		try {
			db.insertOrThrow(NOTES_TABLE, null, newNote);

			cacheValid = false;

		} catch (SQLException sqle) {
			Log.e(TAG, "Error inserting " + note + " into database", sqle);
			//TODO don't fail silently
		}

		close();

	}


	public void addPicture(Picture picture) {

		cacheValid = false;
	//TODO
	}

	public int getInspirationItemCount() {

		this.db = helper.getWritableDatabase();


		if (cacheValid == false) {

			String countNotes = "SELECT Count(*) from " + NOTES_TABLE;
			String countPictures = "SELECT Count(*) from " + PICTURE_TABLE;

			Cursor noteCountCursor = db.rawQuery(countNotes, null);
			noteCountCursor.moveToFirst();
			int noteCount =  noteCountCursor.getInt(0);  //TODO TEST

			Cursor picCountCursor = db.rawQuery(countPictures, null);
			picCountCursor.moveToFirst();
			int pictureCount =  picCountCursor.getInt(0);  //TODO TEST

			Log.i(TAG, "Note count " + noteCount + " pic count " + pictureCount);

			close();

			return (pictureCount + noteCount);


		}
		else {
			close();
			return allInspirationsCache.size();
		}
	}

	public void updateNote(Note updated) {

		//Find notes with this ID in database and change it's text and modified date.

		this.db = helper.getWritableDatabase();

		ContentValues newNoteData = new ContentValues();
		newNoteData.put(NOTE_TEXT_COL, updated.getText());
		newNoteData.put(NOTE_DATE_LAST_MOD_COL, updated.getDateModifiedAsString());

		String whereClause = NOTE_ID_COL + " = " + Integer.toString(updated.mDatabaseID);

		Log.i(TAG, "updating " + updated.toString() + " " + whereClause);

		int rowsUpdated = db.update(NOTES_TABLE, newNoteData, whereClause, null );

		cacheValid = false;
		close();

	}

	public void delete(InspirationItem item) {

		//What type of thing is this?

		db = helper.getWritableDatabase();

		int db_id = item.mDatabaseID;

		//TODO a method call here.

		if (item instanceof Note) {


			ContentValues deleteNoteData = new ContentValues();
			deleteNoteData.put(NOTE_ID_COL, db_id);

			String whereClause = NOTE_ID_COL + " = " + Integer.toString(db_id);  //alternative: stringformat

			Object result = db.delete(NOTES_TABLE, whereClause, null);
			Log.i(TAG, result.toString());

			cacheValid = false;

			close();
		}
		//SQL needed: DELETE FROM NOTESTABLE WHERE ID = WHATEVERITIS

		else if (item instanceof Picture) {

			String whereClause = PICTURE_ID_COL + " = " + Integer.toString(db_id);  //alternative: stringformat

			db.delete(PICTURE_TABLE, whereClause, null);

			cacheValid = false;

			close();


		}

		else {
			Log.e(TAG, "shouldn't be here, unknown type for which delete has not been implemented");
		}

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
			String createNotesSQL = String.format("CREATE TABLE %s ( %s integer primary key autoincrement not null, %s string not null, %s string not null, %s string not null)",
					NOTES_TABLE, NOTE_ID_COL, NOTE_TEXT_COL, NOTE_DATE_CREATE_COL, NOTE_DATE_LAST_MOD_COL);

			db.execSQL(createNotesSQL);


			String createPicturesSQL = String.format("CREATE TABLE %s ( %s integer primary key autoincrement not null, %s string not null, %s string not null, %s string not null, %s string)",
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