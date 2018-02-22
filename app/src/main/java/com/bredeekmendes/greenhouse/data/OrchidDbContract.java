package com.bredeekmendes.greenhouse.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for orchid data base
 */

public class OrchidDbContract {

    /*
    The Content Authority for the content provider
     */
    public static final String CONTENT_AUTHORITY = "com.bredeekmendes.greenhouse";

    /*
    The Base Content URI for the content provider
    */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
    The main path for the content provider
    */
    public static final String PATH_ORCHIDS = "orchid";


    // This class defines the strings containing the table name and their columns names
    public static final class OrchidDataBaseEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Orchid table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ORCHIDS)
                .build();

        public static final String TABLE_NAME = "orchidDataBase";
        public static final String COLUMN_GREENHOUSE = "greenhouse";
        public static final String COLUMN_GENUS = "genus";
        public static final String COLUMN_SPECIES = "species";
        public static final String COLUMN_IS_ALIVE = "is_alive";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        /**
         * Builds a URI that adds the orchid id to the end of the orchid content URI path.
         * This is used to query details about a single orchid entry by id. This is what is
         * going to be used in the detail activity
         *
         * @param id Normalized date in milliseconds
         * @return Uri to query details about a single orchid entry
         */
        public static Uri buildWeatherUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }

    }

}
