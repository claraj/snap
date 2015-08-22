package com.example.hello.inspirationboard;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin on 8/20/15.
 */
public class Hashtags {

	//Linkedlist of hashtags
	//Every hashtag should be an all one word string with no punctuation


	private static String TAG = "Hashtags";
	private ArrayList<String> mHashtags;

	public Hashtags() {
		mHashtags = new ArrayList<>();
	}

	public void addHashtag(String newTag) {
		mHashtags.add(newTag);
	}

	public void removeTag(String tag) {
		if (mHashtags.remove(tag) == false) {
			Log.e(TAG, "Hashtag list DOES NOT contain the Hashtag " + tag + " list not changed");
		};
	}

}
