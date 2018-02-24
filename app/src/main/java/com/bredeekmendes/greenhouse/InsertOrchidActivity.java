package com.bredeekmendes.greenhouse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog.OnDateSetListener;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.OrchidDateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class InsertOrchidActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mGenus;
    EditText mSpecies;
    EditText mGreenhouse;
    RadioButton mYes;
    RadioButton mNo;
    EditText mDate;
    private DatePickerDialog mDatePickerDialog;

    private DatePickerDialog.OnDateSetListener mDateListener;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_orchid);

        bindViews();

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mDate.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view) {
        mDatePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_orchid_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("Debug", "buttonclicked");
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.insert_button_menu:
                long date = OrchidDateUtils.getNormalizedUtcDateForToday();
                Uri uri = getContentResolver().
                        insert(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI,
                                textEditsToContentValues());
                clearTextViews();
                Toast.makeText(this,uri.getLastPathSegment(),Toast.LENGTH_LONG).show();
                hideKeyboard(this);
                Toast.makeText(this, "Orchid added!", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues textEditsToContentValues(){
        ContentValues cv = new ContentValues();
        String orchidGenus = mGenus.getText().toString();
        String orchidSpecies = mSpecies.getText().toString();
        String orchidGreenhouse = mGreenhouse.getText().toString();
        String isAlive;
        if (mNo.isChecked() && !(mNo.isChecked())){
            isAlive = "No";
        } else {isAlive = "Yes";}
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, orchidGenus);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, orchidSpecies);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE, orchidGreenhouse);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE, isAlive);
        Log.d("Debug", "sending values to cv");
        return cv;
    }

    private void clearTextViews(){
        mGenus.setText("");
        mGenus.clearFocus();
        mSpecies.setText("");
        mSpecies.clearFocus();
        mGreenhouse.setText("");
        mGreenhouse.clearFocus();
        mNo.setChecked(true);
        mNo.setChecked(false);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void bindViews(){
        mGenus = findViewById(R.id.insert_orchid_genus);
        mSpecies = findViewById(R.id.insert_orchid_species);
        mGreenhouse = findViewById(R.id.insert_orchid_greenhouse);
        mYes = findViewById(R.id.insert_orchid_radio_yes);
        mNo = findViewById(R.id.insert_orchid_radio_no);
        mDate = findViewById(R.id.insert_orchid_date);
    }

}
