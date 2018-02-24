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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;

public class MainActivity extends AppCompatActivity implements OrchidAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{



    private RecyclerView mRecyclerView;
    private OrchidAdapter mOrchidAdapter;
    private Cursor mCursor;

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


/*        insertFakeData("laelia", "labiata");
        insertFakeData("laelia", "purpurata");
        insertFakeData("catleya", "brasiliense");
        insertFakeData("epidendrum", "lala");
        insertFakeData("laelia", "labiata");*/


        showLoading();
        getSupportLoaderManager().initLoader(ID_ORCHID_LOADER, null, this);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete

                // COMPLETED (1) Construct the URI for the item to delete
                //[Hint] Use getTag (from the adapter code) to get the id of the swiped item
                // Retrieve the id of the task to delete
                int id = viewHolder.getAdapterPosition();
                mCursor.moveToPosition(id);
                String stringId = mCursor.getString(mCursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry._ID));
                // Build appropriate uri with String row id appended
                Uri uri = OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                // COMPLETED (2) Delete a single row of data using a ContentResolver
                getContentResolver().delete(uri, null, null);

                // COMPLETED (3) Restart the loader to re-query for all tasks after a deletion
                getSupportLoaderManager().restartLoader(ID_ORCHID_LOADER, null,
                        MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);

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
        Uri uriForOrchidClicked = OrchidDbContract.OrchidDataBaseEntry.buildOrchidUriWithId(id);
        intentDetailActivity.setData(uriForOrchidClicked);
        Log.e("Debug",uriForOrchidClicked.toString());
        startActivity(intentDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
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
        mCursor = data;
        mOrchidAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        showWeatherDataView();
        if (data.getCount() == 0) {
            Toast.makeText(this, "No orchid to show!", Toast.LENGTH_SHORT).show();
        }
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
