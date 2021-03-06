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



	//TODO use SQLite Date functions to store dates more effectively


	//Todo ignore case for filter


	//todo

	private final static String TAG = "DATABASE MANAGER";

	private static Context mContext;
	private static SQLHelper helper;
	private static SQLiteDatabase db;
	private static final String DB_NAME = "inspiration_items";
	private static final int DB_VERSION = 8;

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

	//Singleton class

	private static final DatabaseManager INSTANCE = new DatabaseManager();

	public static DatabaseManager getInstance(Context c) {

		mContext = c;

		if (helper == null ) {
			helper = new SQLHelper(c);
		}
		if (db == null) {
			db = helper.getWritableDatabase();
		}
		return INSTANCE;
	}


	public void close() {
		helper.close();
	}


	public Picture getPicture(long id) {

		//SELECT * FROM PICTURES WHERE ID = ID

		db = helper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(PICTURE_ID_COL, id);

		String where = PICTURE_ID_COL + " = " + Long.toString(id);

		Cursor cursor = db.query(PICTURE_TABLE, null, where, null, null, null, null);

		Picture picture = null;

		if (cursor.getCount() == 0 ) {
			Log.e(TAG, "no picture found in DB for id " + id);
		}

		else if (cursor.getCount() == 1 ) {

			//cool - found exactly one picture
			cursor.moveToFirst();

			String uri = cursor.getString(1);
			String create = cursor.getString(2);
			String mod = cursor.getString(3);
			String tags = cursor.getString(4);

			picture = new Picture(id, uri, create, mod, tags);
		}

		else {
			//more than one picture returned for this id
			Log.e(TAG, "ERROR more than one picture for ID " + id);
		}

		cursor.close();
		db.close();

		return picture;   /// So will return null if 0 or more than 1 picture found.

	}



	public Note getNote(long id) {

		//SELECT * FROM NOTES WHERE ID = ID

		db = helper.getReadableDatabase();

		ContentValues values = new ContentValues();
		values.put(NOTE_ID_COL, id);

		String where = NOTE_ID_COL + " = " + Long.toString(id);

		Cursor cursor = db.query(NOTES_TABLE, null, where, null, null, null, null);

		Note note = null;

		if (cursor.getCount() == 0 ) {
			Log.e(TAG, "no note found in DB for id " + id);
		}

		else if (cursor.getCount() == 1 ) {

			//cool - found exactly one note
			cursor.moveToFirst();

			String text = cursor.getString(1);
			String create = cursor.getString(2);
			String mod = cursor.getString(3);

			note = new Note(id, text, create, mod);
		}

		else {
			//more than one picture returned for this id
			Log.e(TAG, "ERROR more than one picture for ID " + id);
		}

		cursor.close();
		db.close();

		return note;   /// So will return null if 0 or more than 1 picture found.

	}


	public InspirationItem getItemForPosition(int position, String filter) {

		//Formulate a query. Fetch everything from all tables, sort in date order, return position-th item.
		//Query: select * from notes; select * from notes table
		//Optionally filter the results

		//TODO there MUST be SQL that does this for me.  some kind of unions? The goal is to get all the data, sort by date, return position'th item.

		db = helper.getReadableDatabase();

		ArrayList<InspirationItem> allData = new ArrayList<>();

		String noteWhereClause = null;
		String pictureWhereClause = null;

		if (filter != null) {
			noteWhereClause = NOTE_TEXT_COL + " LIKE '%" + filter + "%'";
			pictureWhereClause = PICTURE_HASHTAGS_COL + " LIKE '%" + filter + "%'";
		}

		Cursor noteCursor = db.query(NOTES_TABLE, null, noteWhereClause, null, null, null, null);

		int id; String text, create, mod;
		noteCursor.moveToFirst();
		while (noteCursor.isAfterLast() == false) {
			id = noteCursor.getInt(0);
			text = noteCursor.getString(1);
			create = noteCursor.getString(2);
			mod = noteCursor.getString(3);
			Note note = new Note(id, text, create, mod);
			Log.i(TAG, "Read this note from DB: " + note.toString());

			noteCursor.moveToNext();
			allData.add(note);
		}


		Cursor pictureCursor = db.query(PICTURE_TABLE, null, pictureWhereClause, null, null, null, null);

		String tags, uri;
		pictureCursor.moveToFirst();
		while (pictureCursor.isAfterLast() == false) {
			id = pictureCursor.getInt(0);
			uri = pictureCursor.getString(1);
			create = pictureCursor.getString(2);
			mod = pictureCursor.getString(3);
			tags = pictureCursor.getString(4);

			Picture picture = new Picture(id, uri, create, mod, tags);

			pictureCursor.moveToNext();
			allData.add(picture);

		}

		noteCursor.close();
		pictureCursor.close();
		db.close();


		Collections.sort(allData);

		try {
			return allData.get(position);
		} catch (IndexOutOfBoundsException ie ) {
			Log.e(TAG, "Fetching element " + position + " from this dataset" + allData);
			return null;
		}

	}


