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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookStoreContract.DeliveryEntry;
import com.example.android.bookstoreapp.data.BookStoreContract.SupplierEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Allows user to create a new supplier or edit an existing one.
 */
public class SupplierEditor extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = SupplierEditor.class.getSimpleName();

    /**
     * Identifier for the supplier data loader
     */
    private static final int EDIT_SUPPLIER_LOADER = 6;

    /**
     * Content URI for the existing supplier (null if it's a new supplier)
     */
    private Uri mCurrentSupplierUri;

    // EditText fields from the layout file
    @BindView(R.id.edit_supplier_name)
    EditText mSupplierNameEditText;

    @BindView(R.id.edit_supplier_phone)
    EditText mSupplierPhoneEditText;

    @BindView(R.id.edit_supplier_address)
    EditText mSupplierAddressEditText;

    @BindView(R.id.supplier_editor_hint)
    TextView mSupplierEditorHint;

    Unbinder unbinder;

    /**
     * Boolean flag that keeps track of whether the supplier has been edited (true) or not (false)
     */
    private boolean mSupplierHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mSupplierHasChanged boolean to true.
     */
    private View.OnTouchListener mSupplierTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mSupplierHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier_editor);
        unbinder = ButterKnife.bind(this);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if a user wants to create a new supplier or edit an existing one.
        Intent intent = getIntent();
        mCurrentSupplierUri = intent.getData();

        if (mCurrentSupplierUri == null) {
            setTitle(R.string.supplier_editor_title_new); //a new supplier is added if the supplier name is not yet in db; otherwise the supplier is edited
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.supplier_editor_title_edit);
            mSupplierEditorHint.setVisibility(View.GONE);

            // Initialize a loader to read the supplier data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EDIT_SUPPLIER_LOADER, null, this);
        }

        mSupplierNameEditText.setOnTouchListener(mSupplierTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mSupplierTouchListener);
        mSupplierAddressEditText.setOnTouchListener(mSupplierTouchListener);
    }

    /**
     * Get user input from editor and save supplier into database.
     */
    private void saveSupplier() {
        String supplierNameStr = mSupplierNameEditText.getText().toString().trim();
        String phoneStr = mSupplierPhoneEditText.getText().toString().trim();
        String addressStr = mSupplierAddressEditText.getText().toString().trim();

        int supplierExists = MyUtils.supplierExistsInDB(this, supplierNameStr);

        // Check if this is supposed to be a new supplier
        // and check if all the fields in the editor are blank
        if (mCurrentSupplierUri == null && supplierExists == -1 &&
                TextUtils.isEmpty(supplierNameStr) && TextUtils.isEmpty(phoneStr) &&
                TextUtils.isEmpty(addressStr)) {
            // Since no fields were modified, we can return early without creating a new supplier.
            return;
        }

        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplierNameStr);
        values.put(SupplierEntry.COLUMN_PHONE, phoneStr);
        values.put(SupplierEntry.COLUMN_ADDRESS, addressStr);

        // Determine if this is a new or existing supplier
        if (mCurrentSupplierUri == null && supplierExists == -1) {
            // This is a NEW supplier
            Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toast.makeText(this, R.string.supplier_editor_insert_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.supplier_editor_insert_success,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING supplier
            if (mCurrentSupplierUri == null) {
                mCurrentSupplierUri = ContentUris.withAppendedId(SupplierEntry.CONTENT_URI, supplierExists);
            }

            int rowsAffected = getContentResolver().update(mCurrentSupplierUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.supplier_editor_update_failed,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.supplier_editor_update_success,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Try to perform the deletion of the supplier in the database.
     * Can't delete a supplier if there are records about it in the deliveries table.
     * Must delete deliveries first.
     */
    private void deleteSupplier() {
        // Only perform the delete if this is an existing supplier.
        if (mCurrentSupplierUri != null) {

            if (supplierExistsInDeliveries(String.valueOf(ContentUris.parseId(mCurrentSupplierUri)))) {
                //Can't delete the supplier if there are records about it in the deliveries table.
                Log.e(LOG_TAG, "Can't delete the supplier if there are records about it in the deliveries table. " + mCurrentSupplierUri);
                Toast.makeText(this, R.string.supplier_editor_delete_failed,
                        Toast.LENGTH_LONG).show();
            } else {
                int rowsDeleted = getContentResolver().delete(mCurrentSupplierUri, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, R.string.supplier_editor_delete_failed,
                            Toast.LENGTH_LONG).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, R.string.supplier_editor_delete_success,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        // Close the activity
        finish();
    }

    /**
     * check if there are deliveries from the given supplier in the deliveries table
     *
     * @param supplierId
     * @return true if there are deliveries
     */
    private boolean supplierExistsInDeliveries(String supplierId) {
        String[] projection = new String[]{
                DeliveryEntry.TABLE_NAME + "." + DeliveryEntry._ID,
                DeliveryEntry.COLUMN_SUPPLIER_ID,
                SupplierEntry.TABLE_NAME + "." + SupplierEntry._ID,
        };

        Cursor cursor = getContentResolver().query(ContentUris.withAppendedId(DeliveryEntry.CONTENT_URI_WITH_SUPPLIER, Long.parseLong(supplierId)),
                projection, null, null, null);

        //if supplier does not exist in deliveries
        if (cursor.getCount() == 0) {
            return false;
        }
        cursor.close();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editors, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentSupplierUri == null) {
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
                    saveSupplier();
                } catch (IllegalArgumentException e) {
                    Toast.makeText(this, R.string.enter_required,
                            Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "Can't save supplier: IllegalArgumentException " + e.toString());
                }
                finish();
                //in order to go to the suppliers list after a supplier was edited - including when the supplier was edited from the BookDetailsActivity
                Intent intent = new Intent(SupplierEditor.this, MainActivity.class);
                intent.putExtra("EXTRA_PAGE", "1");
                startActivity(intent);

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                DialogInterface.OnClickListener deleteButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Delete" button
                                deleteSupplier();
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                MyUtils.showDeleteConfirmationDialog(SupplierEditor.this, deleteButtonClickListener, getString(R.string.delete_dialog_msg_supplier));
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mSupplierHasChanged) {
                    NavUtils.navigateUpFromSameTask(SupplierEditor.this);
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
                                NavUtils.navigateUpFromSameTask(SupplierEditor.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                MyUtils.showUnsavedChangesDialog(SupplierEditor.this, discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mSupplierHasChanged) {
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
        MyUtils.showUnsavedChangesDialog(SupplierEditor.this, discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_PHONE,
                SupplierEntry.COLUMN_ADDRESS};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentSupplierUri,         // Query the content URI for the current supplier
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first (and only) row of the cursor and reading data from it
        if (cursor.moveToFirst()) {
            // Find the columns of supplier attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PHONE);
            int addressColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_ADDRESS);

            // Read the supplier attributes from the Cursor for the current supplier
            String supplierName = cursor.getString(nameColumnIndex);
            String phone = cursor.getString(phoneColumnIndex);
            String address = cursor.getString(addressColumnIndex);

            // Update the views on the screen with the values from the database
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(phone);
            mSupplierAddressEditText.setText(address);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierAddressEditText.setText("");

        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
