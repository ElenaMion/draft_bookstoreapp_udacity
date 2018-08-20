package com.example.android.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SupplierCursorAdapter extends CursorAdapter {


    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.supplier_item, parent, false);
        SupplierViewHolder viewHolder = new SupplierViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * This method binds the book data to the given list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SupplierViewHolder viewHolder = (SupplierViewHolder) view.getTag();

        // Find the columns of supplier attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PHONE);
        int addressColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_ADDRESS);

        // Read the supplier attributes from the Cursor for the current supplier
        String supplierName = cursor.getString(nameColumnIndex);
        String phone = cursor.getString(phoneColumnIndex);
        String address = cursor.getString(addressColumnIndex);

        // Update the TextViews with the attributes for the current supplier
        viewHolder.tvSupplierName.setText(supplierName);
        viewHolder.tvPhone.setText(phone);
        viewHolder.tvAddress.setText(address);
    }

    public static class SupplierViewHolder {
        @BindView(R.id.supplier_name)
        TextView tvSupplierName;
        @BindView(R.id.phone)
        TextView tvPhone;
        @BindView(R.id.address)
        TextView tvAddress;

        public SupplierViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
