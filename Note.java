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
	private int maxRows = 5;

	private String indicateMore = "...";

	public Note(String text, Date created, Date lastMod) {
		mText = text;
		mDateCreated = created;
		mDateLastModified = lastMod;
	}


	public Note(long id, String text, Date created, Date lastMod) {
		this(text, created, lastMod);
		mDatabaseID = id;

	}



	public Note(long id, String text, String created, String lastMod) {

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

		View noteView = inflate.inflate(R.layout.note_list_item, parent, false);  //TODO???

		//fill in fields.... first 100 chars from text, plus date created. Layout controls max lines and adds ... if text doesn't fit.

		TextView noteText = (TextView)noteView.findViewById(R.id.note_start_text);



		noteText.setText(mText);
/*		if (mText.length() < previewLengthChars) {
			noteText.setText(mText);
		} else {
			String previewChars = mText.substring(0, previewLengthChars);
			previewChars = previewChars.concat(indicateMore);
			noteText.setText(previewChars);

		}
*/
		TextView noteDateCreate = (TextView)noteView.findViewById(R.id.note_create_date);
		noteDateCreate.setText(getDateCreatedAsString());

		TextView noteDateModified = (TextView)noteView.findViewById(R.id.note_mod_date);
		noteDateModified.setText(getDateModifiedAsString());



		//TODO Last mod

		return noteView;


	}


	public void setText(String text) {
		this.mText = text;
	}
}
