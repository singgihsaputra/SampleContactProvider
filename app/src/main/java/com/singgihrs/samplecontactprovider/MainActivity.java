package com.singgihrs.samplecontactprovider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends FragmentActivity implements LoaderManager
    .LoaderCallbacks<Cursor> {

    private static final int PERMISSION_REQUEST_CONTACT = 1;

    /*
    * Defines an array that contains column names to move from
    * the Cursor to the ListView.
    */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
        Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private final static int[] TO_IDS = {
        R.id.tvContactName,
        R.id.tvContactNumber
    };

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
        {
            ContactsContract.Contacts._ID,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.IN_VISIBLE_GROUP,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        };

    private static final String SELECTION =
        "((" + ContactsContract.Contacts.DISPLAY_NAME
            + " NOTNULL) AND (" + ContactsContract.Contacts.DISPLAY_NAME
            + " != '' )) AND " +
            ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1 AND " +
            ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 AND " +
            (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? " : ContactsContract
                .Contacts
                .DISPLAY_NAME + " LIKE ? ");

    private static final String SORT = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE " +
        "LOCALIZED ASC";

    ListView lstContact;

    EditText etSearch;

    private SimpleCursorAdapter mCursorAdapter;

    private CustomContactAdapter customContactAdapter;

    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = new String[1];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lstContact = (ListView) findViewById(R.id.lstContacts);
        etSearch = (EditText) findViewById(R.id.etSearch);
        getPermission();
    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                new String[]
                    {Manifest.permission.READ_CONTACTS}
                , PERMISSION_REQUEST_CONTACT);
        } else {
            fetchContant();
        }
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri CONTACT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(this, CONTACT_URI, PROJECTION,
            SELECTION, mSelectionArgs, SORT);
        return cursorLoader;
    }

    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchContant();
                }
                return;
            }
        }
    }

    private void fetchContant() {
        mCursorAdapter = new SimpleCursorAdapter(
            this,
            R.layout.custom_element,
            null,
            FROM_COLUMNS, TO_IDS,
            0);
        lstContact.setAdapter(mCursorAdapter);
        mSelectionArgs[0] = "%%";
        getSupportLoaderManager().initLoader(1, null, this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                int count) {
                mSelectionArgs[0] = "%" + s + "%";
                getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}