package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SupplierFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SupplierFragment.class.getSimpleName();

    @BindView(R.id.supplier_list)
    ListView supplierListView;
    @BindView(R.id.supplier_empty_view)
    TextView supplierEmptyView;
    @BindView(R.id.fab_supplier)
    FloatingActionButton fabSupplier;

    private Unbinder unbinder;

    private Uri mCurrentBookDetUri;

    SupplierCursorAdapter mAdapter;
    private static final int LOADER_ID = 2;
    static final String[] SUPPLIER_PROJECTION = new String[]{SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID, SupplierEntry.COLUMN_SUPPLIER_NAME, SupplierEntry.COLUMN_PHONE, SupplierEntry.COLUMN_ADDRESS};
    static final String SUPPLIER_SORT_ORDER = SupplierEntry.COLUMN_SUPPLIER_NAME + " ASC ";


    public SupplierFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.supplier_item_list, container, false);
        // Find the ListView which will be populated with the supplier data
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            try {
                Long currentBookDetUri = Long.parseLong(getArguments().get("currentBookDetId").toString());
                mCurrentBookDetUri = ContentUris.withAppendedId(DeliveryEntry.CONTENT_URI_WITH_BOOK, currentBookDetUri);
                Log.e(LOG_TAG, "mCurrentBookDetUri " + mCurrentBookDetUri);
                fabSupplier.setVisibility(View.GONE);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "Cannot parse URI for book details. " + e.toString());
            }
        }

        fabSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SupplierEditor.class);
                startActivity(intent);
            }
        });

        supplierListView.setEmptyView(supplierEmptyView);

        // Setup an Adapter to create a list item for each row of supplier data in the Cursor.
        mAdapter = new SupplierCursorAdapter(getContext(), null);
        // Attach the adapter to the ListView.
        supplierListView.setAdapter(mAdapter);

        supplierListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SupplierEditor.class);
                Uri currentSupplierUri = ContentUris.withAppendedId(SupplierEntry.CONTENT_URI, id);
                Log.e(LOG_TAG, "Supplier clicked " + id);
                intent.setData(currentSupplierUri);
                startActivity(intent);
            }
        });

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
        if (mCurrentBookDetUri == null) {
            return new CursorLoader(getContext(), SupplierEntry.CONTENT_URI,
                    SUPPLIER_PROJECTION, null, null, SUPPLIER_SORT_ORDER);
        } else{
            return new CursorLoader(getContext(), mCurrentBookDetUri,
                    SUPPLIER_PROJECTION, null, null, SUPPLIER_SORT_ORDER);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
