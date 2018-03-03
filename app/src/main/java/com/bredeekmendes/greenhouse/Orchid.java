package com.bredeekmendes.greenhouse;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.StringUtils;

import java.text.SimpleDateFormat;

/**
 * Created by arthur on 2/28/18.
 */

public class Orchid {


    private String mGenus;
    private String mSpecies;
    private String mGreenhouse;
    private int mStatus;
    private long mDate;

    /**
     * Creates an Orchid object and initializes the field values to default cases
     * @param cursor the cursor pointing to row which values will be used to create an orchid object
     */
    public Orchid (Cursor cursor){
        cursor.moveToFirst();
        if (cursor.isNull(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS))){
            mGenus = "Unknown genus";
        }
        else {
            mGenus = cursor.getString(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS));
            if (mGenus.isEmpty() || mGenus==""){
                mGenus = "Unknown genus";
            }
        }
        if (cursor.isNull(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES))){
            mSpecies = "Spec";
        }
        else {
            mSpecies = cursor.getString(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES));
            if (mSpecies.isEmpty() || mSpecies==""){
                mSpecies = "Spec";
            }
        }
        mGreenhouse = cursor.getString(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES));
        if (mGreenhouse.isEmpty() || mGreenhouse==""){
                mGreenhouse = null;
        }
        int isAlive = cursor.getInt(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE));
            mStatus = isAlive;
        if(cursor.isNull(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP))){
            mDate = System.currentTimeMillis();
        }
        else mDate = cursor.getLong(cursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP));
    }

    public Orchid (Uri uri){

    }


    /*
    These methods deal with the Orchid object field values in the raw form as they are available
    in the content provider table
     */

    /**
     * Gets the raw genus data from the table
     * @return mGenus the genus name
     */
    public String getGenus() {
        return StringUtils.normalizeString(mGenus);
    }

    /**
     * Gets the raw species data from the table
     * @return mSpecies the species name
     */
    public String getSpecies() {
        return StringUtils.normalizeString(mSpecies);
    }

    /**
     * Gets the raw greenhouse data from the table
     * @return mGreenhouse the greenhouse the orchid belongs to
     */
    public String getGreenhouse() {
        return StringUtils.normalizeString(mGreenhouse);
    }

    /**
     * Gets the raw status data from the table
     * @return mStatus is true if the orchid is alive and false uf the orchid is dead
     */
    public int getStatus() {
        return mStatus;
    }

    /**
     * Gets the raw orchid collection date from the table
     * @return mDate returns the date in Millis when the orchid was purchased/collected
     */
    public long getDate() {
        return mDate;
    }


    /*
    These methods access the String values of the field variables
     */
    /**
     * Gets the Genus data from the table
     * @return orchid genus in String format
     */
    public String getStrGenus() {
        return mGenus;
    }

    /**
     * Gets the Genus data from the table
     * @return orchid species in String format
     */
    public String getStrSpecies() {
        return mSpecies;
    }

    /**
     * Gets the Greenhouse data from the table
     * @return orchid greenhouse in String format
     */
    public String getStrGreenhouse() {
        return mGreenhouse;
    }

    /**
     * Gets the Status data from the table
     * @return orchid status in String format
     */
    public String getStrStatus() {
        if (mStatus==0) {
            return "Dead";
        }
        else {
            return "Alive";
        }
    }

    /**
     * Gets the Date data from the table
     * @return orchid data in String format, showing Month and Year
     */
    public String getStrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM/yyyy");
        String strDate = formatter.format(mDate);
        return strDate;
    }

    /*
    These methods set the field values of the Orchid object
     */
    /**
     * Sets the Genus data for an Orchid object
     * @param mGenus the orchid genus
     */
    public void setGenus(String mGenus) {
        this.mGenus = mGenus;
    }

    /**
     * Sets the Species data for an Orchid object
     * @param mSpecies the orchid species
     */
    public void setSpecies(String mSpecies) {
        this.mSpecies = mSpecies;
    }

    /**
     * Sets the Grennhouse data for an Orchid object
     * @param mGreenhouse the orchid greenhouse
     */
    public void setGreenhouse(String mGreenhouse) {
        this.mGreenhouse = mGreenhouse;
    }

    /**
     * Sets the Species data for an Orchid object
     * @param mStatus the orchid status
     */
    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    /**
     * Sets the Date data for an Orchid object
     * @param mDate the orchid date
     */
    public void setDate(long mDate) {
        this.mDate = mDate;
    }

    /*
    These are auxiliary methods for the Orchid Object
     */

    /**
     * This method returns the orchid object parameters in content values, ready for a insert
     * @return cv a ContentValue variable with all the Orchid Object data
     */
    public ContentValues getOrchidContentValues(){
        ContentValues cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, mGenus);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, mSpecies);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE, mGreenhouse);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE, mStatus);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP, mDate);
        return cv;
    }

    public void setStrGenus(String genus) {
        this.mGenus = genus;
    }
    public void setStrSpecies(String species) {
        this.mSpecies = species;
    }
    public void setStrGreenhouse(String greenhouse) {
        this.mGreenhouse = greenhouse;
    }
    public void setStrStatus(String status) {
        if (status=="Dead"||status=="dead"||status=="Deady"){
            mStatus=0;
        }
        else{
            mStatus=1;
        }
    }
    public void setStrDate(String date){

    }
}
