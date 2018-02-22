package com.bredeekmendes.greenhouse;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.StringUtils;
import com.bredeekmendes.greenhouse.utilities.TestUtil;

/**
 * Created by arthur on 2/4/18.
 */

public class DetailOrchids extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    TextView mGenus;
    TextView mSpecies;
    TextView mGreenhouse;
    TextView mIsAlive;
    View vGenus;
    View vSpecies;
    View vGreenhouse;
    View vIsAlive;

    private static final int ID_ORCHID_LOADER = 77;

    public static final String[] MAIN_FORECAST_PROJECTION = {
            OrchidDbContract.OrchidDataBaseEntry._ID,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orchid_details_page);

        mGenus = findViewById(R.id.detail_genus);
        mSpecies = findViewById(R.id.detail_species);
        mGreenhouse = findViewById(R.id.detail_greenhouse);
        mIsAlive = findViewById(R.id.detail_is_alive);
        vGenus = findViewById(R.id.detail_cl_genus);
        vSpecies = findViewById(R.id.detail_cl_species);
        vGreenhouse = findViewById(R.id.detail_cl_greenhouse);
        vIsAlive = findViewById(R.id.detail_cl_is_alive);

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
                Log.d("Debug",orchidQueryUri.toString());
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
            loadDetailData(mGenus, genus, vGenus);
            loadDetailData(mSpecies, species, vSpecies);
            loadDetailData(mGreenhouse, greenhouse, vGreenhouse);
            loadDetailData(mIsAlive, isAlive, vIsAlive);


        }
        else {Log.d("TAG", "not working");}
        //String id = data.getString(data.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry._ID));

        //String greenhouse = data.getString(data.getColumnIndex(MAIN_FORECAST_PROJECTION[3]));
        //String isAlive = data.getString(data.getColumnIndex(MAIN_FORECAST_PROJECTION[4]));


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
