package com.example.hello.inspirationboard;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class AddEditViewNoteActivity extends AddInspirationActivity {


	private static final String TAG = "AddEditViewNote";
	Note mNote;
	DatabaseManager dbMananger;
	EditText textEntered;

	private boolean modifying;


	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);

		dbMananger = DatabaseManager.getInstance(this);

		long noteId = getIntent().getLongExtra(InspirationList.NOTE_DB_ID, -1);

		textEntered = (EditText) findViewById(R.id.new_note_edittext);
		TextView originalCreateDateTV = (TextView) findViewById(R.id.date_note_created);
		TextView modifiedDateTV = (TextView) findViewById(R.id.date_note_modified);
		Button saveButton = (Button) findViewById(R.id.save_note_button);


		if (noteId == -1) {

			modifying = false;
			//We are are creating new note. Clear everything, set create date/mod date to now
			mNote = new Note(noteId, "", new Date(), new Date());
			textEntered.setText("");
			originalCreateDateTV.setText(getString(R.string.created) + mNote.getDateCreatedAsString());
			modifiedDateTV.setText(getString(R.string.modified) + "");
			//leave modified date blank

		} else {

			modifying = true;
			//Displaying data from existing note
			mNote = dbMananger.getNote(noteId);
			textEntered.setText(mNote.getText());
			originalCreateDateTV.setText(getString(R.string.created) + mNote.getDateCreatedAsString());
			modifiedDateTV.setText(getString(R.string.modified) + mNote.getDateModifiedAsString());

		}


		saveButton.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					//save and return to calling activity

					saveNote();
					setResult(RESULT_OK);
					Log.i(TAG, "save pressed, saving and exiting, note is " + mNote);

					finish();

				}
		});


		Button cancelButton = (Button) findViewById(R.id.cancel_note_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					textEntered.setText("");   //clear screen //TODO avoid final declaration?
					setResult(RESULT_CANCELED);
					finish();
				}
		});



	}

	@Override
	public void onBackPressed(){

		saveNote();
		//setResult(RESULT_OK);
		Log.i(TAG, "on back pressed, saving and exiting, note is " + mNote);

		finish();
	}


	protected void saveNote() {

		mNote.setDateLastModified(new Date());
		mNote.setText(textEntered.getText().toString());

		if (modifying) {
			dbMananger.updateNote(mNote);
		} else {
			//add new note - so long as text is not null and is not empty.
			if (mNote.getText() != null && !mNote.getText().equals("")) {
				dbMananger.addNote(mNote);
			}
		}

	}


}
