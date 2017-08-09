package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private ImageView mSearchButton;
    private TextInputEditText mSearchText;
    private BookAdapter mAdapter;
    private String mQueryText;
    private ExpandableListView mListView;
    private static final String GOOGLE_BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        mSearchButton = (ImageView) findViewById(R.id.search_icon);
        mSearchText = (TextInputEditText) findViewById(R.id.search_field);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchText.clearFocus();
                mEmptyView.setText("");
                mEmptyView.setVisibility(View.GONE);
                launchQuery(mSearchText.getText().toString());
            }
        });

//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                boolean handled = false;
//                if (i == EditorInfo.IME_ACTION_SEND) {
//                    mEmptyView.setVisibility(View.GONE);
//                    launchQuery(mSearchText.getText().toString());
//                    handled = true;}
//                return handled;
//            }
//        });
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, GOOGLE_BOOKS_REQUEST_URL + mQueryText);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        ExpandableListAdapter adapter = new BookAdapter(this, new ArrayList<Book>());
        mListView.setAdapter(adapter);

        if (books != null && !books.isEmpty()) {
            mAdapter = new BookAdapter(this, (ArrayList<Book>) books);
            mListView.setAdapter(mAdapter);
        }
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setText(R.string.empty_view_text);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        ExpandableListAdapter adapter = new BookAdapter(this, new ArrayList<Book>());
        mListView.setAdapter(adapter);
    }

    private void launchQuery(String searchText) {
        mQueryText = searchText;

        if (searchText != null && searchText != "") {

            if (isOnline()) {
                mProgressBar.setVisibility(View.VISIBLE);

                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(BOOK_LOADER_ID, null, this);

                mListView = (ExpandableListView) findViewById(R.id.list);
                mListView.setEmptyView(mEmptyView);

                mAdapter = new BookAdapter(this, new ArrayList<Book>());

                mListView.setAdapter(mAdapter);

                mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                        Log.d("onGroupClick:", "worked");
                        expandableListView.expandGroup(i);
                        return false;
                    }
                });

            }
            else {
                mProgressBar.setVisibility(View.GONE);
                mEmptyView.setText(R.string.no_connection);
            }
        }
        else{
            Toast.makeText(this, "You need to introduce some text to search", Toast.LENGTH_LONG);
        }
    }
}