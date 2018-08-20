package com.example.android.bookstoreapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

public class DeliveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    DeliveryCursorAdapter mAdapter;
    private static final int LOADER_ID = 3;
    static final String[] DELIVERY_PROJECTION = new String[]{
            DeliveryEntry.TABLE_NAME + "." + DeliveryEntry._ID,
            DeliveryEntry.COLUMN_BOOK_ID,
            BookEntry.TABLE_NAME + "." + BookEntry._ID,
            BookEntry.COLUMN_BOOK_NAME,
            BookEntry.COLUMN_BOOK_AUTHOR,
            DeliveryEntry.COLUMN_SUPPLIER_ID,
            SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID,
            SupplierEntry.COLUMN_SUPPLIER_NAME,
            SupplierEntry.COLUMN_PHONE,
            DeliveryEntry.COLUMN_QUANTITY_DELIVERED,
            DeliveryEntry.COLUMN_DATE
    };

    static final String DELIVERY_SORT_ORDER = BookEntry.COLUMN_BOOK_NAME + " ASC, " +  DeliveryEntry.COLUMN_DATE + " DESC ";


    public DeliveryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delivery_item_list, container, false);
        // Find the ListView which will be populated with the supplier data
        ListView deliveryListView = (ListView) view.findViewById(R.id.delivery_list);
        // Setup an Adapter to create a list item for each row of supplier data in the Cursor.
        mAdapter = new DeliveryCursorAdapter(getContext(), null);
        // Attach the adapter to the ListView.
        deliveryListView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(getContext(), DeliveryEntry.CONTENT_URI,
                DELIVERY_PROJECTION, null, null, DELIVERY_SORT_ORDER);
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
