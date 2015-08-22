package com.example.hello.inspirationboard;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;


//List of notes, scrollable, in ListView, most recent first
//Way to add new notes (Hamburger menu? This opens new Activity *Ability to modify this* may need two+ mechanisms Gesture recognition on main screen?)
//Way to search (search bar at top of list)  TODO searchview instead of edittext
//Way to delete (long press on list item + context menu)
//Tap list item to view note or picture in new Activity
//Needs to be in fragments BECAUSE tablet view is different to phone BUT this prototype is for a phone

//Database of list items: notes, photos
//Schema:

//Notes table: text of note, date created
//Photos table: hashtags (space separated string), Uri of photo, date created

public class InspirationList extends ActionBarActivity {


	private static String TAG = "Inspiration list main class";

	//TODO progress bar while list loads

	private ListView mInspirationList;
	private DatabaseManager mDatabaseManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inspiration_list);


		//Populate the list
		//set up search handlers



		configureDatabase();


		addTestData();


		configureListView();
		populateList();



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


		//Add footer view with add button


		View footerView = getLayoutInflater().inflate(R.layout.list_footer_view, null);
		mInspirationList.addFooterView(footerView);

		//TODO event handlers for buttons

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
		mInspirationList.setAdapter(new ListDataProvider(this, mDatabaseManager));


	}

	private void populateList() {

		//TODO - this will come from a database




	}

	private void configureDatabase() {
		//TODO - what else?

		mDatabaseManager = new DatabaseManager(this);

	}


	private void addNote(){
		//TODO


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
