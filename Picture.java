package com.example.hello.inspirationboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public class Picture extends InspirationItem {

	private final String TAG = "PICTURE class";
	private Uri mFileUri;
	private Hashtags mHashtags;

	private int mThumbnailWidth = 100;
	private int mThumbnailHeight = 100;

	static {

		//TODO read in dimensions for thumbnails from resource file replace these
		/*
	private int thumbnailWidth = 100;
	private int thumbnailHeight = 100;*/
	}


	public Picture(Uri uri, Date taken, Date modified, Hashtags tags) {

		mFileUri = uri;
		mDateCreated = taken;

		if (tags == null) {
			mHashtags = new Hashtags();
		} else {
			mHashtags = tags;
		}

		if (modified == null) {
			mDateLastModified = taken;
		} else {
			mDateLastModified = modified;
		}

	}


	public Picture(long id, Uri uri, Date taken, Date modified, Hashtags tags) {
		this(uri, taken, modified, tags);
		mDatabaseID = id;

	}

	public Picture(long id, String uriString, String takenString, String modString, String tagsString) {

		this.mDatabaseID = id;
		this.mFileUri = Uri.parse(uriString);

		try {
			this.mDateCreated = dateFormatter.parse(takenString);
			this.mDateLastModified = dateFormatter.parse(modString);
		} catch (ParseException pe) {
			Log.e(TAG, "Failed parsing date mod " + modString + " or date create " + takenString, pe );
		}

		this.mHashtags = new Hashtags(tagsString);

	}

	public String getUriAsString() {
		Log.i(TAG, "Returning the URI as this string " + mFileUri.toString() );
		return mFileUri.toString();

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
		//return null;


		LayoutInflater inflate = LayoutInflater.from(appContext);

		View picView = inflate.inflate(R.layout.picture_list_item, parent, false);  //TODO???

		//fill in fields.... thumbnail, hashtags, plus date created and mod  //TODO Last mod.

		TextView dateCreateText = (TextView)picView.findViewById(R.id.picture_create_date);
		dateCreateText.setText(getDateCreatedAsString());
		TextView dateModText = (TextView)picView.findViewById(R.id.picture_mod_date);
		dateModText.setText(getDateModifiedAsString());
		TextView hashtagsText = (TextView)picView.findViewById(R.id.hashtags);
		hashtagsText.setText(getHashtagsAsString());

		Log.i(TAG, "the hashtags are as follows:" + getHashtagsAsString());
		//TODO make hashtags scroll if too long???

		//get image thumbnail.... ThumbnailUtils !

		Bitmap imageThumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(getFileUri().getPath()), mThumbnailWidth, mThumbnailHeight);

		ImageView thumbNailImageView = (ImageView)picView.findViewById(R.id.image_thumbnail);

		thumbNailImageView.setImageBitmap(imageThumbnail);


		return picView;    //TODO consider memory here and


	}

	public Uri getFileUri() {
		return mFileUri;
	}

	public String getHashtagsAsString() {

		return mHashtags.toString();
	}

	public void setHashtags(String tags) {
		mHashtags = new Hashtags(tags);
	}
}
