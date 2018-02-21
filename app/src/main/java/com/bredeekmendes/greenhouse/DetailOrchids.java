package com.bredeekmendes.greenhouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bredeekmendes.greenhouse.utilities.StringUtils;

/**
 * Created by arthur on 2/4/18.
 */

public class DetailOrchids extends AppCompatActivity {

    TextView mGenus;
    TextView mSpecies;
    TextView mGreenhouse;
    TextView mIsAlive;
    View vGreenhouse;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orchid_details_page);

        mGenus = findViewById(R.id.detail_genus);
        mSpecies = findViewById(R.id.detail_species);
        mGreenhouse = findViewById(R.id.detail_greenhouse);
        mIsAlive = findViewById(R.id.detail_is_alive);
        vGreenhouse = findViewById(R.id.detail_cl_greenhouse);

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
        mGenus.setText(StringUtils.normalizeString(value[0]));
        mSpecies.setText(StringUtils.normalizeString(value[1]));
    }

}
