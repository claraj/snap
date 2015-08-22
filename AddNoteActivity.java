package com.example.hello.inspirationboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class AddNoteActivity extends AddInspirationActivity {


	public final static String NEW_NOTE_TEXT = "New note text from add note activity";

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);


		final EditText textEntered = (EditText) findViewById(R.id.new_note_edittext);

		//Add listener

		//This will be started as a StartActivityForResult.... perhaps this should make requests to add to DB?

		//TODO Save button OR back button must save text as new note


		Button saveButton = (Button) findViewById(R.id.save_note_button);

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//save and return to calling activity

				String noteText = textEntered.getText().toString();

				//Note newNote = new Note(noteText, new Date(), new Date());

				Intent returnNote = new Intent();
				returnNote.putExtra(NEW_NOTE_TEXT, noteText);

				setResult(RESULT_OK, returnNote);
				finish();

			}
		});


		Button cancelButton = (Button) findViewById(R.id.cancel_note_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				textEntered.setText("");   //clear screen
				setResult(RESULT_CANCELED);
				finish();
			}
		});


	}






}
