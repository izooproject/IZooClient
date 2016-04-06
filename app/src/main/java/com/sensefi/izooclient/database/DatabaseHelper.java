package com.sensefi.izooclient.database;
/**
 * Created by boobathiayyasamy on 03/04/16.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sensefi.izooclient.view.SettingsView;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Name
    public static String DATABASE_NAME = "settings_database";

    // Current version of database
    private static final int DATABASE_VERSION = 1;

    // Name of table
    private static final String TABLE_SETTINGS = "settings";

    // All Keys used in table
    private static final String KEY_ID = "id";
    private static final String KEY_IP_ADDRESS = "ip_Address";
    private static final String KEY_PORT = "port";

    public static String TAG = "tag";

    // settings Table Create Query
    /**
     * CREATE TABLE settings ( id INTEGER PRIMARY KEY AUTOINCREMENT, ipaddress
     * TEXT,port TEXT);
     */

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE "
            + TABLE_SETTINGS + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_IP_ADDRESS + " TEXT,"
            + KEY_PORT + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called by system if the database is accessed but not yet
     * created.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SETTINGS); // create students table

    }

    /**
     * This method is called when any modifications in database are done like
     * version is updated or database schema is changed
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_SETTINGS); // drop table if exists

        onCreate(db);
    }

    /**
     *
     * This method is used to add serttings detail in SETTINGS Table
     *
     * @return
     */

    public boolean addSettingsDetail(SettingsView settingsView) {
        Log.d("Insert Operation: ", "Inside Insert Operation");
        boolean returnValue = false;
        SQLiteDatabase db = this.getWritableDatabase();

        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_IP_ADDRESS, settingsView.getIpAddress());
        values.put(KEY_PORT, settingsView.getPort());

        // insert row in settings table

        long insert = db.insert(TABLE_SETTINGS, null, values);
        Log.d("Insert Opeartion: ", String.valueOf(insert));

        if(insert != -1) {
            returnValue = true;
        }
        Log.d("Insert Opeartion:", String.valueOf(returnValue));
        return returnValue;
    }

    /**
     * This method is used to update particular student entry
     *
     * @param settingsView
     * @return
     */
    public int updateEntry(SettingsView settingsView) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Creating content values
        ContentValues values = new ContentValues();
        values.put(KEY_IP_ADDRESS, settingsView.getIpAddress());
        values.put(KEY_PORT, settingsView.getPort());

        // update row in students table base on students.is value
        return db.update(TABLE_SETTINGS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(settingsView.getId()) });
    }

    /**
     * Used to delete particular settings entry
     *
     * @param id
     */
    public void deleteEntry(long id) {

        // delete row in students table based on id
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Used to delete all settings entry
     *
     *
     */
    public void deleteAllEntry() {
        Log.d("Inside Delete All:","Delete Begins");
        // delete row in students table based on id
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SETTINGS, null,null);
        Log.d("Inside Delete All:", "Delete Completed");
    }

    /**
     * Used to get particular student details
     *
     * @param id
     * @return
     */

    public SettingsView getSettings(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT * FROM students WHERE id = ?;
        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS + " WHERE "
                + KEY_ID + " = " + id;
        Log.d(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        SettingsView settingsView = new SettingsView();
        settingsView.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        settingsView.setPort(c.getString(c.getColumnIndex(KEY_PORT)));
        settingsView.setIpAddress(c.getString(c.getColumnIndex(KEY_IP_ADDRESS)));

        return settingsView;
    }

    /**
     * Used to get detail of entire database and save in array list of data type
     * StudentsModel
     *
     * @return
     */
    public List<SettingsView> getAllStudentsList() {
        Log.d("Get Settings:","Fetch Begins");
        List<SettingsView> settingsViewList = new ArrayList<SettingsView>();

        String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;
        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                SettingsView settingsView = new SettingsView();
                settingsView.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                settingsView.setPort(c.getString(c.getColumnIndex(KEY_PORT)));
                settingsView.setIpAddress(c.getString(c.getColumnIndex(KEY_IP_ADDRESS)));


                Log.d(TAG, settingsView.getPort());
                Log.d(TAG, settingsView.getIpAddress());
                // adding to settings list
                settingsViewList.add(settingsView);
            } while (c.moveToNext());
        }
        Log.d("Get Settings:","Fetch Ends");
        return settingsViewList;
    }
}

