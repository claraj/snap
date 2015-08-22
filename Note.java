package com.example.hello.inspirationboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class Note extends InspirationItem {

	private String mText;
	private Date dateCreated;
	private Date dateLastMod;

	private int previewLengthChars = 100;

	public Note(String text, Date created, Date lastMod) {
		mText = text;
		dateCreated = created;
		dateLastMod = lastMod;
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
		noteDateCreate.setText(dateCreated.toString());


		//TODO Last mod

		return noteView;


	}
}
