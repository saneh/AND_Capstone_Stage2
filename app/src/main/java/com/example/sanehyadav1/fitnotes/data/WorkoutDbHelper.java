package com.example.sanehyadav1.fitnotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper
 */

public class WorkoutDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fitnotes.db";

    private static final int DATABASE_VERSION = 5;

    private Context mContext;

    private static final String TAG = WorkoutDbHelper.class.getSimpleName();

    public WorkoutDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //getting the application context
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL query to create workout table
        final String SQL_CREATE_WORKOUT_TABLE =
                "CREATE TABLE " + WorkoutContract.WorkoutEntry.TABLE_NAME + " (" +
                        WorkoutContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WorkoutContract.WorkoutEntry.COLUMN_DATE + " INTEGER NOT NULL, "+
                        WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_CATEGORY + " TEXT NOT NULL, " +
                        WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME + " TEXT NOT NULL, " +
                        WorkoutContract.WorkoutEntry.COLUMN_DIST_IN_METERS + " INTEGER, " +
                        WorkoutContract.WorkoutEntry.COLUMN_WEIGHT_IN_KG + " REAL, " +
                        WorkoutContract.WorkoutEntry.COLUMN_TIME_IN_MINS + " INTEGER, " +
                        WorkoutContract.WorkoutEntry.COLUMN_REP_COUNT + " INTEGER, " +
                        WorkoutContract.WorkoutEntry.COLUMN_IS_WORKOUT_COMPLETE + " INTEGER DEFAULT 0 "+
                        ");";
        db.execSQL(SQL_CREATE_WORKOUT_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutContract.WorkoutEntry.TABLE_NAME);
        onCreate(db);
    }

}
