package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;

/**
 * Class for entering an information about a delivery - includes book info, supplier info and delivery info.
 * Books and suppliers can be added or edited through a delivery.
 */
public class DeliveryEditor extends AppCompatActivity {
    //TODO
    public static final String LOG_TAG = DeliveryEditor.class.getSimpleName();

    // EditText fields and spinner from the layout file
    @BindView(R.id.edit_book_name)
    EditText mBookNameEditText;

    @BindView(R.id.edit_book_author)
    EditText mAuthorEditText;

    @BindView(R.id.edit_book_genre)
    Spinner mGenreSpinner;

    @BindView(R.id.edit_book_price)
    EditText mPriceEditText;

    @BindView(R.id.edit_delivery_quantity)
    EditText mQuantityDelEditText;

    @BindView(R.id.edit_delivery_date)
    EditText mDateEditText;

    @BindView(R.id.edit_supplier_name)
    EditText mSupplierNameEditText;

    @BindView(R.id.edit_supplier_phone)
    EditText mSupplierPhoneEditText;

    @BindView(R.id.edit_supplier_address)
    EditText mSupplierAddressEditText;

    Unbinder unbinder;
    private int mGenre = BookEntry.GENRE_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the supplier has been edited (true) or not (false)
     */
    private boolean mDeliveryHasChanged = false;

