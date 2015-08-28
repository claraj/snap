package com.example.hello.inspirationboard;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin on 8/20/15.
 */
public class Hashtags {


	private static final String SEPARATOR_CHAR = " ";

	//Linkedlist of hashtags
	//Every hashtag should be an all one word string with no punctuation to make dividing easier


	private static String TAG = "Hashtags";
	private ArrayList<String> mHashtags;

	public Hashtags() {
		mHashtags = new ArrayList<>();
	}

	public Hashtags(String tagString) {

		mHashtags = new ArrayList<>();

		String[] tags = tagString.split(SEPARATOR_CHAR);


		if (tags != null && tags.length > 0) {
			for (String t : tags) {
				mHashtags.add(t);
		}
		}

	}

	@Override
	public String toString() {


		String tagString = "";

		if (mHashtags == null) {
			return tagString;  //so return empty string
		}

		for (String t : mHashtags) {
			tagString = tagString.concat(t);
			tagString = tagString.concat(SEPARATOR_CHAR);
		}

		//Remove last separator_char

		if (tagString.endsWith(SEPARATOR_CHAR)) {
			int cutChar = tagString.lastIndexOf(SEPARATOR_CHAR);
			tagString = tagString.substring(0, cutChar);
		}

		Log.i(TAG, "Hasttags string returned is " + tagString);

		return tagString;
	}

	public void addHashtag(String newTag) {
		mHashtags.add(newTag);
	}

	public void removeTag(String tag) {
		if (mHashtags.remove(tag) == false) {
			Log.e(TAG, "Hashtag list DOES NOT contain the Hashtag " + tag + " list not changed");
		};
	}

	public boolean contains(String tag) {
		return mHashtags.contains(tag);
	}

}
