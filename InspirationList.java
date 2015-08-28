package com.example.hello.inspirationboard;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.File;
import java.util.Date;
import java.util.UUID;



//TODO Needs to be in fragments BECAUSE tablet view is different to phone BUT this prototype is for a phone (so far)
//TODO show modified dates in ListView
//TODO clean up search functionality, such as "no items found" message, hide keyboard after user clicks search
//TODO Back button saves note too

//Tidy UI, organize components more neatly




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


	public static boolean searching = false;

	private Uri pictureUri;


;	//TODO progress bar while list loads

	//TODO load list asynchronously

	private ListView mInspirationList;
	private DatabaseManager mDatabaseManager;

	private ListDataProvider mListAdapter;

	private Button clearSearchButton;

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


		clearSearchButton = (Button) findViewById(R.id.clear_search_button);
		clearSearchButton.setVisibility(View.GONE);
		clearSearchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListAdapter.clearSearchString();
				Log.i(TAG, "clearing search string and hiding button");
				clearSearchButton.setVisibility(View.GONE);
			}
		});

		final SearchView searchBox = (SearchView) findViewById(R.id.search_box);

		searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i(TAG, "onquerytextchanged, submit: " + query);

				if (query == null) {
					return false;
				}
				if (query.length() == 0) {
					return false;
				}

				mListAdapter.setSearchString(query);
				refreshList();
				clearSearchButton.setVisibility(View.VISIBLE);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.i(TAG, "onquerytextchanged, : " + newText);
				//otherwise ignore
				return false;
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

					Intent editViewNote = new Intent(InspirationList.this, AddEditViewNoteActivity.class);
					editViewNote.putExtra(NOTE_DB_ID, item.mDatabaseID);
					editViewNote.putExtra(NOTE_CREATE_DATE, item.mDateCreated);
					editViewNote.putExtra(NOTE_TEXT, ((Note) item).getText());

					editViewNote.putExtra(EDIT_EXISTING_NOTE, true);
					startActivityForResult(editViewNote, VIEW_EDIT_NOTE_REQUEST_CODE);
				}

				if (item instanceof Picture) {

					//TODO save modified hashtags

					Intent editViewPicture = new Intent(InspirationList.this, ViewEditPictureActivity.class);
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

		mInspirationList.invalidateViews();

	}


	private void addNote(){

		Intent newNote = new Intent(this, AddEditViewNoteActivity.class);
		newNote.putExtra(EDIT_EXISTING_NOTE, false);

		startActivityForResult(newNote, NEW_NOTE_REQUEST_CODE);

	}


	@Override
	protected void onActivityResult(int request, int result, Intent data){

		if (request == NEW_PICTURE_REQUEST_CODE && result == RESULT_OK) {

			//More complex than note. After picture is taken, create new intent to launch
			//ViewPictureActivity so user can review and add hashtags.

			Picture newPicture = new Picture(pictureUri, new Date(), new Date(), null);

			Log.i(TAG, "on activity result picture uri is " + pictureUri);

			//Add to database

			long newPictureID = mDatabaseManager.addPicture(newPicture);

			refreshList();

			//Start EditPictureActivity for user to view picture and add hashtags, if desired

			Intent viewPicture = new Intent(InspirationList.this, ViewEditPictureActivity.class);

			//Add the DB Id and launch viewPicture
			viewPicture.putExtra(PICTURE_DB_ID, newPictureID);

			startActivity(viewPicture);
		}

		else {
			//Like a note was added or modified, or something....
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
		//TODO do we need this?
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
