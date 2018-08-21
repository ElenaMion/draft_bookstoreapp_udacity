package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class with static helper methods - e.g. methods for formatting Strings before displaying them to a user
 */
public class MyUtils {
    /**
     * * Convert int value for genre from the BookStoreContract into a user-friendly String
     *
     * @param genreInt int value
     * @param context  - needed in order to access strings from strings.xml
     * @return genre String
     */
    public static String displayGenre(Context context, int genreInt) {
        String genre;
        switch (genreInt) {
            case BookEntry.GENRE_FANTASY:
                genre = context.getString(R.string.fantasy);
                break;
            case BookEntry.GENRE_SCI_FI:
                genre = context.getString(R.string.sci_fi);
                break;
            case BookEntry.GENRE_HORROR:
                genre = context.getString(R.string.horror);
                break;
            case BookEntry.GENRE_ADVENTURE:
                genre = context.getString(R.string.adventure);
                break;
            case BookEntry.GENRE_ROMANCE:
                genre = context.getString(R.string.romance);
                break;
            case BookEntry.GENRE_DRAMA:
                genre = context.getString(R.string.drama);
                break;
            default:
                genre = context.getString(R.string.unknown_genre);
                break;
        }
        return genre;
    }

    /**
     * Convert price from cents into euros + cents
     *
     * @param price
     * @return
     */
    public static String displayPrice(String price) {
        try {
            double priceFormatted = Double.parseDouble(price) / 100;
            return String.format("%.2f", priceFormatted);
        } catch (NumberFormatException e) {
            return price;
        }
    }

    public static String displayDate(String publDate) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("d-MMMM-yyyy");
        Date tmpDate = null;
        try {
            tmpDate = format1.parse(publDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedPublDate = format2.format(tmpDate);
        formattedPublDate = formattedPublDate.replace("-", " ");
        return ((formattedPublDate));
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    public static void showUnsavedChangesDialog(Context context,
                                                DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showDeleteConfirmationDialog(Context context,
                                                    DialogInterface.OnClickListener deleteButtonClickListener, String message) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.delete, deleteButtonClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * check if a supplier with the given name exists in the database
     *
     * @param context
     * @param supplierName
     * @return supplier _ID if it exists; return -1 if it doesn't exist in DB
     */
    public static int supplierExistsInDB(Context context, String supplierName) {
        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME
        };

        String selection = SupplierEntry.COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = {supplierName};
        Cursor cursor = context.getContentResolver().query(SupplierEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        //if supplier does not exist yet
        if (cursor.getCount() == 0) {
            return -1;
        } else {
            try {
                if (cursor.moveToFirst()) {
                    int idColumnIndex = cursor.getColumnIndex(SupplierEntry._ID);
                    int foundSupplierId = cursor.getInt(idColumnIndex);
                    return foundSupplierId;
                }
            } catch (Exception e) {
                Log.e(context.getClass().getSimpleName(), context.getString(R.string.find_supplier_error_msg) + e);
            } finally {
                cursor.close();
            }
            return -1; //an error occured;
        }
    }

    /**
     * check if a book with the given name exists in the database\
     *
     * @param context
     * @param bookName
     * @param author
     * @return book _ID if it exists; return -1 if it doesn't exist in DB
     */
    public static int bookExistsInDB(Context context, String bookName, String author) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR
        };
        String selection = BookEntry.COLUMN_BOOK_NAME + "=? AND " + BookEntry.COLUMN_BOOK_AUTHOR + "=? ";
        String[] selectionArgs = {bookName, author};

        Cursor cursor = context.getContentResolver().query(BookEntry.CONTENT_URI,
                projection, selection, selectionArgs, null);

        //if book does not exist yet
        if (cursor.getCount() == 0) {
            return -1;
        } else {
            try {
                if (cursor.moveToFirst()) {
                    int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                    int foundBookId = cursor.getInt(idColumnIndex);
                    return foundBookId;
                }
            } catch (Exception e) {
                Log.e(context.getClass().getSimpleName(), context.getString(R.string.find_book_error_msg) + e);
            } finally {
                cursor.close();
            }
            return -1; //an error occured;
        }
    }

    /**
     * updates quantity of books in stock
     *
     * @param context
     * @param bookId
     * @param newQuantity
     * @return how many rows were updated
     */
    public static int updateQuantity(Context context, int bookId, int newQuantity) {

        if (newQuantity < 0) {
            Toast.makeText(context, context.getString(R.string.error_msg_cant_sell),
                    Toast.LENGTH_SHORT).show();
            return 0;
        }

        Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);

        ContentValues book = new ContentValues();
        book.put(BookEntry.COLUMN_QUANTITY_IN_STOCK, newQuantity);

        //update existing book with new quantity
        int rowsAffected = context.getContentResolver().update(bookUri, book, null, null);

        return rowsAffected;
    }
}
