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


	private Context mAppContext;

	ListDataProvider(Context appContext) {
		mAppContext = appContext;
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
		TextView testing = new TextView(mAppContext);  testing.setText("testing");
		return testing;
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
