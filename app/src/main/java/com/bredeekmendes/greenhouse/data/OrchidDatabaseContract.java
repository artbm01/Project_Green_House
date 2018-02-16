package com.bredeekmendes.greenhouse.data;

import android.provider.BaseColumns;

/**
 * Contract for orchid data base
 */

public class OrchidDatabaseContract {


    // This class defines the strings containing the table name and their columns names
    public static final class OrchidDataBaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "orchidDataBase";
        public static final String COLUMN_GREENHOUSE = "greenhouse";
        public static final String COLUMN_GENUS = "genus";
        public static final String COLUMN_SPECIES = "species";
        public static final String COLUMN_IS_ALIVE = "is_alive";
        public static final String COLUMN_TIMESTAMP = "timestamp";

    }

}
