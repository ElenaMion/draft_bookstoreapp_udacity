package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;

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
        // for the postivie and negative buttons on the dialog.
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
        // for the postivie and negative buttons on the dialog.
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
}
