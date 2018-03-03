package com.bredeekmendes.greenhouse.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bredeekmendes.greenhouse.data.OrchidDbContract.*;



/**
 * Created by arthur on 1/30/18.
 */

public class OrchidDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "orchids.db";
    private static final int DATABASE_VERSION = 10;

    public OrchidDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

/*
    This method creates the database as defined by its columns.
*/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Creates a String query called SQL_CREATE_ORCHID_DATABASE that will create the table
        // to hold orchid data

        final String SQL_CREATE_ORCHID_DATABASE =        "CREATE TABLE "+
                OrchidDataBaseEntry.TABLE_NAME         + " (" +
                OrchidDataBaseEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                OrchidDataBaseEntry.COLUMN_GENUS       + " TEXT, " +
                OrchidDataBaseEntry.COLUMN_SPECIES     + " TEXT, " +
                OrchidDataBaseEntry.COLUMN_GREENHOUSE  + " TEXT, " +
                OrchidDataBaseEntry.COLUMN_IS_ALIVE    + " INT DEFAULT 1, " +
                OrchidDataBaseEntry.COLUMN_TIMESTAMP   + " INT" +
                                                         ");";

        //Creates the table
        sqLiteDatabase.execSQL(SQL_CREATE_ORCHID_DATABASE);

    }
/*
This method provides and update of the database, when new columns are added. The database version has to be changed for this method to be called.
*/
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OrchidDataBaseEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
