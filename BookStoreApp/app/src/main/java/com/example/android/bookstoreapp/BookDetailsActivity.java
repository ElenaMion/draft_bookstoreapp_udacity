package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BookDetailsActivity extends AppCompatActivity implements
        android.app.LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = SupplierFragment.class.getSimpleName();

    private static final int BOOK_DETAILS_LOADER = 4;

    private SupplierFragment bookSuppliers;

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

    @BindView(R.id.book_details_ll)
    LinearLayout bookDetailsLayout;

    @BindView(R.id.increase_quantity_stock)
    Button increaseQuantityStock;
    @BindView(R.id.decrease_quantity_stock)
    Button decreaseQuantityStock;

    static final String[] BOOK_PROJECTION = new String[]{BookEntry._ID, BookEntry.COLUMN_BOOK_NAME, BookEntry.COLUMN_BOOK_AUTHOR, BookEntry.COLUMN_GENRE, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_QUANTITY_IN_STOCK};

    private Unbinder unbinder;

    private Uri mCurrentBookDetUri;

    //for quantity changing buttons
    int currentBookId;
    int currentQuantityInStock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details);
        unbinder = ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentBookDetUri = intent.getData();

        getLoaderManager().initLoader(BOOK_DETAILS_LOADER, null, this);

        Bundle arguments = new Bundle();
        String currentBookIdStr = String.valueOf(ContentUris.parseId(mCurrentBookDetUri));
        currentBookId = Integer.parseInt(currentBookIdStr);
        arguments.putString("currentBookDetId", currentBookIdStr);
        bookSuppliers = new SupplierFragment();
        bookSuppliers.setArguments(arguments);

        getSupportFragmentManager().beginTransaction().replace(R.id.supplier_scroll_book_details, bookSuppliers).commit();

        bookDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailsActivity.this, BookEditor.class);
                intent.setData(mCurrentBookDetUri);
                startActivity(intent);
                }
            }
        );

        decreaseQuantityStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = currentQuantityInStock - 1;
                MyUtils.updateQuantity(BookDetailsActivity.this, currentBookId, newQuantity);
            }
        });

        increaseQuantityStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = currentQuantityInStock + 1;
                MyUtils.updateQuantity(BookDetailsActivity.this, currentBookId, newQuantity);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                mCurrentBookDetUri,
                BOOK_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first (and only) row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityStColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY_IN_STOCK);

            // Read the book attributes from the Cursor for the current book
            String bookName = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String genre = MyUtils.displayGenre(BookDetailsActivity.this, cursor.getInt(genreColumnIndex));
            String price = getString(R.string.currency) + MyUtils.displayPrice(cursor.getString(priceColumnIndex));
            currentQuantityInStock = cursor.getInt(quantityStColumnIndex);

            // Update the TextViews with the attributes for the current book
            tvBookName.setText(bookName);
            tvAuthor.setText(author);
            tvGenre.setText(genre);
            tvPrice.setText(price);
            tvQuantityInStock.setText("" + currentQuantityInStock);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(LOG_TAG, "Inside onLoaderReset");
        // If the loader is invalidated, clear out all the data from the input fields.
        tvBookName.setText("");
        tvAuthor.setText("");
        tvGenre.setText("");
        tvPrice.setText("");
        tvQuantityInStock.setText("");

        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
