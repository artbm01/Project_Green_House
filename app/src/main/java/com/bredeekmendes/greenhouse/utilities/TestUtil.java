package com.bredeekmendes.greenhouse.utilities;

/**
 * Created by arthur on 2/4/18.
 */

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        //create a list of fake guests
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, "Laelia");
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, "jongheana");
        list.add(cv);

        cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, "Cattleya");
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, "purpurata");
        list.add(cv);

        cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, "cattleya");
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, "labiata");
        list.add(cv);

        cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, "epidendrum");
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, "denticulatum");
        list.add(cv);

        cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, "laelia");
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, "Briegeri");
        list.add(cv);

        //insert all guests in one transaction
        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}