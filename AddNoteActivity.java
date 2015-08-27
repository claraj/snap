package com.example.hello.inspirationboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class AddNoteActivity extends AddInspirationActivity {

//TODO two different types of behavior -  editing existing OR creating new?


	//public final static String NEW_NOTE_TEXT = "New note text from add note activity";

	Date originalDate;
	int db_id;


	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);

		final EditText textEntered = (EditText) findViewById(R.id.new_note_edittext);

		//defaults to null so ok if new note
		String originalText = getIntent().getStringExtra(InspirationList.NOTE_TEXT);
		textEntered.setText(originalText);

		db_id = getIntent().getIntExtra(InspirationList.NOTE_DB_ID, 0);

		originalDate = (Date)getIntent().getSerializableExtra(InspirationList.NOTE_CREATE_DATE);
		if (originalDate == null ) {
			originalDate = new Date();
		}


		TextView originalCreateDateTV = (TextView) findViewById(R.id.date_note_created);


		String dateCreatedText = InspirationItem.dateFormatter.format(originalDate);
		originalCreateDateTV.setText(dateCreatedText);

		//Add listener

		//TODO Save button OR back button must save text as new note

		Button saveButton = (Button) findViewById(R.id.save_note_button);

		final boolean editingExisting = getIntent().getBooleanExtra(InspirationList.EDIT_EXISTING_NOTE, false);


		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//save and return to calling activity

				String noteText = textEntered.getText().toString();

				Intent returnNote = new Intent();
				returnNote.putExtra(InspirationList.NOTE_TEXT, noteText);
				returnNote.putExtra(InspirationList.NOTE_CREATE_DATE, originalDate);
				returnNote.putExtra(InspirationList.NOTE_DB_ID, db_id);

				setResult(RESULT_OK, returnNote);
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
