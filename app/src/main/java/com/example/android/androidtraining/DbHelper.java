package com.example.android.androidtraining;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by hydRa on 18.7.2015.
 * Source: https://www.youtube.com/watch?v=v61A90qlK9s
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    public static final int DB_VERSION = 4; // Tested the onUpgrade method, initially was 1
    public static final String DB_NAME = "registration.db";
    public static final String DB_TABLE = "users";
    public static final String C_ID = BaseColumns._ID;
    public static final String C_FIRSTNAME = "FirstName";
    public static final String C_LASTNAME = "LastName";
    public static final String C_PASSWORD = "Password";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table sql command, alternative could be via Strings resources
        String sql_createTable = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)",
                DB_TABLE, C_ID, C_FIRSTNAME, C_LASTNAME, C_PASSWORD);
        // Check if the command is correct in debug Logcat
        Log.d(TAG, "onCreate sql: " + sql_createTable);
        db.execSQL(sql_createTable);
    }

    // Should be used for updating database to different version
    // like when new columns are introduced
    // care should be taken on the fact that different users upgrade from-to different versions
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Usually "ALTER TABLE" statement is used to add the columns
        // Android databases should be used as a "cache db" so the important information
        // are transmitted to the online database (security/reliability.. reasons)
        // if that is the case than the create/drop is also the usual approach

        // For testing purposes I will just drop and recreate the table
        String sql_deleteTable = String.format("DROP TABLE IF EXISTS %s", DB_TABLE);
        db.execSQL(sql_deleteTable);
        Log.d(TAG, "onUpgrade: Table: " + DB_TABLE + " had been dropped and recreated");
        this.onCreate(db);
    }
}
