package com.bredeekmendes.greenhouse;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OrchidAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{


    private Context context = MainActivity.this;
    private RecyclerView mRecyclerView;
    private OrchidAdapter mOrchidAdapter;
    private Cursor mCursor;
    private ContentValues deletedOrchid;

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
                //Get the item id to create the correct URI
                int id = viewHolder.getAdapterPosition();
                mCursor.moveToPosition(id);
                String stringId = mCursor.getString(mCursor.getColumnIndex(OrchidDbContract.OrchidDataBaseEntry._ID));

                // Build appropriate uri with String row id appended
                Uri uri = OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                //Saves the orchid data for undo purposes
                deletedOrchid = new ContentValues();
                for (int column=0; column<mCursor.getColumnCount(); column++){
                    deletedOrchid.put(mCursor.getColumnName(column),mCursor.getString(column));
                }

                Snackbar snackbar = Snackbar
                        .make(mRecyclerView, "ORCHID REMOVED", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getContentResolver().insert(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI,deletedOrchid);

                            }
                        });
                snackbar.show();

                getContentResolver().delete(uri, null, null);
                mOrchidAdapter.notifyItemRemoved(id);
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
        showOrchidsDataView();
        if (data.getCount() == 0) {
            Toast.makeText(this, "No orchid to show!", Toast.LENGTH_SHORT).show();
        }


/*        Map hashMap = new HashMap();
        Gson gason = new Gson();
        for (int i = 0; i < mCursor.getColumnCount(); i++) {
            hashMap.put(mCursor.getColumnName(i),mCursor.getString(i));
        }
        Log.d("JASON",gason.toJson(hashMap));*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mOrchidAdapter.swapCursor(null);
    }

    private void showOrchidsDataView(){
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

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ID_ORCHID_LOADER, null,
                MainActivity.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(ID_ORCHID_LOADER, null,
                MainActivity.this);
    }
}
