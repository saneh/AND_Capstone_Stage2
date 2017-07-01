package com.example.sanehyadav1.fitnotes.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Content Provider
 */

public class WorkoutProvider extends ContentProvider {

    public static final int CODE_WORKOUTS_WITH_DATE=100;
    public static final int CODE_WORKOUTS = 101;
    public static final String TAG = WorkoutProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WorkoutDbHelper mDbHelper;

    /*
    @return a UriMatcher that correctly matches the constant for CODE_WORKOUTS_WITH_DATE
     */
    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkoutContract.CONTENT_AUTHORITY;
        //"/#' if the path is followed by any number it should return CODE_WORKOUTS_WITH_DATE
        matcher.addURI(authority,WorkoutContract.PATH_WORKOUT,CODE_WORKOUTS);
        matcher.addURI(authority,WorkoutContract.PATH_WORKOUT+ "/#",CODE_WORKOUTS_WITH_DATE);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new WorkoutDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch(sUriMatcher.match(uri)){

            case CODE_WORKOUTS_WITH_DATE:{
                String normalizedUtcDateString = uri.getLastPathSegment();

                String[] selectionArguments = new String[]{normalizedUtcDateString};

                //get every workout row in workoutEntry table selected by date and ordered by workout category id
                cursor = mDbHelper.getReadableDatabase().query(
                        WorkoutContract.WorkoutEntry.TABLE_NAME,
                        projection,
                        WorkoutContract.WorkoutEntry.COLUMN_DATE + " =? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder

                );
                break;

            }
            case CODE_WORKOUTS:
                cursor = mDbHelper.getReadableDatabase().query(
                        WorkoutContract.WorkoutEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw  new UnsupportedOperationException("Unknown Uri: "+ uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("GetType is not implemented");
    }
    //Insert is not implemented instead we implement bulkinsert
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase dbWritable = mDbHelper.getWritableDatabase();
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)){
            case CODE_WORKOUTS: //Check if we need a new URI constant or the CODE_WORKOUTS_WITH_DATE will work here also
                dbWritable.beginTransaction();
                try {
                    for(ContentValues value:values){
                       long id= dbWritable.insert(WorkoutContract.WorkoutEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            rowsInserted++;
                        }

                    }
                    dbWritable.setTransactionSuccessful();
                }
                finally {
                    dbWritable.endTransaction();
                }
            default:
                super.bulkInsert(uri,values);
        }
        if(rowsInserted>0){
            getContext().getContentResolver().notifyChange(uri,null);
            Log.d("WorkoutProvider",String.valueOf(rowsInserted)+" rows inserted in database");
        }
        return rowsInserted;
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
       int numRowsDeleted;
        //If selection==null in that case it will delete the entire table. In order to return number of rows deleted
        //in such a case, we need to set selection="1", otherwise this operation will not return the number of row deleted.
        if(null==selection) selection="1";
        switch (sUriMatcher.match(uri)){
            case CODE_WORKOUTS:
                numRowsDeleted=mDbHelper.getWritableDatabase().delete(WorkoutContract.WorkoutEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:"+uri);
        }
        if(numRowsDeleted!=0){
            //If we actually deleted rows , notify that a change has occurred to this uri
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsUpdated;
        switch (sUriMatcher.match(uri)){
            case CODE_WORKOUTS:
                numRowsUpdated = mDbHelper.getWritableDatabase().update(WorkoutContract.WorkoutEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw  new UnsupportedOperationException("Unknown Uri" + uri);
        }
        if(numRowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
            Log.d(TAG,numRowsUpdated + " Row updated");
        }
        return numRowsUpdated;
    }



}
