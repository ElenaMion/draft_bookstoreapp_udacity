package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
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
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;

public class BookEditor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SupplierEditor.class.getSimpleName();

    private static final int EDIT_BOOK_LOADER = 5;

    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri mCurrentBookUri;

    // EditText fields and spinner from the layout file
    @BindView(R.id.edit_book_name)
    EditText mBookNameEditText;

    @BindView(R.id.edit_book_author)
    EditText mAuthorEditText;

    @BindView(R.id.edit_book_genre)
    Spinner mGenreSpinner;

    @BindView(R.id.edit_book_price)
    EditText mPriceEditText;

    @BindView(R.id.book_editor_hint)
    TextView mBookEditorHint;

    private int mGenre = BookEntry.GENRE_UNKNOWN;

    Unbinder unbinder;

    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean mBookHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying the view
     */
    private View.OnTouchListener mBookTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_editor);
        unbinder = ButterKnife.bind(this);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if a user wants to create a new book or edit an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(R.string.book_editor_title_new); //a new book is added if the book name and author are not yet in db; otherwise the book is edited
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.book_editor_title_edit);
            mBookEditorHint.setVisibility(View.GONE);

            getLoaderManager().initLoader(EDIT_BOOK_LOADER, null, this);
        }

        mBookNameEditText.setOnTouchListener(mBookTouchListener);
        mAuthorEditText.setOnTouchListener(mBookTouchListener);
        mPriceEditText.setOnTouchListener(mBookTouchListener);
        mGenreSpinner.setOnTouchListener(mBookTouchListener);
        setupSpinner();
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
     * Get user input from editor and save book into database.
     */
    private void saveBook() {
        String bookNameStr = mBookNameEditText.getText().toString().trim();
        String bookAuthorStr = mAuthorEditText.getText().toString().trim();
        String priceStr = mPriceEditText.getText().toString().trim();

        int bookExists = MyUtils.bookExistsInDB(this, bookNameStr, bookAuthorStr);

        // Check if this is supposed to be a new supplier
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null && bookExists == -1 &&
                TextUtils.isEmpty(bookNameStr) && TextUtils.isEmpty(bookAuthorStr) &&
                TextUtils.isEmpty(priceStr) && mGenre == BookEntry.GENRE_UNKNOWN) {
            // Since no fields were modified, we can return early without creating a new supplier.
            return;
        }

        // If the price is not provided by the user, use 0 by default.
        int price = 0;
        if (!TextUtils.isEmpty(priceStr)) {
            price = Integer.parseInt(priceStr);
        }

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, bookNameStr);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, bookAuthorStr);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_GENRE, mGenre);

        // Determine if this is a new or existing book
        if (mCurrentBookUri == null && bookExists == -1) {
            // This is a NEW book
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, R.string.book_editor_insert_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.book_editor_insert_success,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING supplier
            if (mCurrentBookUri == null) {
                mCurrentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookExists);
            }

            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.book_editor_update_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.book_editor_update_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {

            //delete book deliveries first
            String selectionBookDeliveries = DeliveryEntry.COLUMN_BOOK_ID + "= ?";
            String[] selectionArgsBookDeliveries = new String[]{String.valueOf(ContentUris.parseId(mCurrentBookUri))};

            int deliveryRowsDeleted = getContentResolver().delete(DeliveryEntry.CONTENT_URI, selectionBookDeliveries, selectionArgsBookDeliveries);

            //show how many deliveries were deleted
            Toast.makeText(this, "" + deliveryRowsDeleted + getString(R.string.deliveres_deleted_toast),
                    Toast.LENGTH_LONG).show();

            //delete the book
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.book_editor_delete_failed,
                        Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.book_editor_delete_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editors, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                try {
                    saveBook();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, R.string.enter_required,
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "Can't save book: IllegalArgumentException " + e.toString());
                }
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Delete" button
                                deleteBook();
                                //in order to go to the book list after a book was deleted
                                Intent intent = new Intent(BookEditor.this, MainActivity.class);
                                startActivity(intent);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                MyUtils.showDeleteConfirmationDialog(BookEditor.this, deleteButtonClickListener, getString(R.string.delete_dialog_msg_book));
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(BookEditor.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(BookEditor.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                MyUtils.showUnsavedChangesDialog(BookEditor.this, discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
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
        MyUtils.showUnsavedChangesDialog(BookEditor.this, discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_GENRE,
                BookEntry.COLUMN_PRICE};

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
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

            // Read the book attributes from the Cursor for the current book
            String bookName = cursor.getString(nameColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            int genre = cursor.getInt(genreColumnIndex);
            int price = cursor.getInt(priceColumnIndex);

            // Update the TextViews with the attributes for the current book
            mBookNameEditText.setText(bookName);
            mAuthorEditText.setText(author);
            mPriceEditText.setText("" + price);

            switch (genre) {
                case BookEntry.GENRE_FANTASY:
                    mGenreSpinner.setSelection(1);
                    break;
                case BookEntry.GENRE_SCI_FI:
                    mGenreSpinner.setSelection(2);
                    break;
                case BookEntry.GENRE_HORROR:
                    mGenreSpinner.setSelection(3);
                    break;
                case BookEntry.GENRE_ADVENTURE:
                    mGenreSpinner.setSelection(4);
                    break;
                case BookEntry.GENRE_ROMANCE:
                    mGenreSpinner.setSelection(5);
                    break;
                case BookEntry.GENRE_DRAMA:
                    mGenreSpinner.setSelection(6);
                    break;
                default:
                    mGenreSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mBookNameEditText.setText("");
        mAuthorEditText.setText("");
        mPriceEditText.setText("");
        mGenreSpinner.setSelection(0); // Select "Unknown" genre

        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
