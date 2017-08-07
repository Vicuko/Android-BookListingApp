package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Vicuko on 8/8/17.
 */

public class BookLoader extends AsyncTaskLoader {

    private final String LOG_TAG = BookLoader.class.getName();
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Object loadInBackground() {
        if (mUrl == null || mUrl == "") {
            return null;
        }
        List<Book> result = QueryUtils.fetchBooksData(mUrl);
        return result;
    }
}