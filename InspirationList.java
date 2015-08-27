package com.example.hello.inspirationboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


//TODO Way to search (search bar at top of list)  TODO searchview instead of edittext
//TODO Needs to be in fragments BECAUSE tablet view is different to phone BUT this prototype is for a phone (so far)
//TODO show modified dates in ListView
//TODO delete picture
//TODO Back button saves note too




public class InspirationList extends ActionBarActivity {


	private static String TAG = "Inspiration list main class";

	private static int NEW_NOTE_REQUEST_CODE = 1;
	private static int VIEW_EDIT_NOTE_REQUEST_CODE = 2;
	private static int NEW_PICTURE_REQUEST_CODE = 3;


	public static String NOTE_DB_ID = "id primary key from database";
	public static String NOTE_CREATE_DATE = "date note created";   //TODO remove these and just send ID,
	public static String NOTE_TEXT = "note's text";
	public static String EDIT_EXISTING_NOTE = "edit existing? ";


	public static String PICTURE_DB_ID = "id primary key from db for picture";


	private Uri pictureUri;


;	//TODO progress bar while list loads

	//TODO load list asynchronously

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

		configureSearchFeatures();

	}

	private void configureSearchFeatures() {

		final SearchView searchBox = (SearchView) findViewById(R.id.search_box);

		searchBox.setOnSearchClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				if (searchBox.getQuery() == null || searchBox.getQuery().toString().length() == 0) {
					return;
				}

				//else search!

				ArrayList<InspirationItem> matches = mDatabaseManager.search (searchBox.getQuery().toString());

				//Do something...  //TODO do we modify what adapter displays?
				//TODO must have back button returning to original list
				//TODO must have a cancel or show all button




			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
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

					//TODO just send the ID and have the activity fetch the rest of stuff from the DB.

					Intent editViewNote = new Intent(InspirationList.this, AddNoteActivity.class);
					editViewNote.putExtra(NOTE_DB_ID, item.mDatabaseID);
					editViewNote.putExtra(NOTE_CREATE_DATE, item.mDateCreated);
					editViewNote.putExtra(NOTE_TEXT, ((Note) item).getText());

					editViewNote.putExtra(EDIT_EXISTING_NOTE, true);
					startActivityForResult(editViewNote, VIEW_EDIT_NOTE_REQUEST_CODE);
				}

				if (item instanceof Picture) {

					//TODO save modified hashtags

					Intent editViewPicture = new Intent(InspirationList.this, ViewPictureActivity.class);
					editViewPicture.putExtra(PICTURE_DB_ID, item.mDatabaseID);
					startActivity(editViewPicture);


				}
			}
		});

		//Indicate that the list view should display a context menu on long-press
		registerForContextMenu(mInspirationList);





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
		InspirationItem item = mListAdapter.getItem(listPosition);
		Log.i(TAG, item.toString() );
		//get Inspiration which corresponds to this ID

		//TODO should work for Note and Picture - currently just Note //FIXME
		mDatabaseManager.delete(item);

		refreshList();

	}

	private void configureDatabase() {
		//TODO - anything else?

		mDatabaseManager = DatabaseManager.getInstance(this);

	}


	protected void refreshList(){

		//replaces the list adapter. TODO this *can't* be the right way... can it?

		//TODO this can't be how you do this correctly.
		mInspirationList.setAdapter(new ListDataProvider(this, mDatabaseManager));

		//TODO Try this...???? OR replace with ArrayLAdapter ???
		//mInspirationList.invalidateViews();



	}


	private void addNote(){

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

			//Update list with latest content;

			refreshList();

			Log.i(TAG, "Added note:" + newNote.toString());
		}


		else if (request == VIEW_EDIT_NOTE_REQUEST_CODE && result == RESULT_OK) {

			//Should have
			// new text for a current note. Must modify and update correct note in DB

			Bundle b = data.getExtras();
			long noteID = b.getLong(NOTE_DB_ID);
			Date created = (Date)b.getSerializable(NOTE_CREATE_DATE);
			String text = b.getString(NOTE_TEXT);

			Note updated = new Note(noteID, text, created, new Date());
			mDatabaseManager.updateNote(updated);

			refreshList();


		}


		else if (request == NEW_PICTURE_REQUEST_CODE && result == RESULT_OK) {

			//Bitmap picture = (Bitmap) data.getExtras().get("data");

			//pictureUri = data.getParcelableExtra(MediaStore.EXTRA_OUTPUT);

			Picture newPicture = new Picture(pictureUri, new Date(), new Date(), null);

			Log.i(TAG, "on activity result picture uri is " + pictureUri);

			//Add to database

			long newPictureID = mDatabaseManager.addPicture(newPicture);

			refreshList();

			//Start EditPictureActivity for user to view picture and add hashtags, if desired

			Intent viewPicture = new Intent(InspirationList.this, ViewPictureActivity.class);

			//Add the DB Id and launch viewPicture
			viewPicture.putExtra(PICTURE_DB_ID, newPictureID);

			startActivity(viewPicture);

		}

		else {
			refreshList();
		}

	}


	private void addPicture () {
		Log.i(TAG, "Add picture button click");

		//Specify filename
		//Use a UUID plus current date/time

		UUID uuid = UUID.randomUUID();
		String filename = "InspirationBoard_ " + new Date().toString() + "_" + uuid.toString() + ".jpg" ;


		//remove suspect chars
		filename = filename.replace(":", "-");
		while (filename.contains(" ")) {
			filename = filename.replace(" ", "-");
		}


		Log.i(TAG, "Will save this picture to " + filename);

		//TODO look into different types of directories returned here.
		File file = new File(Environment.getExternalStorageDirectory(), filename);



		//TODO save original size *and* thumbnail to cut down on resizing when list is being drawn?
		pictureUri = Uri.fromFile(file);

		Log.i(TAG, "in addPicture. The picture Uri is" + pictureUri.toString());

		Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		takePicture.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);  //save my picture here plz

		startActivityForResult(takePicture, NEW_PICTURE_REQUEST_CODE);





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
