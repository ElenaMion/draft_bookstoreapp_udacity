package com.example.android.bookstoreapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;

/**
 * Class for entering an information about a delivery - includes book info, supplier info and delivery info.
 * Books and suppliers can be added or edited through a delivery.
 */
public class DeliveryEditor extends AppCompatActivity {
    //TODO
    public static final String LOG_TAG = SupplierEditor.class.getSimpleName();

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
}
