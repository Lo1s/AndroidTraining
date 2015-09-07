package com.example.android.androidtraining.GettingStarted;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidtraining.R;


public class LoginActivity extends ActionBarActivity {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor dbQuery;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPass;
    private EditText editTextPassRepeated;
    private TextView textViewLastSigned;
    private String firstName;
    private String lastName;
    private String password;
    private String passwordRepeated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Open database
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        checkLastSignedUser();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Close database
        db.close();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_login, menu);
        // Associate searchable configuration with the SearchView
        // TODO: 7.9.2015 Fix this after adding search method
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //SearchView searchView = (SearchView) menu.findItem(R.id.search_login).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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

    public void checkLastSignedUser() {
        // Read the last signed user
        String[] columns = new String[]{DbHelper.C_FIRSTNAME, DbHelper.C_LASTNAME};
        dbQuery = db.rawQuery(String.format("select %s, %s from %s", DbHelper.C_FIRSTNAME,
                DbHelper.C_LASTNAME, DbHelper.DB_TABLE), null);
        dbQuery.moveToLast();
        if (dbQuery.getCount() > 0) {
            textViewLastSigned = (TextView) findViewById(R.id.lastSignedUser);
            textViewLastSigned.setText(dbQuery.getString(0) + " " + dbQuery.getString(1));
        }
    }

    public void signIn(View view) {
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextPass = (EditText) findViewById(R.id.editTextPass);
        editTextPassRepeated = (EditText) findViewById(R.id.editTextPassRepeat);

        firstName = editTextFirstName.getText().toString();
        lastName = editTextLastName.getText().toString();
        password = editTextPass.getText().toString();
        passwordRepeated = editTextPassRepeated.getText().toString();

        if ((firstName.length() > 0) && password.equals(passwordRepeated) && (password.length() >= 4)) {
            // Values could be inputed into the database by the spaghetti code
            // which would look the same as in DbHelper.onCreate method (string sql command passed to db.exec..
            // This would be very bad approach for security and performance reasons (SQL injection, parsing multiple statements
            // Instead we use ContentValues class which stores the values in key-value pair and safely parse it to the db
            ContentValues values = new ContentValues();
            values.put(DbHelper.C_FIRSTNAME, firstName);
            values.put(DbHelper.C_LASTNAME, lastName);
            values.put(DbHelper.C_PASSWORD, password);
            db.insert(DbHelper.DB_TABLE, null, values);

            editTextFirstName.setText("");
            editTextLastName.setText("");
            editTextPass.setText("");
            editTextPassRepeated.setText("");

            Toast.makeText(this, "Signed successfully ! (" + firstName + ": " + password + ")", Toast.LENGTH_LONG).show();
            checkLastSignedUser();
        } else if ((firstName.length() > 0) && !password.equals(passwordRepeated)){
            Toast.makeText(this, "Registration failed: Password does not match!", Toast.LENGTH_LONG).show();
        } else if ((firstName.length() > 0) && password.length() < 4) {
            Toast.makeText(this, "Registration failed: Password at least 4 chars", Toast.LENGTH_LONG).show();
        } else if (firstName.length() <= 0)
            Toast.makeText(this, "Registration failed: Username not filled in", Toast.LENGTH_LONG).show();
    }

    // Show the database of already registered users
    public void showRegistered(View view) {
        startActivity(new Intent(this, DatabaseActivity.class));
    }
}
