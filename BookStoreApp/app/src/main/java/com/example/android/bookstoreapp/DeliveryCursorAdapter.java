package com.example.android.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeliveryCursorAdapter extends CursorAdapter {


    public DeliveryCursorAdapter(Context context, Cursor c) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.delivery_item, parent, false);
        DeliveryViewHolder viewHolder = new DeliveryViewHolder(view);
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
        DeliveryViewHolder viewHolder = (DeliveryViewHolder) view.getTag();

        // Find the columns of delivery attributes that we're interested in
        int bookNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int quantityDelivColumnIndex = cursor.getColumnIndex(DeliveryEntry.COLUMN_QUANTITY_DELIVERED);
        int dateColumnIndex = cursor.getColumnIndex(DeliveryEntry.COLUMN_DATE);
        int supplierNameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PHONE);
        // Read the delivery attributes from the Cursor for the current delivery
        String bookName = cursor.getString(bookNameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        String quantityDelivered = cursor.getString(quantityDelivColumnIndex);
        String date = MyUtils.displayDate(cursor.getString(dateColumnIndex));
        String supplierName = cursor.getString(supplierNameColumnIndex);
        String phone = cursor.getString(phoneColumnIndex);

        // Update the TextViews with the attributes for the current delivery
        viewHolder.tvBookName.setText(bookName);
        viewHolder.tvAuthor.setText(author);
        viewHolder.tvQuantityDelivered.setText(quantityDelivered);
        viewHolder.tvDate.setText(date);
        viewHolder.tvSupplierName.setText(supplierName);
        viewHolder.tvPhone.setText(phone);
    }

    public static class DeliveryViewHolder {
        @BindView(R.id.book_name)
        TextView tvBookName;
        @BindView(R.id.author)
        TextView tvAuthor;
        @BindView(R.id.quantity_delivered)
        TextView tvQuantityDelivered;
        @BindView(R.id.date)
        TextView tvDate;
        @BindView(R.id.supplier_name)
        TextView tvSupplierName;
        @BindView(R.id.phone)
        TextView tvPhone;

        public DeliveryViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
