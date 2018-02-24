package com.bredeekmendes.greenhouse;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bredeekmendes.greenhouse.data.OrchidDbContract;
import com.bredeekmendes.greenhouse.utilities.StringUtils;

/**
 * Created by arthur on 2/3/18.
 */

public class OrchidAdapter extends RecyclerView.Adapter<OrchidAdapter.OrchidAdapterViewHolder>{

    private static final String TAG = OrchidAdapter.class.getSimpleName();
    private final ListItemClickListener mListItemClickListener;
    private Cursor mCursor;


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
            mCursor.moveToPosition(adapterPosition);
            long id = mCursor.getLong(MainActivity.INDEX_ID);
            mListItemClickListener.onListItemClick(id);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick (long id);
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
        mCursor.moveToPosition(position);
        holder.mGenusTextView.setText(StringUtils.normalizeString(mCursor.getString(mCursor
                .getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_GENUS))));
        holder.mSpeciesTextView.setText(StringUtils.normalizeString(mCursor.getString(mCursor
                .getColumnIndex(OrchidDbContract.OrchidDataBaseEntry.COLUMN_SPECIES))));
    }

    //this should return the table size
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
