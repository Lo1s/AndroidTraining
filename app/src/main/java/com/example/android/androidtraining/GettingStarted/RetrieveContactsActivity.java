package com.example.android.androidtraining.GettingStarted;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.android.androidtraining.R;

public class RetrieveContactsActivity extends AppCompatActivity {

    // Defines a variable for the search string
    private static String mSearchString;

    public static class ContactsFragment extends android.support.v4.app.Fragment implements
            LoaderManager.LoaderCallbacks, AdapterView.OnItemClickListener {

        /*
        * Defines an array that contains column names to move from
        * the Cursor to the ListView.
        */
        private final static String[] FROM_COLUMNS = {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME
        };
        // Define a projection
        private static final String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME
        };
        // The column index for the _ID column
        private static final int CONTACT_ID_INDEX = 0;
        // The column index for the LOOKUP_KEY column
        private static final int LOOKUP_KEY_INDEX = 1;
        // Define the selection
        private static final String SELECTION =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                        ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

        // Defines the array to hold values that replace the ?
        private String[] mSelectionArgs = { mSearchString };

        /*
        * Defines an array that contains resource ids for the layout views
        * that get the Cursor column contents. The id is pre-defined in
        * the Android framework, so it is prefaced with "android.R.id"
        */
        private final static int[] TO_IDS = {android.R.id.text1};
        // Define global mutable variables
        // Define a ListView object
        ListView mContactsList;
        // Define variables for the contact the user selects
        // The contact's _ID value
        long mContactId;
        // The contact's LOOKUP_KEY
        String mContactKey;
        // A content URI for the selected contact
        Uri mContactUri;
        // An adapter that binds the result Cursor to the ListView
        SimpleCursorAdapter mCursorAdapter;

        // Empty public constructor, required by the system
        public ContactsFragment() {

        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the fragment layout
            return inflater.inflate(R.layout.fragment_retrieve_contacts, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Gets the ListView from the View list of the parent activity
            mContactsList = (ListView) getActivity().findViewById(android.R.id.list);
            // Gets a CursorAdapter
            mCursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.activity_retrieve_contacts_item,
                    null,
                    FROM_COLUMNS, TO_IDS,
                    0);
            // Sets the adapter for the ListView
            mContactsList.setAdapter(mCursorAdapter);
            // Set the item click listener to be the current fragment.
            mContactsList.setOnItemClickListener(this);

            // Initializes the loader
            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public Loader onCreateLoader(int id, Bundle args) {
            /*
            * Makes search string into pattern and
            * stores it in the selection array
            */
            mSelectionArgs[0] = "%" + mSearchString + "%";
            // Starts the query
            return new CursorLoader(
                    getActivity(),
                    ContactsContract.Contacts.CONTENT_URI,
                    PROJECTION, SELECTION, mSelectionArgs, null);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Get the Cursor
            Cursor cursor = mCursorAdapter.getCursor();
            // Move to the selected contact
            cursor.moveToPosition(position);
            // Get the _ID value
            mContactId = cursor.getLong(CONTACT_ID_INDEX);
            // Get the selected LOOKUP KEY
            mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
            // Create the contact's content Uri
            mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
            /*
            * You can use mContactUri as the content URI for retrieving
            * the details for a contact.
            */
        }

        @Override
        public void onLoaderReset(Loader loader) {
            // Delete the reference to the existing Cursor
            mCursorAdapter.swapCursor(null);
        }

        @Override
        public void onLoadFinished(Loader loader, Object data) {
            // Put the result Cursor in the adapter for the ListView
            mCursorAdapter.swapCursor((Cursor)data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_contacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_retrieve_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchContacts(View view) {
        EditText editTextSearch = (EditText) findViewById(R.id.editText_search_contacts);
        mSearchString = editTextSearch.getText().toString();
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = new ContactsFragment();
        ft.add(R.id.fragment_retrieve_contacts, fragment);
        ft.commit();
    }
}
