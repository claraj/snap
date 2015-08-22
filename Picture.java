package com.example.hello.inspirationboard;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class Picture extends InspirationItem {

	private final String TAG = "PICTURE class";
	private Uri mFileUri;
	private Date mDateTaken;
	private Hashtags mHashtags;

	public Picture(Uri uri, Date taken, Date modified, Hashtags tags) {

		mFileUri = uri;
		mDateTaken = taken;
		mHashtags = tags;

		if (modified == null) {
			mDateLastModified = taken;
		} else {
			mDateLastModified = modified;
		}

	}



	public Picture(int id, Uri uri, Date taken, Date modified, Hashtags tags) {
		this(uri, taken, modified, tags);
		mDatabaseID = id;

	}


	public Picture(int id, String uri, String created, String lastMod, String tags) {

		mFileUri = Uri.parse(uri);   //TODO error checking
		mDatabaseID = id;
		mHashtags = new Hashtags(tags);

		try {
			mDateCreated = dateFormatter.parse(created);
			mDateLastModified = dateFormatter.parse(lastMod);
		} catch (ParseException pe) {
			Log.e(TAG, "Parse exception turning created or last mod into Date" + created + ", " + lastMod + " setting both to NULL");
			mDateLastModified = null;
			mDateCreated = null;
		}
	}


	@Override
	public View getView(Context appContext, ViewGroup parent) {
		return null;
	}
}
