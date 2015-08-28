package com.example.hello.inspirationboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 8/20/15.
 */
public abstract class InspirationItem implements Comparable<InspirationItem> {

	protected Date mDateLastModified;
	protected Date mDateCreated;
	protected long mDatabaseID;

	public static final String DATE_FORMAT_STRING =  "EEE MMM d yyyy @ HH:mm" ;

	protected static SimpleDateFormat dateFormatter;


	static {
		dateFormatter = new SimpleDateFormat();
		dateFormatter.applyPattern(DATE_FORMAT_STRING);

	}

	public String getDateCreatedAsString(){
		return dateFormatter.format(mDateCreated);
	}

	public String getDateModifiedAsString(){
		return dateFormatter.format(mDateLastModified);
	}


	public int compareTo(InspirationItem another) {

		//Sorting in date order. So, if another is earlier in time, return -1
		//If another is later in time, return +1
		//		If another is the same time, return 0;

		//Using Date.compareTo has the oldest things first. SO have to reverse sign.
		return -(this.mDateLastModified.compareTo(another.mDateLastModified));


	}


	public void setDateLastModified(Date lastModified) {
		mDateLastModified = lastModified;
	}

	public void setDateCreated(Date created) {
		mDateCreated = created;
	}


	public abstract View getView(Context appContext, ViewGroup parent);




}
