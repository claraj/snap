package com.example.hello.inspirationboard;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;




//TODO Way to search (search bar at top of list)  TODO searchview instead of edittext
//TODO Way to delete (long press on list item + context menu)
//TODO Tap list item to view note or picture in new Activity
//TODO Add pictures and hashtags
//TODO Needs to be in fragments BECAUSE tablet view is different to phone BUT this prototype is for a phone (so far)
//TODO show modified dates in ListView
//TODO Back button saves note too




public class InspirationList extends ActionBarActivity {


	private static String TAG = "Inspiration list main class";

	private static int NEW_NOTE_REQUEST_CODE = 1;
	private static int VIEW_EDIT_NOTE_REQUEST_CODE = 2;
	private static int NEW_PICTURE_REQUEST_CODE = 3;


	public static String NOTE_DB_ID = "id primary key from database";
	public static String NOTE_CREATE_DATE = "date note created";
	public static String NOTE_TEXT = "note's text";
	public static String EDIT_EXISTING_NOTE = "edit existing? ";
;	//TODO progress bar while list loads

	private ListView mInspirationList;
	private DatabaseManager mDatabaseManager;

	private ListDataProvider mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspiration_list);

		configureDatabase();

	//	addTestData();

		configureListView();

	}

	private void addTestData() {

		Note test1 = new Note("Hello, I'm a test note", new Date(), new Date());
		Note test2 = new Note("A resfjdfgjkfgjkldflgkjdgfkjlgdjklgdjklfgdjkdover 100 characters I think sdfkljsdfjljlksdfjklsdklfjgkl;gdfkl;gdfjkldfgjkldgfjklHello, I'm a test note", new Date(), new Date());
		Note test3 = new Note("Another test", new Date(), new Date());

		mDatabaseManager.addNote(test1);
		mDatabaseManager.addNote(test2);
		mDatabaseManager.addNote(test3);

	}

	private void configureListView() {

		mInspirationList = (ListView)findViewById(R.id.inspiration_list);

		//Add footer view with add buttons - not any more, add buttons permanently to screen

		//View footerView = getLayoutInflater().inflate(R.layout.list_footer_view, null);
		//mInspirationList.addFooterView(footerView);

		Button addNote = (Button)findViewById(R.id.add_note_button);
		addNote.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "add note on click");
				InspirationList.this.addNote();
			}
		});

		Button addPicture = (Button)findViewById(R.id.add_picture_button);
		addPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "add picture on click");
				InspirationList.this.addPicture();
			}
		});

		//TODO Add header with search bar/searchview

		//Call adapter after setting footer/header

		mListAdapter = new ListDataProvider(this, mDatabaseManager);

		mInspirationList.setAdapter(mListAdapter);


		//Add click listener and long-press listener

		mInspirationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//open new Activity to display note
				InspirationItem item = mListAdapter.getItem(position);
				if (item instanceof Note) {
					//TODO open Note Activity for reading, editing.

					Intent editViewNote = new Intent(InspirationList.this, AddNoteActivity.class);
					editViewNote.putExtra(NOTE_DB_ID, item.mDatabaseID);
					editViewNote.putExtra(NOTE_CREATE_DATE, item.mDateCreated);
					editViewNote.putExtra(NOTE_TEXT, ((Note) item).getText());

					editViewNote.putExtra(EDIT_EXISTING_NOTE, true);
					startActivityForResult(editViewNote, VIEW_EDIT_NOTE_REQUEST_CODE);
				}

				if (item instanceof Picture) {
					//TODO picture Activity, view, edit hashtags etc.
				}
			}
		});

		/*mInspirationList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				return false;
				//TODO context menu to delete this item
			}
		});*/

		//Indicate that the list view should display a context menu on long-press
		registerForContextMenu(mInspirationList);





//		mInspirationList.setAdapter(new ListDataProvider(this, mDatabaseManager));

	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.inspiration_list_context_menu, menu);
	}

	//If the menu item is handled here, then return true. Otherwise, return false.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.delete_inspiration_menu:
				deleteItem(item.getItemId(), info.position);
				return true;
			default:
				return super.onContextItemSelected(item);
		}


	}


	protected void deleteItem(int itemId, int listPosition) {

		Log.i(TAG, "context menu click on " + itemId + " list position" + listPosition);


		InspirationItem item = mListAdapter.getItem(listPosition); //todo! this is NOT working
		//get Inspiration which corresponds to this ID

		mDatabaseManager.delete(item);


	}

	private void configureDatabase() {
		//TODO - anything else?

		mDatabaseManager = new DatabaseManager(this);

	}


	private void addNote(){
		//TODO

		Intent newNote = new Intent(this, AddNoteActivity.class);
		newNote.putExtra(EDIT_EXISTING_NOTE, false);

		startActivityForResult(newNote, NEW_NOTE_REQUEST_CODE);

	}


	@Override
	protected void onActivityResult(int request, int result, Intent data){

		if (request == NEW_NOTE_REQUEST_CODE && result == RESULT_OK) {

			//Extract text, generate new note, add to DB.

			String newNoteText = data.getExtras().getString(NOTE_TEXT);

			if (newNoteText == null || newNoteText.length() == 0) {
				Log.i(TAG, "new note text is blank or null");
				return;
			}

			Note newNote = new Note(newNoteText, new Date(), new Date());
			mDatabaseManager.addNote(newNote);

			//Tell adapter to update;
			//TODO this can't be how you do this correctly.
			mInspirationList.setAdapter(new ListDataProvider(this, mDatabaseManager));

			Log.i(TAG, "Added note:" + newNote.toString());
		}


		if (request == VIEW_EDIT_NOTE_REQUEST_CODE && result == RESULT_OK) {

			//Should have
			// new text for a current note. Must modify and update correct note in DB

			Bundle b = data.getExtras();
			int noteID = b.getInt(NOTE_DB_ID);
			Date created = (Date)b.getSerializable(NOTE_CREATE_DATE);
			String text = b.getString(NOTE_TEXT);

			Note updated = new Note(noteID, text, created, new Date());
			mDatabaseManager.updateNote(updated);

			//TODO this can't be how you do this correctly.
			mInspirationList.setAdapter(new ListDataProvider(this, mDatabaseManager));



		}

	}


	private void addPicture () {
		Log.i(TAG, "Add picture not implemented");
		//TODO
	}


	//Override to ensure DB is closed when user navigates away from app
	//TEST: this will happen when user views a note/picture. TODO Ensure it is re-opened to refresh list.
	@Override
	public void onPause(){
		super.onPause();
		mDatabaseManager.close();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_inspiration_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
