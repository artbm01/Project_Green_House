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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    private final Context detailContext = this;
    private Uri orchidUri;
    private TextView mGenus, mSpecies, mGreenhouse, mIsAlive, mDatetime;
    private View vGenus, vSpecies, vGreenhouse, vIsAlive, vDatetime;
    private Menu menu;

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
        mDatetime = findViewById(R.id.detail_datetime);
        vGenus = findViewById(R.id.detail_cl_genus);
        vSpecies = findViewById(R.id.detail_cl_species);
        vGreenhouse = findViewById(R.id.detail_cl_greenhouse);
        vIsAlive = findViewById(R.id.detail_cl_is_alive);
        vDatetime = findViewById(R.id.detail_cl_datetime);
        vGenus.setOnLongClickListener(new MyLongClickListener());
        vSpecies.setOnLongClickListener(new MyLongClickListener());
        vGreenhouse.setOnLongClickListener(new MyLongClickListener());
        vIsAlive.setOnLongClickListener(new MyLongClickListener());

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
                updateAllFields();
                NavUtils.navigateUpFromSameTask(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String dateFormatting(String date) throws ParseException {
        Date dateParsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date);
        String dateFormatted = new SimpleDateFormat("MM-dd-yyyy").format(dateParsed);
        return dateFormatted;
    }

    private class MyLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            //Shows the update button on the action bar
            MenuItem updateIcon = menu.findItem(R.id.update_button_menu);
            updateIcon.setVisible(true);
            if (view.getId()==vIsAlive.getId()){
                alivePrompt();
            }
            else {textPrompt(view);}
            return true;
        }
    }

    /**
     * Handles query requests from clients. This is used in Project Greenhouse to query for all
     * the orchid data
     *
     * @param textView           Detail that shows up on the card
     * @param value              Title that shows up on the card
     * @param view               The view that contains the card
     */
    private void loadDetailData(TextView textView, String value, View view){
        value = StringUtils.normalizeString(value);
        if (value.isEmpty() || value==""){
            view.setVisibility(View.GONE);
        }
        textView.setText(StringUtils.normalizeString(value));
    }

    /**
     * This method calls a prompt for the user to change TEXT in orchid data
     */
    private void textPrompt(View view){
        final View thisView = view;
        LayoutInflater li = LayoutInflater.from(detailContext);
        View promptsView = li.inflate(R.layout.text_prompt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                detailContext);

        alertDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.text_user_input);
        final TextView titlePrompt = promptsView.findViewById(R.id.title_text_prompt);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                switch (thisView.getId()){
                                    case R.id.detail_cl_genus:
                                        titlePrompt.setText("Genus:");
                                        mGenus.setText(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                    case R.id.detail_cl_species:
                                        titlePrompt.setText("Species:");
                                        mSpecies.setText(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                    case R.id.detail_cl_greenhouse:
                                        titlePrompt.setText("Greenhouse:");
                                        mGreenhouse.setText(StringUtils.normalizeString(userInput.getText()));
                                        break;
                                }
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
        LayoutInflater li = LayoutInflater.from(detailContext);
        View promptsView = li.inflate(R.layout.alive_prompt, null);

        final RadioGroup group = promptsView.findViewById(R.id.radio_group);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                detailContext);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Log.d("Debug",Integer.toString(group.getCheckedRadioButtonId()));
                                switch (group.getCheckedRadioButtonId()){
                                    case R.id.alive_yes:
                                        mIsAlive.setText("Yes");
                                        break;
                                    case R.id.alive_no:
                                        mIsAlive.setText("No");
                                        break;
                                }
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
     * Takes the data currently showing on the TextViews and updates the database with the values
     */
    private void updateAllFields(){
        ContentValues cv = new ContentValues();
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS,mGenus.getText().toString());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES,mSpecies.getText().toString());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE,mGreenhouse.getText().toString());
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE,mIsAlive.getText().toString());
        getContentResolver().update(orchidUri,cv,null,null);
        Toast.makeText(detailContext, "Orchid updated!", Toast.LENGTH_SHORT).show();
    }
}
