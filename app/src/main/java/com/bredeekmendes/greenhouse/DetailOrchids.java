package com.bredeekmendes.greenhouse;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by arthur on 2/4/18.
 */

public class DetailOrchids extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    TextView mGenus;
    TextView mSpecies;
    TextView mGreenhouse;
    TextView mIsAlive;
    TextView mDatetime;
    View vGenus;
    View vSpecies;
    View vGreenhouse;
    View vIsAlive;
    View vDatetime;

    private static final int ID_ORCHID_LOADER = 77;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_orchid_page);



        mGenus = findViewById(R.id.detail_genus);
        mSpecies = findViewById(R.id.detail_species);
        mGreenhouse = findViewById(R.id.detail_greenhouse);
        mIsAlive = findViewById(R.id.detail_is_alive);
        mDatetime = findViewById(R.id.detail_datetime);
        vGenus = findViewById(R.id.detail_cl_genus);
        vSpecies = findViewById(R.id.detail_cl_species);
        vGreenhouse = findViewById(R.id.detail_cl_greenhouse);
        vIsAlive = findViewById(R.id.detail_cl_is_alive);
        vDatetime = findViewById(R.id.detail_cl_datetime);

        getSupportLoaderManager().initLoader(ID_ORCHID_LOADER, null, this);


    }

    private void loadDetailData(TextView textView, String value, View view){
        value = StringUtils.normalizeString(value);
        if (value.isEmpty() || value==""){
            view.setVisibility(View.GONE);
        }
        textView.setText(StringUtils.normalizeString(value));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_ORCHID_LOADER:
                Intent intentThatStartedThisActivity = getIntent();
                /* URI for all rows of weather data in our weather table */
                Uri orchidQueryUri = intentThatStartedThisActivity.getData();
                /* Sort order: Ascending by date */
                String sortOrder = OrchidDbContract.OrchidDataBaseEntry._ID + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                return new CursorLoader(this,
                        orchidQueryUri,
                        null,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            String genus = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS));
            String species = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES));
            String greenhouse = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE));
            String isAlive = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE));
            String datetime = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP));
            loadDetailData(mGenus, genus, vGenus);
            loadDetailData(mSpecies, species, vSpecies);
            loadDetailData(mGreenhouse, greenhouse, vGreenhouse);
            loadDetailData(mIsAlive, isAlive, vIsAlive);
            loadDetailData(mDatetime, datetime, vDatetime);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_orchid_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_button_menu:
                Intent intentThatStartedThisActivity = getIntent();
                Uri orchidQueryUri = intentThatStartedThisActivity.getData();
                String id = orchidQueryUri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};
                getContentResolver().delete(orchidQueryUri,null,
                        null);
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String dateFormatting(String date) throws ParseException {
        Date dateParsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
        String dateFormatted = new SimpleDateFormat("MM-dd-yyyy").format(dateParsed);
        return dateFormatted;
    }

}
