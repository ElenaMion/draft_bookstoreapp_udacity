package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;

/**
 * Database helper for BookStore app. Manages database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "bookstore.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    // a String that contains the SQL statement to create the books table
    String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
            + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
            + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, "
            + BookEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
            + BookEntry.COLUMN_QUANTITY_IN_STOCK + " INTEGER NOT NULL DEFAULT 0, "
            + BookEntry.COLUMN_GENRE + " INTEGER NOT NULL DEFAULT 0);";

    // a String that contains the SQL statement to create the suppliers table
    String SQL_CREATE_SUPPLIERS_TABLE = "CREATE TABLE " + SupplierEntry.TABLE_NAME + " ("
            + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SupplierEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
            + SupplierEntry.COLUMN_PHONE + " TEXT NOT NULL, "
            + SupplierEntry.COLUMN_ADDRESS + " TEXT); ";

    // a String that contains the SQL statement to create the deliveries table
    String SQL_CREATE_DELIVERIES_TABLE = "CREATE TABLE " + DeliveryEntry.TABLE_NAME + " ("
            + DeliveryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DeliveryEntry.COLUMN_BOOK_ID + " INTEGER NOT NULL, "
            + DeliveryEntry.COLUMN_SUPPLIER_ID + " INTEGER NOT NULL, "
            + DeliveryEntry.COLUMN_QUANTITY_DELIVERED + " INTEGER NOT NULL DEFAULT 0, "
            + DeliveryEntry.COLUMN_DATE + " TEXT, "
            + "FOREIGN KEY(" + DeliveryEntry.COLUMN_BOOK_ID + ") REFERENCES " + BookEntry.TABLE_NAME + "(_id), "
            + "FOREIGN KEY(" + DeliveryEntry.COLUMN_SUPPLIER_ID + ") REFERENCES " + SupplierEntry.TABLE_NAME + "(_id) " + "); ";

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statements
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
        db.execSQL(SQL_CREATE_SUPPLIERS_TABLE);
        db.execSQL(SQL_CREATE_DELIVERIES_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
