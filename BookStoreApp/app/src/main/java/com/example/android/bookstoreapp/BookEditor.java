package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.example.android.bookstoreapp.data.BookStoreContract.BookEntry;

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
        setContentView(R.layout.supplier_editor);
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
            mPriceEditText.setText(price);

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
