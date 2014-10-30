package com.foxycode.testapp.DataBase.Provider;


import android.content.ContentProvider;

import com.foxycode.testapp.DataBase.DatabaseHelper;

public abstract class BaseContentProvider extends ContentProvider {

	protected DatabaseHelper mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = DatabaseHelper.getInstance(getContext());
		return (mDbHelper != null);
	}

}