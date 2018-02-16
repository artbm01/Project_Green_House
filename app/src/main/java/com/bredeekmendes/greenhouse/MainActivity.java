package com.bredeekmendes.greenhouse;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OrchidAdapter.ListItemClickListener {



    private RecyclerView mRecyclerView;
    private OrchidAdapter mOrchidAdapter;

    String[] genus = {"laelia", "laelia", "catleya", "epidendrum","laelia", "laelia", "catleya", "epidendrum"};
    String[] species = {"labiata", "purpurata", "brasiliense", "lala","labiata", "purpurata", "brasiliense", "lala"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_orchids_main);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mOrchidAdapter = new OrchidAdapter(this);
        mRecyclerView.setAdapter(mOrchidAdapter);

        loadData(genus, species);

        //Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

    }

    private void loadData(String[]genus, String[]species) {
        mOrchidAdapter.setGenusData(genus);
        mOrchidAdapter.setSpeciesData(species);

    }

    @Override
    public void onListItemClick(String genus, String species) {
        Context context = this;
        Toast.makeText(context, genus + " " + species, Toast.LENGTH_SHORT)
                .show();
        Intent intentDetailActivity = new Intent(this, DetailOrchids.class);
        String[] orchidDetails = {genus, species};
        intentDetailActivity.putExtra("OrchidDetails", orchidDetails);
        startActivity(intentDetailActivity);

    }


}
