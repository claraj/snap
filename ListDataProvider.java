package com.example.hello.inspirationboard;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by admin on 8/22/15.
 */
public class ListDataProvider implements ListAdapter{


	//TODO this must talk to DB and get items for list in correct order

	private final String TAG = "ListDataProvider";

	private Context mAppContext;
	private DatabaseManager mDb;

	ListDataProvider(Context appContext, DatabaseManager db) {
		mAppContext = appContext;
		mDb = db;
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

		//ASK database how many things


		int itemCount = mDb.getInspirationItemCount();

		Log.i(TAG, "Counting list, this many items " + itemCount);

		return itemCount;


		//TODO
	}

	@Override
	public Object getItem(int position) {


		Log.i(TAG, "getting item for position " + position);

		return  mDb.getItemForPosition(position);

		//TODO
		//return "testing";   //todo what is this for and how diff from getView? Presumably this will want to return an InspirationItem.


	}

	@Override
	public long getItemId(int position) {
		return position;  //todo
	}

	@Override
	public boolean hasStableIds() {   //TODO what is this for?
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//TODO must talk to DB.

		Log.i(TAG, "getting view for position " + position);


		InspirationItem item = mDb.getItemForPosition(position);

		Log.i(TAG, "this view is " + item.toString());


		return item.getView(mAppContext, parent);

		//Suggest

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

		if (mDb.getInspirationItemCount() == 0 ) {
			return true;
		}
		return false;
	}





}
