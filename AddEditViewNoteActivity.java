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

//TODO two different types of behavior -  editing existing OR creating new?

//TODO rewrite this class will send updates to database


	Date originalDate;
	int db_id;

	Note mNote;

	DatabaseManager dbMananger;



	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);

		dbMananger = DatabaseManager.getInstance(this);

		long noteId = getIntent().getLongExtra(InspirationList.NOTE_DB_ID, -1);


		final EditText textEntered = (EditText) findViewById(R.id.new_note_edittext);
		TextView originalCreateDateTV = (TextView) findViewById(R.id.date_note_created);
		Button saveButton = (Button) findViewById(R.id.save_note_button);


		if (noteId == -1) {
			//We are are creating new note. Clear everything, set create date/mod date to now
			mNote = new Note(noteId, "", new Date(), new Date());
			textEntered.setText("");
			originalCreateDateTV.setText(mNote.getDateCreatedAsString());

		} else {

			//Displaying data from existing note
			mNote = dbMananger.getNote(noteId);
			textEntered.setText(mNote.getText());
			originalCreateDateTV.setText(mNote.getDateCreatedAsString());


			saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//save and return to calling activity
					saveNote();
					setResult(RESULT_OK);
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

	}

	@Override
	public void onBackPressed(){

		saveNote();
		setResult(RESULT_OK);
		finish();
	}


	protected void saveNote() {

		mNote.setDateLastModified(new Date());
		dbMananger.updateNote(mNote);

	}


}
