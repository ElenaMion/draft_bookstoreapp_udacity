package com.example.android.bookstoreapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DeliveryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DeliveryFragment.class.getSimpleName();

    @BindView(R.id.delivery_list)
    ListView deliveryListView;
    @BindView(R.id.delivery_empty_view)
    TextView deliveryEmptyView;
//    @BindView(R.id.delivery_loading_spinner)
    View deliveryLoadingSpinner;
    @BindView(R.id.fab_delivery)
    FloatingActionButton fabDelivery;

    private Unbinder unbinder;

    DeliveryCursorAdapter mAdapter;
    private static final int DELIVERY_LOADER_ID = 3;
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

    static final String DELIVERY_SORT_ORDER = BookEntry.COLUMN_BOOK_NAME + " ASC, " + DeliveryEntry.COLUMN_DATE + " DESC ";


    public DeliveryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delivery_item_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        fabDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DeliveryEditor.class);
                startActivity(intent);
            }
        });

        deliveryListView.setEmptyView(deliveryEmptyView);

        // Setup an Adapter to create a list item for each row of supplier data in the Cursor.
        mAdapter = new DeliveryCursorAdapter(getContext(), null);
        // Attach the adapter to the ListView.
        deliveryListView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(DELIVERY_LOADER_ID, null, this);
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
        return new CursorLoader(getContext(), DeliveryEntry.CONTENT_URI,
                DELIVERY_PROJECTION, null, null, DELIVERY_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

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
