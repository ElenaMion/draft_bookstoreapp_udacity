package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.bookstoreapp.R;
import com.example.android.bookstoreapp.SupplierEditor;
import com.example.android.bookstoreapp.data.BookDbHelper;
import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

public class BookProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;
    /**
     * URI matcher code for the content URI for the suppliers table
     */
    private static final int SUPPLIERS = 200;

    /**
     * URI matcher code for the content URI for a single supplier in the suppliers table
     */
    private static final int SUPPLIER_ID = 201;
    /**
     * URI matcher code for the content URI for the deliveries table
     */
    private static final int DELIVERIES = 300;

    /**
     * URI matcher code for the content URI for deliveries for a certain book
     */
    private static final int DELIVERY_BOOK_ID = 401;

    /**
     * URI matcher code for the content URI for deliveries for a certain book
     */
    private static final int DELIVERY_SUPPLIER_ID = 501;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_BOOKS + "/#", BOOK_ID);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_SUPPLIERS, SUPPLIERS);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_SUPPLIERS + "/#", SUPPLIER_ID);
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_DELIVERIES, DELIVERIES);

        //e.g. content://com.example.android.bookstoreapp/deliveries/books/1 - for book with id=1
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_DELIVERIES + "/" + BookStoreContract.PATH_BOOKS + "/#", DELIVERY_BOOK_ID);

        //e.g. content://com.example.android.bookstoreapp/deliveries/suppliers/1 - for supplier with id=1
        sUriMatcher.addURI(BookStoreContract.CONTENT_AUTHORITY, BookStoreContract.PATH_DELIVERIES + "/" + BookStoreContract.PATH_SUPPLIERS + "/#", DELIVERY_SUPPLIER_ID);
    }

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SUPPLIERS:
                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(SupplierEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case DELIVERIES:
                String selectionDeliveries = DeliveryEntry.COLUMN_BOOK_ID + "=" + BookEntry.TABLE_NAME + "." + BookEntry._ID + " AND " +
                        DeliveryEntry.COLUMN_SUPPLIER_ID + "=" + SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID;
                cursor = database.query(DeliveryEntry.TABLE_NAME + ", " + BookEntry.TABLE_NAME + ", " + SupplierEntry.TABLE_NAME,
                        projection, selectionDeliveries, null, null, null, sortOrder);
                break;
            case DELIVERY_BOOK_ID:
                String selectionDeliveryBook = DeliveryEntry.COLUMN_BOOK_ID + "=? AND " + DeliveryEntry.COLUMN_BOOK_ID + "=" + BookEntry.TABLE_NAME + "." + BookEntry._ID + " AND " +
                        DeliveryEntry.COLUMN_SUPPLIER_ID + "=" + SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(DeliveryEntry.TABLE_NAME + " , " + BookEntry.TABLE_NAME + " , " + SupplierEntry.TABLE_NAME, projection, selectionDeliveryBook, selectionArgs,
                        SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID, null, sortOrder);
                break;
            case DELIVERY_SUPPLIER_ID:
                String selectionDeliverySupplier = DeliveryEntry.COLUMN_SUPPLIER_ID + "=? AND " +
                        DeliveryEntry.COLUMN_SUPPLIER_ID + "=" + SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID;
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(DeliveryEntry.TABLE_NAME + " , " + SupplierEntry.TABLE_NAME, projection, selectionDeliverySupplier, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            case SUPPLIERS:
                return SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            case DELIVERIES:
                return DeliveryEntry.CONTENT_LIST_TYPE;
            case DELIVERY_BOOK_ID:
                return DeliveryEntry.CONTENT_ITEM_TYPE_WITH_BOOK;
            case DELIVERY_SUPPLIER_ID:
                return DeliveryEntry.CONTENT_ITEM_TYPE_WITH_SUPPLIER;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            //TODO others
            case SUPPLIERS:
                return insertSupplier(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a supplier into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertSupplier(Uri uri, ContentValues values) {

        String name = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_NAME);
        if (name == null || TextUtils.isEmpty(name)) {
            Log.e(LOG_TAG, "Name is null!");
            throw new IllegalArgumentException(getContext().getString(R.string.supplier_name_required));
        }

        String phone = values.getAsString(SupplierEntry.COLUMN_PHONE);
        if (phone == null || TextUtils.isEmpty(phone)) {
            throw new IllegalArgumentException(getContext().getString(R.string.supplier_phone_required));
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(SupplierEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            //TODO others
            case SUPPLIERS:
                rowsDeleted = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //TODO add for other URI
            case SUPPLIERS:
                return updateSupplier(uri, values, selection, selectionArgs);
            case SUPPLIER_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSupplier(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSupplier(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(SupplierEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_NAME);
            if (name == null || TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException(getContext().getString(R.string.supplier_name_required));
            }
        }

        if (values.containsKey(SupplierEntry.COLUMN_PHONE)) {
            String phone = values.getAsString(SupplierEntry.COLUMN_PHONE);
            if (phone == null || TextUtils.isEmpty(phone)) {
                throw new IllegalArgumentException(getContext().getString(R.string.supplier_phone_required));
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(SupplierEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
