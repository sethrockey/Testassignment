package com.sj.cityweather.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.sj.cityweather.R;
import com.sj.cityweather.model.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rk on 7/11/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "cityweather";

    // City table name
    private static final String TABLE_CITY = "city";

    // City Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "cityname";

    private static final String TAG = DataBaseHandler.class.getSimpleName();
    private static String DATABASE_PATH;
    private SQLiteDatabase database;
    private final Context context;
    private SQLiteDatabase db = null;

    public DataBaseHandler(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        Log.d(TAG, "DATABASE_PATH: " + DATABASE_PATH);
        Log.d(TAG, "Context.getFilesDir().getDatabasePath(): " + context.getDatabasePath(DATABASE_NAME).getPath());
        final File dbfile = context.getDatabasePath(DATABASE_NAME);
        Log.d(TAG, "Get database dir: " + dbfile.getAbsolutePath());
        boolean databaseExists = false;
        try {
            databaseExists = checkDataBase();
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e);
        }

        if (databaseExists) {
            Log.d(TAG, "*Local Database Exists*");
        } else {
            Log.d(TAG, "*Setup Local Database*");
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, "Exception: ", e);
                throw new Error("Error copying database");
            } catch (Exception e) {
                Log.e(TAG, "Exception: ", e);
            }
        }
        db = getWritableDatabase();
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        // Check if default db location is valid.
        final File dbfile = context.getDatabasePath(DATABASE_NAME);
        try {
            SQLiteDatabase.openDatabase(dbfile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY).close();
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "SQLiteException: ", e);
        }

        // MSH: Default db location was invalid - lets check secondary location.
        final String MY_PATH = DATABASE_PATH + DATABASE_NAME;
        try {
            SQLiteDatabase.openDatabase(MY_PATH, null, SQLiteDatabase.OPEN_READONLY).close();
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "SQLiteException: ", e);
        }

        return false;
    }

    /**
     * Copies your database FROM your local raw-folder to the just created empty database in the system folder, FROM where it can be accessed and
     * handled. This is done by transfering bytestream.
     */

    private void copyDataBase() throws IOException {
        InputStream input = null;
        FileOutputStream output = null;
        SQLiteDatabase database = null;
        String databasePath = null;
        try {
            try {
                database = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
                databasePath = database.getPath();
            } catch (Exception e) {
                Log.e(TAG, "Exception: ", e);
            }
            if (database != null) {
                database.close();
            }
        } catch (Exception e1) {
            Log.e(TAG, "Exception: ", e1);
        }
        int c;
        byte[] temp;
        try {
            output = new FileOutputStream(databasePath);
            input = context.getResources().openRawResource(R.raw.cityweather);
            temp = new byte[1024];
            while ((c = input.read(temp)) != -1) {
                output.write(temp, 0, c);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
            copyDataBaseOldFlow();
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
                output.flush();
            }
        }
        Log.d(TAG, "Copy database done");
    }

    private void copyDataBaseOldFlow() throws IOException {
        InputStream input = null;
        FileOutputStream output = null;
        SQLiteDatabase database = null;
        try {
            database = context.openOrCreateDatabase(DATABASE_NAME, 0, null);
            if (database != null) {
                database.close();
            }
        } catch (Exception e1) {
            Log.e(TAG, "Exception: ", e1);
        }
        int c;
        byte[] temp;
        try {
            final File databaseFile = new File(DATABASE_PATH, DATABASE_NAME);
            databaseFile.mkdirs();
            databaseFile.createNewFile();
            output = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);
            input = context.getResources().openRawResource(R.raw.cityweather);
            temp = new byte[1024];
            while ((c = input.read(temp)) != -1) {
                output.write(temp, 0, c);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
                output.flush();
            }
        }
    }

    /**
     * TODO: Return a static instance - make sure to test!
     *
     * @return
     */
    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     *
     * @param city
     */

    int count = 10;

    // Adding new City
    public void addCity(City city) {
        count = getCityCount() + 1;
        String query = "INSERT INTO " + TABLE_CITY + " (" + KEY_ID + ", " + KEY_NAME + ") VALUES(?,?)";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, count + "");
        statement.bindString(2, city.getCity() + "");
        statement.execute();
        statement.close();
    }

    // Getting single city
    City getCity(String city) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CITY, new String[]{
                        KEY_NAME}, KEY_NAME + "=?",
                new String[]{city}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        City cityValue = new City(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return city
        return cityValue;
    }

    // Getting All City
    public Cursor getAllCity() {
        // Select All Query
        String selectQuery = "SELECT * FROM city;";
        String[] columns = new String[] { KEY_ID, KEY_NAME };

        Cursor cursor = db.query(TABLE_CITY, columns, null,
                null, null, null, null);
        cursor.moveToFirst();
        // return city list
        Log.v("DATABASE", "Cursor: "+ cursor.getCount());
        return cursor;
    }

    // Updating single city
    public int updateCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, city.getCity());

        // updating row
        return db.update(TABLE_CITY, values, KEY_NAME + " = ?",
                new String[]{city.getCity()});
    }

    // Deleting single city
    public void deleteCity(City city) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CITY, KEY_NAME + " = ?",
                new String[]{city.getCity()});
        //db.close();
    }


    // Getting city Count
    public int getCityCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CITY;
        Cursor cursor = db.rawQuery(countQuery, null);
        // return city
        return cursor.getCount();
    }
}
