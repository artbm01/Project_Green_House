package com.bredeekmendes.greenhouse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by arthur on 2/3/18.
 */

public class OrchidAdapter extends RecyclerView.Adapter<OrchidAdapter.OrchidAdapterViewHolder>{

    private static final String TAG = OrchidAdapter.class.getSimpleName();
    private String[] mGenus;
    private String[] mSpecies;
    private final ListItemClickListener mListItemClickListener;

    public class OrchidAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        public final TextView mGenusTextView;
        public final TextView mSpeciesTextView;

        public OrchidAdapterViewHolder(View view){
            super(view);
            mGenusTextView = view.findViewById(R.id.tv_genus_data);
            mSpeciesTextView = view.findViewById(R.id.tv_species_data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String genusForPosition = mGenus[adapterPosition];
            String speciesForPosition = mSpecies[adapterPosition];
            mListItemClickListener.onListItemClick(genusForPosition, speciesForPosition);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick (String genusForThePosition, String speciesForThePosition);
    }

    public OrchidAdapter(ListItemClickListener click) {
        mListItemClickListener = click;
    }

    @Override
    public OrchidAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.main_orchid_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new OrchidAdapterViewHolder(view);
    }

    //this is the position of the view holder. Maybe when I have the table i should get items based on the table ID and position
    @Override
    public void onBindViewHolder(OrchidAdapterViewHolder holder, int position) {
        String genusForPosition = mGenus[position];
        String speciesForPosition = mSpecies[position];
        holder.mGenusTextView.setText(genusForPosition);
        holder.mSpeciesTextView.setText(speciesForPosition);
    }

    //this should return the table size
    @Override
    public int getItemCount() {
        if (null == mSpecies) return 0;
        return 5;
    }

    public void setGenusData(String[] genus) {
        mGenus = genus;
        notifyDataSetChanged();
    }

    public void setSpeciesData(String[] species) {
        mSpecies = species;
        notifyDataSetChanged();
    }
}
