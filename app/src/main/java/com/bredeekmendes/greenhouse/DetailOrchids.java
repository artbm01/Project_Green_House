package com.bredeekmendes.greenhouse;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.MonthYearPicker;
import com.bredeekmendes.greenhouse.utilities.OrchidDateUtils;
import com.bredeekmendes.greenhouse.utilities.StringUtils;

/**
 * Created by arthur on 2/4/18.
 */

public class DetailOrchids extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private final Context detailContext = this;
    private Uri orchidUri;
    private TextView mGenus, mSpecies, mGreenhouse, mIsAlive, mDate;
    private View vGenus, vSpecies, vGreenhouse, vIsAlive, vDatetime;
    private Menu menu;
    private Orchid detailedOrchid;

    private static final int ID_ORCHID_LOADER = 77;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_orchid);

        /*Binding views*/
        mGenus = findViewById(R.id.detail_genus);
        mSpecies = findViewById(R.id.detail_species);
        mGreenhouse = findViewById(R.id.detail_greenhouse);
        mIsAlive = findViewById(R.id.detail_is_alive);
        mDate = findViewById(R.id.detail_datetime);
        vGenus = findViewById(R.id.detail_cl_genus);
        vSpecies = findViewById(R.id.detail_cl_species);
        vGreenhouse = findViewById(R.id.detail_cl_greenhouse);
        vIsAlive = findViewById(R.id.detail_cl_is_alive);
        vDatetime = findViewById(R.id.detail_cl_datetime);
        vGenus.setOnLongClickListener(new MyLongClickListener());
        vSpecies.setOnLongClickListener(new MyLongClickListener());
        vGreenhouse.setOnLongClickListener(new MyLongClickListener());
        vIsAlive.setOnLongClickListener(new MyLongClickListener());
        vDatetime.setOnLongClickListener(new MyLongClickListener());

        Intent intentThatStartedThisActivity = getIntent();
        orchidUri = intentThatStartedThisActivity.getData();
        getSupportLoaderManager().initLoader(ID_ORCHID_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {

            case ID_ORCHID_LOADER:

                Intent intentThatStartedThisActivity = getIntent();
                /* URI for all rows of weather data in our weather table */
                Uri orchidQueryUri = intentThatStartedThisActivity.getData();

                return new CursorLoader(this,
                        orchidQueryUri,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        detailedOrchid = new Orchid(data);
        showDetails(detailedOrchid);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_orchid_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_button_menu:
                getContentResolver().delete(orchidUri,null,null);
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.update_button_menu:
                //Makes the button to invisible
                MenuItem updateIcon = menu.findItem(R.id.update_button_menu);
                updateIcon.setVisible(false);
                updateOrchidToContentProvider();
                NavUtils.navigateUpFromSameTask(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            //Shows the update button on the action bar
            MenuItem updateIcon = menu.findItem(R.id.update_button_menu);
            updateIcon.setVisible(true);
            openUserInputDialog(view);
            showDetails(detailedOrchid);
            return true;
        }
    }

    private void openUserInputDialog(View view) {
        switch (view.getId()){
            case R.id.detail_cl_is_alive:
                alivePrompt();
                break;
            case R.id.detail_cl_datetime:
                final MonthYearPicker picker;
                picker = new MonthYearPicker(DetailOrchids.this);
                picker.build(new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = "01/"+(picker.getSelectedMonth()+1)+"/"+picker.getSelectedYear();
                        detailedOrchid.setDate(OrchidDateUtils.getDateInMillis(date));
                        showDetails(detailedOrchid);
                    }
                }, null);
                picker.show();
                break;
            default:
                textPrompt(view);
                break;
            }
    }

    /**
     * This method calls a prompt for the user to change TEXT in orchid data
     */
    private void textPrompt(View view){
        final View thisView = view;
        LayoutInflater li = LayoutInflater.from(detailContext);
        View promptsView = li.inflate(R.layout.text_prompt, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                detailContext);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.text_user_input);
        TextView titlePrompt = promptsView.findViewById(R.id.title_text_prompt);
        switch (thisView.getId()){
            case R.id.detail_cl_genus:
                titlePrompt.setText("Type in the new genus:");
                break;
            case R.id.detail_cl_species:
                titlePrompt.setText("Type in the new species:");
                break;
            case R.id.detail_cl_greenhouse:
                titlePrompt.setText("Type in the new greenhouse:");
                break;
        }
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                switch (thisView.getId()){
                                    case R.id.detail_cl_genus:
                                        detailedOrchid.setStrGenus(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                    case R.id.detail_cl_species:
                                        detailedOrchid.setStrSpecies(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                    case R.id.detail_cl_greenhouse:
                                        detailedOrchid.setStrGreenhouse(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                }
                                showDetails(detailedOrchid);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * This method calls a prompt for the user to change IS_ALIVE in orchid data
     */
    private void alivePrompt(){

        AlertDialog.Builder aliveDialogBuilder = new AlertDialog.Builder(
                detailContext);
        aliveDialogBuilder.setTitle("Type in the orchid status:");
        aliveDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Alive",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                               detailedOrchid.setStatus(1);
                                showDetails(detailedOrchid);
                                dialog.cancel();
                                }
                            }
                        )
                .setNegativeButton("Dead",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                detailedOrchid.setStatus(0);
                                showDetails(detailedOrchid);
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = aliveDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Takes the object data and updates the database with the values
     */
    private void updateOrchidToContentProvider(){
        ContentValues cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS,detailedOrchid.getGenus());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES,detailedOrchid.getSpecies());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE,detailedOrchid.getGreenhouse());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE,detailedOrchid.getStatus());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP,detailedOrchid.getDate());
        getContentResolver().update(orchidUri,cv,null,null);
        Toast.makeText(detailContext, "Orchid updated!", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method populates the orchid object data into the appropriate views and sets the view
     * visibility to GONE in case the field is empty
     * @param orchid the orchid object with all the orchid details
     */
    private void showDetails(Orchid orchid) {
        mGenus.setText(detailedOrchid.getStrGenus());
        mSpecies.setText(detailedOrchid.getStrSpecies());
        mGreenhouse.setText(detailedOrchid.getStrGreenhouse());
        if (mGreenhouse.getText()==null || mGreenhouse.getText()==""){
            vGreenhouse.setVisibility(View.GONE);
        }
        mIsAlive.setText(detailedOrchid.getStrStatus());
        mDate.setText(detailedOrchid.getStrDate());
    }
}
