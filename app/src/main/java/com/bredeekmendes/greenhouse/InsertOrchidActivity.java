package com.bredeekmendes.greenhouse;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;


public class InsertOrchidActivity extends AppCompatActivity {

    EditText mGenus;
    EditText mSpecies;
    EditText mGreenhouse;
    Boolean isAlive;
    RadioButton mYes;
    RadioButton mNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_orchid);

        mGenus = findViewById(R.id.insert_orchid_genus);
        mSpecies = findViewById(R.id.insert_orchid_species);
        mGreenhouse = findViewById(R.id.insert_orchid_greenhouse);
        mYes = findViewById(R.id.insert_orchid_radio_yes);
        mNo = findViewById(R.id.insert_orchid_radio_no);
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
            case R.id.add_orchid:
                Log.d("Debug", "clickedtoadd");
                Uri uri = getContentResolver().
                        insert(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI,
                                textEditsToContentValues());
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

/*    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.insert_orchid_radio_yes:
                if (checked)
                    isAlive = true;
                    break;
            case R.id.insert_orchid_radio_no:
                if (checked)
                    isAlive = false;;
                    break;
        }
    }*/

}
