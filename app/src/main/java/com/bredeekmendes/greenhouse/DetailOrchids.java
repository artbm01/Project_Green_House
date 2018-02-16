package com.bredeekmendes.greenhouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by arthur on 2/4/18.
 */

public class DetailOrchids extends AppCompatActivity {

    TextView mGenus;
    TextView mSpecies;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orchid_details_page);

        mGenus = findViewById(R.id.genus_detail);
        mSpecies = findViewById(R.id.species_detail);

        String[] values;

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("OrchidDetails")) {
                values = intentThatStartedThisActivity.getStringArrayExtra("OrchidDetails");
                loadDetailData(values);
            }
        }
    }

    private void loadDetailData(String[] value){
        mGenus.setText(value[0]);
        mSpecies.setText(value[1]);
    }

}
