package com.example.hello.inspirationboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class Note extends InspirationItem {

	private final String TAG = "NOTE class";
	private String mText;
	//private Date mDateCreated;
	//private Date dateLastMod;

	private int previewLengthChars = 100;

	public Note(String text, Date created, Date lastMod) {
		mText = text;
		mDateCreated = created;
		mDateLastModified = lastMod;
	}


	public Note(int id, String text, Date created, Date lastMod) {
		this(text, created, lastMod);
		mDatabaseID = id;

	}



	public Note(int id, String text, String created, String lastMod) {

		mText = text;

		mDatabaseID = id;

		try {
			mDateCreated = dateFormatter.parse(created);
			mDateLastModified = dateFormatter.parse(lastMod);
		} catch (ParseException pe) {
			Log.e(TAG, "Parse exception turning created or last mod into Date" + created + ", " + lastMod + " setting both to NULL");
			mDateLastModified = null;
			mDateCreated = null;
		}
	}


	public String getText() {
		return mText;
	}


	@Override
	public String toString() {
		return mText + " created: " + mDateCreated.toString() + "  modified  " + mDateLastModified.toString();
	}


	@Override
	public View getView(Context context, ViewGroup parent) {

		//Inflate from Note layout

		LayoutInflater inflate = LayoutInflater.from(context);

		View noteView = inflate.inflate(R.layout.note_list_item, parent);  //TODO???

		//fill in fields.... first 100 chars from text, plus date created  //TODO Last mod.

		TextView noteText = (TextView)noteView.findViewById(R.id.note_start_text);

		String previewChars = mText.substring(0, previewLengthChars);

		noteText.setText(previewChars);
		TextView noteDateCreate = (TextView)noteView.findViewById(R.id.note_create_date);
		noteDateCreate.setText(mDateCreated.toString());


		//TODO Last mod

		return noteView;


	}



}