    private View.OnTouchListener mDeliveryTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDeliveryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_editor);
        unbinder = ButterKnife.bind(this);

        mBookNameEditText.setOnTouchListener(mDeliveryTouchListener);
        mAuthorEditText.setOnTouchListener(mDeliveryTouchListener);
        mPriceEditText.setOnTouchListener(mDeliveryTouchListener);
        mGenreSpinner.setOnTouchListener(mDeliveryTouchListener);
        setupSpinner();
        mQuantityDelEditText.setOnTouchListener(mDeliveryTouchListener);
        mDateEditText.setOnTouchListener(mDeliveryTouchListener);

        mSupplierNameEditText.setOnTouchListener(mDeliveryTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mDeliveryTouchListener);
        mSupplierAddressEditText.setOnTouchListener(mDeliveryTouchListener);

        //TODO test
        DeliveryDatePicker deliveryDatePicker = new DeliveryDatePicker (DeliveryEditor.this, R.id.edit_delivery_date);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the genre.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {

                    //can't use switch with strings.xml values
                    if (selection.equals(getString(R.string.fantasy))) {
                        mGenre = BookEntry.GENRE_FANTASY;
                    } else if (selection.equals(getString(R.string.sci_fi))) {
                        mGenre = BookEntry.GENRE_SCI_FI;
                    } else if (selection.equals(getString(R.string.horror))) {
                        mGenre = BookEntry.GENRE_HORROR;
                    } else if (selection.equals(getString(R.string.adventure))) {
                        mGenre = BookEntry.GENRE_ADVENTURE;
                    } else if (selection.equals(getString(R.string.romance))) {
                        mGenre = BookEntry.GENRE_ROMANCE;
                    } else if (selection.equals(getString(R.string.drama))) {
                        mGenre = BookEntry.GENRE_DRAMA;
                    } else {
                        mGenre = BookEntry.GENRE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = BookEntry.GENRE_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save supplier into database.
     */
    private void saveDelivery() {
        String bookNameStr = mBookNameEditText.getText().toString().trim();
        String bookAuthorStr = mAuthorEditText.getText().toString().trim();
        String priceStr = mPriceEditText.getText().toString().trim();

        String quantityDelStr = mQuantityDelEditText.getText().toString().trim();
        String dateStr = mDateEditText.getText().toString().trim();

        String supplierNameStr = mSupplierNameEditText.getText().toString().trim();
        String phoneStr = mSupplierPhoneEditText.getText().toString().trim();
        String addressStr = mSupplierAddressEditText.getText().toString().trim();

        int bookId = saveBook(bookNameStr, bookAuthorStr, priceStr, quantityDelStr, mGenre);
        int supplierId = saveSupplier(supplierNameStr, phoneStr, addressStr);

        //if there were no problems with the book and the supplier - add info about the delivery
        if (bookId != -1 && supplierId != -1) {

            // If the quantity delivered is not provided by the user, use 0 by default.
            int quantityDelivered = 0;
            if (!TextUtils.isEmpty(quantityDelStr)) {
                quantityDelivered = Integer.parseInt(quantityDelStr);
            }

            ContentValues delivery = new ContentValues();
            delivery.put(DeliveryEntry.COLUMN_BOOK_ID, bookId);
            delivery.put(DeliveryEntry.COLUMN_SUPPLIER_ID, supplierId);
            delivery.put(DeliveryEntry.COLUMN_QUANTITY_DELIVERED, quantityDelivered);
            delivery.put(DeliveryEntry.COLUMN_DATE, dateStr);

            Uri newUri = getContentResolver().insert(DeliveryEntry.CONTENT_URI, delivery);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.delivery_editor_insert_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.delivery_editor_insert_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * checks if the book entered in delivery already exists; update or create a new one
     *
     * @param bookNameStr
     * @param bookAuthorStr
     * @param priceStr
     * @param quantityDeliveredStr
     * @param genre
     * @return new or updated book _ID
     */
    private int saveBook(String bookNameStr, String bookAuthorStr, String priceStr, String quantityDeliveredStr, int genre) {

        int bookExists = MyUtils.bookExistsInDB(this, bookNameStr, bookAuthorStr);

        //can't find or insert a new book
        if (bookExists == -1 &&
                TextUtils.isEmpty(bookNameStr) && TextUtils.isEmpty(bookAuthorStr) &&
                TextUtils.isEmpty(priceStr) && mGenre == BookEntry.GENRE_UNKNOWN) {
            return -1;
        }

        // If the price is not provided by the user, use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceStr)) {
            price = Integer.parseInt(priceStr);
        }

        // If the quantity delivered is not provided by the user, use 0 by default.
        int quantityDelivered = 0;
        if (!TextUtils.isEmpty(quantityDeliveredStr)) {
            quantityDelivered = Integer.parseInt(quantityDeliveredStr);
        }

        ContentValues book = new ContentValues();
        book.put(BookEntry.COLUMN_BOOK_NAME, bookNameStr);
        book.put(BookEntry.COLUMN_BOOK_AUTHOR, bookAuthorStr);
        book.put(BookEntry.COLUMN_PRICE, price);
        book.put(BookEntry.COLUMN_GENRE, genre);

        // Determine if this is a new or existing book
        if (bookExists == -1) {
            // This is a NEW book
            book.put(BookEntry.COLUMN_QUANTITY_IN_STOCK, quantityDelivered);
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, book);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, R.string.book_editor_insert_failed,
                        Toast.LENGTH_SHORT).show();
                return -1; //error occured, couldn't insert new book
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.book_editor_insert_success,
                        Toast.LENGTH_SHORT).show();
                int newBookId = Integer.parseInt(String.valueOf(ContentUris.parseId(newUri)));
                return newBookId;
            }
        } else {
            // Otherwise this is an EXISTING book
            //get and update quantity in Stock
            String[] projectionQuantityStock = {
                    BookEntry._ID,
                    BookEntry.COLUMN_QUANTITY_IN_STOCK,
            };
            String selectionQuantityStock = BookEntry._ID + "=?";
            String[] selectionArgsQuantityStock = {"" + bookExists};

            Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookExists);

            Cursor cursorQuantityStock = getContentResolver().query(bookUri, projectionQuantityStock, selectionQuantityStock, selectionArgsQuantityStock, null);

            int newQuantity = quantityDelivered;

            if (cursorQuantityStock.moveToFirst()) {
                int oldQuantityInStockColIndex = cursorQuantityStock.getColumnIndex(BookEntry.COLUMN_QUANTITY_IN_STOCK);
                int oldQuantityInStock = cursorQuantityStock.getInt(oldQuantityInStockColIndex);
                newQuantity = oldQuantityInStock + quantityDelivered;

            }

            book.put(BookEntry.COLUMN_QUANTITY_IN_STOCK, newQuantity);

            //update existing book with new values
            int rowsAffected = getContentResolver().update(bookUri, book, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.book_editor_update_failed,
                        Toast.LENGTH_SHORT).show();
                return -1; //error occured, couldn't update the book
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.book_editor_update_success,
                        Toast.LENGTH_SHORT).show();
                return bookExists; //after updating the book, return it's _ID
            }
        }
    }

    /**
     * checks if the supplier entered in delivery already exists; update or create a new one
     *
     * @param supplierNameStr
     * @param phoneStr
     * @param addressStr
     * @return new or updated supplier _ID
     */
    private int saveSupplier(String supplierNameStr, String phoneStr, String addressStr) {

        int supplierExists = MyUtils.supplierExistsInDB(this, supplierNameStr);

        //can't find or insert a new supplier
        if (supplierExists == -1 &&
                TextUtils.isEmpty(supplierNameStr) && TextUtils.isEmpty(phoneStr) &&
                TextUtils.isEmpty(addressStr)) {
            return -1;
        }

        ContentValues supplier = new ContentValues();
        supplier.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplierNameStr);
        supplier.put(SupplierEntry.COLUMN_PHONE, phoneStr);
        supplier.put(SupplierEntry.COLUMN_ADDRESS, addressStr);

        if (supplierExists == -1) {
            // This is a NEW supplier
            Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_URI, supplier);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, R.string.supplier_editor_insert_failed,
                        Toast.LENGTH_SHORT).show();
                return -1; //error occured, couldn't insert new supplier
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.supplier_editor_insert_success,
                        Toast.LENGTH_SHORT).show();
                int newSupplierId = Integer.parseInt(String.valueOf(ContentUris.parseId(newUri)));
                return newSupplierId;
            }
        } else {
            // Otherwise this is an EXISTING supplier
            //update existing supplier with new values
            Uri supplierUri = ContentUris.withAppendedId(SupplierEntry.CONTENT_URI, supplierExists);
            int rowsAffected = getContentResolver().update(supplierUri, supplier, null, null);

            Log.e(LOG_TAG, " Supplier with url " + supplierUri + " updated. Inside Delivery.");

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.supplier_editor_update_failed,
                        Toast.LENGTH_SHORT).show();
                return -1; //error occured, couldn't update the supplier
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.supplier_editor_update_success,
                        Toast.LENGTH_SHORT).show();
                return supplierExists; //after updating the supplier, return it's _ID
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_delivery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                try {
                    saveDelivery();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, R.string.enter_required,
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "Can't save delivery: IllegalArgumentException " + e.toString());
                }
                finish();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mDeliveryHasChanged) {
                    NavUtils.navigateUpFromSameTask(DeliveryEditor.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DeliveryEditor.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                MyUtils.showUnsavedChangesDialog(DeliveryEditor.this, discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mDeliveryHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        MyUtils.showUnsavedChangesDialog(DeliveryEditor.this, discardButtonClickListener);
    }

}
