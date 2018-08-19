package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;

public class BookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    BookCursorAdapter mAdapter;
    private static final int LOADER_ID = 1;
    static final String[] BOOK_PROJECTION = new String[]{BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_AUTHOR, BookEntry.COLUMN_GENRE, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_QUANTITY_IN_STOCK};
    static final String BOOK_SORT_ORDER = BookEntry.COLUMN_BOOK_NAME + " ASC ";


    public BookFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.book_item_list, container, false);
        // Find the ListView which will be populated with the book data
        ListView bookListView = (ListView) view.findViewById(R.id.book_list);
        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        mAdapter = new BookCursorAdapter(getContext(), null);
        // Attach the adapter to the ListView.
        bookListView.setAdapter(mAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
                Uri currentBookDetUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentBookDetUri);
                startActivity(intent);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getContext(), BookEntry.CONTENT_URI,
                BOOK_PROJECTION, null, null, BOOK_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
