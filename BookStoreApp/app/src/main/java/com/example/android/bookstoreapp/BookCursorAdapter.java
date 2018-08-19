package com.example.android.bookstoreapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookCursorAdapter extends CursorAdapter {


    public BookCursorAdapter(Context context, Cursor c) {
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

        View view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        BookViewHolder viewHolder = new BookViewHolder(view);
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
        BookViewHolder viewHolder = (BookViewHolder) view.getTag();

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_GENRE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
        int quantityStColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY_IN_STOCK);
        // Read the book attributes from the Cursor for the current book
        String bookName = cursor.getString(nameColumnIndex);
        String author = cursor.getString(authorColumnIndex);
        String genre = MyUtils.displayGenre(context, cursor.getInt(genreColumnIndex));
        String price = MyUtils.displayPrice(cursor.getString(priceColumnIndex));
        String quantityInStock = cursor.getString(quantityStColumnIndex);

        // Update the TextViews with the attributes for the current book
        viewHolder.tvBookName.setText(bookName);
        viewHolder.tvAuthor.setText(author);
        viewHolder.tvGenre.setText(genre);
        viewHolder.tvPrice.setText(price);
        viewHolder.tvQuantityInStock.setText(quantityInStock);
    }

    public static class BookViewHolder {
        @BindView(R.id.book_name)
        TextView tvBookName;
        @BindView(R.id.author)
        TextView tvAuthor;
        @BindView(R.id.genre)
        TextView tvGenre;
        @BindView(R.id.price)
        TextView tvPrice;
        @BindView(R.id.quantity_in_stock)
        TextView tvQuantityInStock;

        public BookViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
