package com.bredeekmendes.greenhouse;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;

public class MainActivity extends AppCompatActivity implements OrchidAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{



    private RecyclerView mRecyclerView;
    private OrchidAdapter mOrchidAdapter;

    private static final int ID_ORCHID_LOADER = 44;
    public static final String[] MAIN_FORECAST_PROJECTION = {
            OrchidDbContract.OrchidDataBaseEntry._ID,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS,
            OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES
    };

    public static final int INDEX_ID = 0;
    public static final int INDEX_GENUS = 1;
    public static final int INDEX_SPECIES = 2;

    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_orchids_main);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mOrchidAdapter = new OrchidAdapter(this);
        mRecyclerView.setAdapter(mOrchidAdapter);

        Log.d("Debug","before fake data");

       /* insertFakeData("laelia", "labiata");
        insertFakeData("laelia", "purpurata");
        insertFakeData("catleya", "brasiliense");
        insertFakeData("epidendrum", "lala");
        insertFakeData("laelia", "labiata");*/


        Log.d("Debug","after fake data");
        showLoading();
        getSupportLoaderManager().initLoader(ID_ORCHID_LOADER, null, this);

    }

    private void insertFakeData(String genus, String species) {
        ContentValues cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, genus);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, species);
        getContentResolver().insert(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI,cv);
    }

    @Override
    public void onListItemClick(long id) {
        Intent intentDetailActivity = new Intent(MainActivity.this, DetailOrchids.class);
        Uri uriForOrchidClicked = OrchidDbContract.OrchidDataBaseEntry.buildWeatherUriWithId(id);
        intentDetailActivity.setData(uriForOrchidClicked);
        startActivity(intentDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.add_orchid:
                Intent intentToInsertOrchid = new Intent(this,
                        InsertOrchidActivity.class);
                startActivity(intentToInsertOrchid);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_ORCHID_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri orchidQueryUri = OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = OrchidDbContract.OrchidDataBaseEntry._ID + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */

                return new CursorLoader(this,
                        orchidQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mOrchidAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWeatherDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mOrchidAdapter.swapCursor(null);
    }

    private void showWeatherDataView(){
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
