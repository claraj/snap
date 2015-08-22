package com.example.hello.inspirationboard;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by admin on 8/22/15.
 */
public class ListDataProvider implements ListAdapter{


	//TOOD this must talk to DB and get items for list in correct order


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
		return 1;
		//TODO
	}

	@Override
	public Object getItem(int position) {

		//TODO
		return "testing";

	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		//TODO must talk to DB.

		InspirationItem item = mDb.getItemForPosition(position);

		return item.getView(mAppContext, parent);



	}

	@Override
	public int getItemViewType(int position) {
		return IGNORE_ITEM_VIEW_TYPE;
	}

	@Override
	public int getViewTypeCount() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	//Will talk to DB or whatever and return a list of Views



}
