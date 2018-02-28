package com.bredeekmendes.greenhouse;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
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
import com.bredeekmendes.greenhouse.utilities.MonthYearPicker;
import com.bredeekmendes.greenhouse.utilities.OrchidDateUtils;
import com.bredeekmendes.greenhouse.utilities.ViewsUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class InsertOrchidActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mGenus, mSpecies, mGreenhouse, mDate;
    View Date;
    RadioButton mYes, mNo;
    private long datePicked=0;

    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private SimpleDateFormat dateFormatter;
    private MonthYearPicker myp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_orchid);

        bindViews();
        Date = findViewById(R.id.date_test);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        mDate.setOnClickListener(this);

        myp = new MonthYearPicker(this);
        myp.build(new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDate.setText(myp.getSelectedMonthShortName() + " / " + myp.getSelectedYear());
                String date = "01/"+(myp.getSelectedMonth()+1)+"/"+myp.getSelectedYear();
                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    long dateInMillis = date1.getTime();
                    datePicked = dateInMillis;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }, null);

    }


    @Override
    public void onClick(View view) {
        ViewsUtil.hideKeyboard(this);
        myp.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_orchid_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.insert_button_menu:
                long date = System.currentTimeMillis();
                Uri uri = getContentResolver().
                        insert(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI,
                                textEditsToContentValues());
                clearTextViews();
                ViewsUtil.hideKeyboard(this);
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
        long date;
        if (datePicked==0){
            date= System.currentTimeMillis();
        }
        else date = datePicked;
        Toast.makeText(InsertOrchidActivity.this,Long.toString(date),Toast.LENGTH_SHORT).show();
        Log.d("Debug",Long.toString(date));
        if (mNo.isChecked() && !(mNo.isChecked())){
            isAlive = "No";
        } else {isAlive = "Yes";}
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS, orchidGenus);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES, orchidSpecies);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GREENHOUSE, orchidGreenhouse);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_IS_ALIVE, isAlive);
        cv.put(OrchidDbContract.OrchidDataBaseEntry.COLUMN_TIMESTAMP,date);
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

    private void bindViews(){
        mGenus = findViewById(R.id.insert_orchid_genus);
        mSpecies = findViewById(R.id.insert_orchid_species);
        mGreenhouse = findViewById(R.id.insert_orchid_greenhouse);
        mYes = findViewById(R.id.insert_orchid_radio_yes);
        mNo = findViewById(R.id.insert_orchid_radio_no);
        mDate = findViewById(R.id.insert_orchid_date);
    }

}
