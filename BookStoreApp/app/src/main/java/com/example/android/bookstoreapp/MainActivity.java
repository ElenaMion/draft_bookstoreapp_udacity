package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.example.android.bookstoreapp.data.BookDbHelper;
import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;

    Unbinder unbinder;

    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDbHelper(this);

        insertDummyData();

        myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        myViewPagerAdapter.addFragment(new BookFragment(), getString(R.string.books_tab_name));
        myViewPagerAdapter.addFragment(new SupplierFragment(), getString(R.string.suppliers_tab_name));
        myViewPagerAdapter.addFragment(new DeliveryFragment(),getString(R.string.deliveries_tab_name));

        // Set the adapter onto the view pager
        viewPager.setAdapter(myViewPagerAdapter);

        // Connect the tab layout with the view pager.
        tabs.setupWithViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("EXTRA_PAGE");
            int position= Integer.parseInt(value);
            viewPager.setCurrentItem(position);
        }


    }

    private void insertDummyData() {
        insertBookSupplierDelivery(getString(R.string.blood_of_elves), getString(R.string.sapkowski), 1999, 10, BookEntry.GENRE_FANTASY,
                getString(R.string.supplier_abc), "1234567", getString(R.string.abc_address), "2018-01-01");
        insertBookSupplierDelivery(getString(R.string.blood_of_elves), getString(R.string.sapkowski), 2000, 5, BookEntry.GENRE_FANTASY,
                getString(R.string.supplier_def), "7654321", getString(R.string.def_address), "2018-06-01");
        insertBookSupplierDelivery(getString(R.string.tower_swallow), getString(R.string.sapkowski), 1999, 17, BookEntry.GENRE_FANTASY,
                getString(R.string.supplier_abc), "1234567", getString(R.string.abc_address), "2018-01-23");
        insertBookSupplierDelivery(getString(R.string.crime_and_punishment), getString(R.string.dostoyevsky), 1500, 10, BookEntry.GENRE_DRAMA,
                getString(R.string.supplier_abc), "1234567", getString(R.string.abc_address), "2018-01-23");
        insertBookSupplierDelivery(getString(R.string.roadside_picnic), getString(R.string.strugatsky), 1500, 20, BookEntry.GENRE_SCI_FI,
                getString(R.string.supplier_klmn), "6666666", getString(R.string.klmn_old_address), "2018-05-12");
        insertBookSupplierDelivery(getString(R.string.roadside_picnic), getString(R.string.strugatsky), 1500, 8, BookEntry.GENRE_SCI_FI,
                getString(R.string.supplier_klmn), "8888888", getString(R.string.klmn_new_address), "2018-05-12");
    }

    /**
     * Method for inserting information about one delivery (book delivered + supplier who delivered + quantity delivered at a time)
     *
     * @param bookName
     * @param author
     * @param price
     * @param quantityDelivered
     * @param genre
     * @param supplierName
     * @param supplierPhone
     * @param supplierAddress
     * @param date
     */
    private void insertBookSupplierDelivery(String bookName, String author, int price, int quantityDelivered, int genre,
                                            String supplierName, String supplierPhone, String supplierAddress,
                                            String date) {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long bookId = insertBook(bookName, author, price, quantityDelivered, genre);
        long supplierId = insertSupplier(supplierName, supplierPhone, supplierAddress);

        //if there were no problems with the book and the supplier - add info about the delivery
        if (bookId != -1 && supplierId != -1) {
            insertDelivery(bookId, supplierId, quantityDelivered, date);
        }
    }

    /**
     * Inserts a new book or updates a book if there is one with the given name and author exists in db
     *
     * @param bookName
     * @param author
     * @param price
     * @param quantityDelivered
     * @param genre
     * @return book id - the one which was inserted or updated; or -1 if an error occured
     */
    private long insertBook(String bookName, String author, int price, int quantityDelivered, int genre) {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues book = new ContentValues();
        book.put(BookEntry.COLUMN_BOOK_NAME, bookName);
        book.put(BookEntry.COLUMN_BOOK_AUTHOR, author);
        book.put(BookEntry.COLUMN_PRICE, price);
        book.put(BookEntry.COLUMN_GENRE, genre);

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY_IN_STOCK,
                BookEntry.COLUMN_GENRE
        };
        String selection = BookEntry.COLUMN_BOOK_NAME + "=? AND " + BookEntry.COLUMN_BOOK_AUTHOR + "=? ";
        String[] selectionArgs = {bookName, author};

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        //if book does not exist yet
        if (cursor.getCount() == 0) {
            book.put(BookEntry.COLUMN_QUANTITY_IN_STOCK, quantityDelivered);
            // Insert a new row for the book in the database, returning the ID of that new row.
            long newBookRowId = db.insert(BookEntry.TABLE_NAME, null, book);
            Log.e(LOG_TAG, getString(R.string.new_book_insert) + newBookRowId);
            cursor.close();
            return newBookRowId;
        } else {
            try {
                if (cursor.moveToFirst()) {
                    int quantityInStockColIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY_IN_STOCK);
                    int quantityInStock = cursor.getInt(quantityInStockColIndex);
                    int newQuantity = quantityInStock + quantityDelivered;
                    book.put(BookEntry.COLUMN_QUANTITY_IN_STOCK, newQuantity);

                    int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                    int foundBookId = cursor.getInt(idColumnIndex);
                    String newSelection = BookEntry._ID + "=?";
                    String[] newSelectionArgs = {"" + foundBookId};
                    db.update(BookEntry.TABLE_NAME, book, newSelection, newSelectionArgs);
                    Log.e(LOG_TAG, getString(R.string.exist_book_update) + foundBookId);
                    return foundBookId;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, getString(R.string.update_book_error_msg) + e);
            } finally {
                cursor.close();
            }
            return -1; //an error occured; couldn't insert or update book
        }
    }

    /**
     * Inserts a new supplier or updates a supplier if there is one with the given name existing in db
     *
     * @param supplierName
     * @param supplierPhone
     * @param supplierAddress
     * @return supplier id - the one which was inserted or updated; or -1 if an error occured
     */
    private long insertSupplier(String supplierName, String supplierPhone, String supplierAddress) {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues supplier = new ContentValues();
        supplier.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplierName);
        supplier.put(SupplierEntry.COLUMN_PHONE, supplierPhone);
        supplier.put(SupplierEntry.COLUMN_ADDRESS, supplierAddress);

        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME
        };

        String selection = SupplierEntry.COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = {supplierName};

        Cursor cursor = db.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        //if supplier does not exist yet
        if (cursor.getCount() == 0) {
            // Insert a new row for the supplier in the database, returning the ID of that new row.
            long newSupplierRowId = db.insert(SupplierEntry.TABLE_NAME, null, supplier);
            cursor.close();
            Log.e(LOG_TAG, getString(R.string.new_supplier_insert) + newSupplierRowId);
            return newSupplierRowId;
        } else {
            try {
                if (cursor.moveToFirst()) {
                    int idColumnIndex = cursor.getColumnIndex(SupplierEntry._ID);
                    int foundSupplierId = cursor.getInt(idColumnIndex);
                    String newSelection = SupplierEntry._ID + "=?";
                    String[] newSelectionArgs = {"" + foundSupplierId};
                    db.update(SupplierEntry.TABLE_NAME, supplier, newSelection, newSelectionArgs);
                    Log.e(LOG_TAG, getString(R.string.exist_supplier_update) + foundSupplierId);
                    return foundSupplierId;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, getString(R.string.update_supplier_error_msg) + e);
            } finally {
                cursor.close();
            }
            return -1; //an error occured; couldn't insert or update book
        }
    }

    /**
     * Inserts a new entry about a certain delivery
     *
     * @param bookId
     * @param supplierId
     * @param quantityDelivered
     * @param date
     * @return the inserted delivery id
     */
    private long insertDelivery(long bookId, long supplierId, int quantityDelivered, String date) {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues delivery = new ContentValues();
        delivery.put(DeliveryEntry.COLUMN_BOOK_ID, bookId);
        delivery.put(DeliveryEntry.COLUMN_SUPPLIER_ID, supplierId);
        delivery.put(DeliveryEntry.COLUMN_QUANTITY_DELIVERED, quantityDelivered);
        delivery.put(DeliveryEntry.COLUMN_DATE, date);
        long newDeliveryRowId = db.insert(DeliveryEntry.TABLE_NAME, null, delivery);
        Log.e(LOG_TAG, getString(R.string.new_delivery_insert) + newDeliveryRowId);
        return newDeliveryRowId;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}