//RETURN the rowid aka primary key

	public long addNote(Note note){

		this.db = helper.getWritableDatabase();

		ContentValues newNote = new ContentValues();
		newNote.put(NOTE_TEXT_COL, note.getText());
		newNote.put(NOTE_DATE_CREATE_COL, note.getDateCreatedAsString());
		newNote.put(NOTE_DATE_LAST_MOD_COL, note.getDateModifiedAsString());

		long id = -1;

		try {
			id = db.insertOrThrow(NOTES_TABLE, null, newNote);
		} catch (SQLException sqle) {
			Log.e(TAG, "Error inserting " + note + " into database", sqle);
		}

		db.close();

		return id;
	}


	//returns -1 if insert fails, also log message.
	public long addPicture(Picture picture) {


		db = helper.getWritableDatabase();

		ContentValues newPictureData = new ContentValues();
		newPictureData.put(PICTURE_DATE_CREATE_COL, picture.getDateCreatedAsString());
		newPictureData.put(PICTURE_DATE_LAST_MOD_COL, picture.getDateModifiedAsString());
		newPictureData.put(PICTURE_URI_COL, picture.getUriAsString());
		newPictureData.put(PICTURE_HASHTAGS_COL, picture.getHashtagsAsString());

		long id = -1;

		try {
			id = db.insertOrThrow(PICTURE_TABLE, null, newPictureData);
		} catch (SQLException sqle ) {
			Log.e(TAG, "fail on insert picture: " + picture.toString(), sqle);
		}

	//	cacheValid = false;
		db.close();
		return id;


	}

	public int getInspirationItemCount(String filter) {

		this.db = helper.getReadableDatabase();


		if (filter == null) {

			String countNotes = "SELECT Count(*) from " + NOTES_TABLE;
			String countPictures = "SELECT Count(*) from " + PICTURE_TABLE;


			Cursor noteCountCursor = db.rawQuery(countNotes, null);
			noteCountCursor.moveToFirst();
			int noteCount = noteCountCursor.getInt(0);  //TODO TEST

			Cursor picCountCursor = db.rawQuery(countPictures, null);
			picCountCursor.moveToFirst();
			int pictureCount = picCountCursor.getInt(0);  //TODO TEST

			Log.i(TAG, "Note count " + noteCount + " pic count " + pictureCount);

			close();

			noteCountCursor.close();
			picCountCursor.close();

			return (pictureCount + noteCount);

		}  else {

			String countNotes = "SELECT Count(*) from " + NOTES_TABLE + " WHERE " + NOTE_TEXT_COL + " LIKE '%" + filter + "%'";
			Log.i(TAG, "Count notes query with filter of " + filter + " is " + countNotes);
			String countPictures = "SELECT Count(*) from " + PICTURE_TABLE + " WHERE " + PICTURE_HASHTAGS_COL + " LIKE '%" + filter + "%'";
			;


			Cursor noteCountCursor = db.rawQuery(countNotes, null);
			noteCountCursor.moveToFirst();
			int noteCount = noteCountCursor.getInt(0);

			Cursor picCountCursor = db.rawQuery(countPictures, null);
			picCountCursor.moveToFirst();
			int pictureCount = picCountCursor.getInt(0);

			Log.i(TAG, "FILTERED Note count " + noteCount + " pic count " + pictureCount);

			noteCountCursor.close();
			picCountCursor.close();
			close();

			return (pictureCount + noteCount);


		}

	}

	public void updateNote(Note updated) {

		//Find notes with this ID in database and change its text, also update modified date.

		this.db = helper.getWritableDatabase();

		ContentValues newNoteData = new ContentValues();
		newNoteData.put(NOTE_TEXT_COL, updated.getText());
		newNoteData.put(NOTE_DATE_LAST_MOD_COL, updated.getDateModifiedAsString());

		String whereClause = NOTE_ID_COL + " = " + Long.toString(updated.mDatabaseID);

		Log.i(TAG, "updating " + updated.toString() + " " + whereClause);

		int rowsUpdated = db.update(NOTES_TABLE, newNoteData, whereClause, null );

		close();

	}

	public void delete(InspirationItem item) {

		db = helper.getWritableDatabase();

		long db_id = item.mDatabaseID;

		//TODO a method call here?

		if (item instanceof Note) {


			ContentValues deleteNoteData = new ContentValues();
			deleteNoteData.put(NOTE_ID_COL, db_id);

			String whereClause = NOTE_ID_COL + " = " + Long.toString(db_id);  //alternative: stringformat

			Object result = db.delete(NOTES_TABLE, whereClause, null);
			Log.i(TAG, result.toString());

		//	cacheValid = false;

			close();
		}
		//SQL needed: DELETE FROM NOTESTABLE WHERE ID = WHATEVERITIS

		else if (item instanceof Picture) {

			String whereClause = PICTURE_ID_COL + " = " + Long.toString(db_id);  //alternative: stringformat

			db.delete(PICTURE_TABLE, whereClause, null);

		//	cacheValid = false;

			close();


		}

		else {
			Log.e(TAG, "shouldn't be here, unknown type for which delete has not been implemented");
		}

	}

	public void updatePicture(Picture updateMe) {

		//Update picture hashtags and date modified

		this.db = helper.getWritableDatabase();

		ContentValues newPictureData = new ContentValues();
		newPictureData.put(PICTURE_HASHTAGS_COL, updateMe.getHashtagsAsString());
		newPictureData.put(PICTURE_DATE_LAST_MOD_COL, updateMe.getDateModifiedAsString());

		String whereClause = PICTURE_ID_COL + " = " + Long.toString(updateMe.mDatabaseID);

		Log.i(TAG, "updating " + updateMe.toString() + " " + whereClause);

		int rowsUpdated = db.update(PICTURE_TABLE, newPictureData, whereClause, null );

	//	cacheValid = false;
		close();




	}


	//Inner class
	static class SQLHelper extends SQLiteOpenHelper {

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