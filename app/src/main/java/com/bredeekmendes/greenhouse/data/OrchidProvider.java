package com.bredeekmendes.greenhouse.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by arthur on 2/21/18.
 */

public class OrchidProvider extends ContentProvider{

    /*
    * Constants that will be used in the URI matcher to identify the correct URI
    */
    public static final int CODE_ALL_ORCHIDS = 100;
    public static final int CODE_ORCHID_WITH_ID = 101;

    /*
     * The URI Matcher used by this content provider.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private OrchidDbHelper mOrchidDBHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_WEATHER and
     * CODE_WEATHER_WITH_DATE constants defined above.
     * <p>
     * It's possible you might be thinking, "Why create a UriMatcher when you can use regular
     * expressions instead? After all, we really just need to match some patterns, and we can
     * use regular expressions to do that right?" Because you're not crazy, that's why.
     * <p>
     * UriMatcher does all the hard work for you. You just have to tell it which code to match
     * with which URI, and it does the rest automagically. Remember, the best programmers try
     * to never reinvent the wheel. If there is a solution for a problem that exists and has
     * been tested and proven, you should almost always use it unless there is a compelling
     * reason not to.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_WEATHER and CODE_WEATHER_WITH_DATE
     */
    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = OrchidDbContract.CONTENT_AUTHORITY;

        /* This URI is content://com.bredeekmendes.greenhouse/orchids/ */
        matcher.addURI(authority, OrchidDbContract.PATH_ORCHIDS, CODE_ALL_ORCHIDS);

        /*
         * This URI would look something like content://com.bredeekmendes.greenhouse/orchids/10
         */
        matcher.addURI(authority, OrchidDbContract.PATH_ORCHIDS + "/#", CODE_ORCHID_WITH_ID);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called
     * at application launch time.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mOrchidDBHelper = new OrchidDbHelper(getContext());
        return true;
    }

    /**
     * Handles query requests from clients. This is used in Project Greenhouse to query for all
     * the orchid data
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;

        /*
         * Here's the switch statement that, given a URI, will determine what kind of request is
         * being made and query the database accordingly.
         */
        switch (sUriMatcher.match(uri)) {

            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.android.sunshine/weather/1472214172
             *
             * sUriMatcher's match method will return the code that indicates what orchid data
             * is required based on it's ID.
             */
            case CODE_ORCHID_WITH_ID: {

                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};

                cursor = mOrchidDBHelper.getReadableDatabase().query(
                        /* Table we are going to query */
                        OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME,
                        projection,
                        OrchidDbContract.OrchidDataBaseEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * When sUriMatcher's match method is called with a URI that looks EXACTLY like this
             *
             *      content://com.bredeekmendes.greenhouse/orchids
             *
             * sUriMatcher's match method will return the code that indicates that all orchids data
             * is required.
             * The returned cursor will contains every row of orchid data in the orchid table.
             */
            case CODE_ALL_ORCHIDS: {
                cursor = mOrchidDBHelper.getReadableDatabase().query(
                        OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert TEXT
     *
     * @param uri    The URI of the insertion request. This must not be null.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be null
     * @return nothing in Sunshine, but normally the URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case CODE_ALL_ORCHIDS:
                long id = mOrchidDBHelper.getWritableDatabase().insert(
                        OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME,
                        null,
                        values
                );
            if (id>0){
                returnUri = ContentUris.withAppendedId(OrchidDbContract.OrchidDataBaseEntry.CONTENT_URI, id);
            } else{
                throw new UnsupportedOperationException("Unknown URI: "+ uri);
            }
            break;
            default: throw new UnsupportedOperationException("Unknown URI: "+ uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        /* Users of the delete method will expect the number of rows deleted to be returned. */
        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_ALL_ORCHIDS:
                numRowsDeleted = mOrchidDBHelper.getWritableDatabase().delete(
                        OrchidDbContract.OrchidDataBaseEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
