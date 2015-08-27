package com.example.hello.inspirationboard;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by admin on 8/22/15.
 */
public class ListDataProvider implements ListAdapter {
	//public class ListDataProvider extends ArrayAdapter<InspirationItem>{


	//TODO this must talk to DB and get items for list in correct order

	//TODO  "search mode ? "  If in search mode, displays different data from DB.


	private final String TAG = "ListDataProvider";

	private Context mAppContext;
	private DatabaseManager mDb;


	private String mSearchString;

	public ListDataProvider(Context appContext, DatabaseManager db) {
		mAppContext = appContext;
		mDb = db;
		mSearchString = null;
	}


	public void setSearchString(String search) {
		mSearchString = search;
	}

	public void clearSearchString() {
		mSearchString = null;
	}

	public String getSearchString(){
		return mSearchString;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public int getCount() {

		//Ask database how many things

		int itemCount = mDb.getInspirationItemCount(mSearchString);

		Log.i(TAG, "Counting list, this many items " + itemCount);

		return itemCount;

	}

	@Override
	public InspirationItem getItem(int position) {

		Log.i(TAG, "getting item for position " + position);
		return mDb.getItemForPosition(position, mSearchString);

	}

	@Override
	public long getItemId(int position) {
		return position;  //TODO what is this used for?
	}

	@Override
	public boolean hasStableIds() {   //TODO what is this for?
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Log.i(TAG, "getting view for position " + position);

//		InspirationItem item = mDb.getItemForPosition(position);
		InspirationItem item = mDb.getItemForPosition(position, mSearchString);


		Log.i(TAG, "this view is " + item.toString());

		return item.getView(mAppContext, parent);

	}

	@Override
	public int getItemViewType(int position) {
		return IGNORE_ITEM_VIEW_TYPE;     //TODO what was this for??


	}

	@Override
	public int getViewTypeCount() {   //TODO what? this is to do with different types of views e.g. Note and Picture. Look up how to use this.
		return 2;
	}

	@Override
	public boolean isEmpty() {

		if (mDb.getInspirationItemCount(mSearchString) == 0) {
			return true;
		}
		return false;
	}

}
