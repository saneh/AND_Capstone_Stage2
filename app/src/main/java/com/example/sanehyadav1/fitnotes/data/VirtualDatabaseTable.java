package com.example.sanehyadav1.fitnotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.sanehyadav1.fitnotes.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * To look up "image url" from a file , virtual table is used
 */


public class VirtualDatabaseTable {
    private static final String TAG = VirtualDatabaseTable.class.getSimpleName();

    //Columns in FitNotes_workouts_table
    public static final String COL_COMPLETE_WORKOUT_NAME = "COMPLETE_WORKOUT_NAME";
    public static final String COL_IMAGE_URL1 = "IMG_URL_1";
    public static final String COL_IMAGE_URL2 = "IMG_URL_2";
    public static final String COL_VIDEO_URL = "VIDEO_URL";
    public static final String COL_IS_DISTANCE = "IS_DISTANCE";
    public static final String COL_IS_WEIGHT = "IS_WEIGHT";
    public static final String COL_IS_TIME = "IS_TIME";
    public static final String COL_IS_REPS = "IS_REPS";

    // Index of different columns, used in fetching values from cursor
    public static final int INDEX_COMPLETE_WORKOUT_NAME = 0;
    public static final int INDEX_IMAGE_URL1 = 1;
    public static final int INDEX_IMAGE_URL2 = 2;
    public static final int INDEX_VIDEO_URL = 3;
    public static final int INDEX_IS_DISTANCE = 4;
    public static final int INDEX_IS_WEIGHT = 5;
    public static final int INDEX_IS_TIME = 6;
    public static final int INDEX_IS_REPS = 7;

    private static final String DATABASE_NAME = "FITNOTES_WORKOUT_RES";
    private static final String FTS_VIRTUAL_FITNOTES_WORKOUT_TABLE = "FTS_FITNOTES_WORKOUT_TABLE";
    private static final int DATABASE_VERSION = 16;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public VirtualDatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }


    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_CREATE_WORKOUT_IMAGE_VID_TABLE = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_FITNOTES_WORKOUT_TABLE +
                " USING fts3 (" +
                COL_COMPLETE_WORKOUT_NAME + ", " +
                COL_IMAGE_URL1 + ", " +
                COL_IMAGE_URL2 + ", " +
                COL_VIDEO_URL + ", " +
                COL_IS_DISTANCE + ", " +
                COL_IS_WEIGHT + ", " +
                COL_IS_TIME + ", " +
                COL_IS_REPS + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_CREATE_WORKOUT_IMAGE_VID_TABLE);
            Log.d(TAG, "Create workout image and video table is called");
            loadExerciseImageVideoData();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_FITNOTES_WORKOUT_TABLE);
            onCreate(db);
        }

        /*
        These methods are for inserting data in image video virtual table
         */
        //load Image Video data
        private void loadExerciseImageVideoData() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadfile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }).start();
        }

        //Method to load data from raw file ( Image video data)
        private void loadfile() throws IOException {
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.fitnotes_workouts_data);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] strings = TextUtils.split(line, ",");
                    if (strings.length < 8) continue;
                    long id = addWord(strings[0].trim(), strings[1].trim(), strings[2].trim(), strings[3].trim(),
                                    strings[4].trim(),strings[5].trim(),strings[6].trim(),strings[7].trim());

                    if (id < 0) {
                        Log.e(TAG, "unable to add image: " + strings[0].trim());
                    }
                }
            } finally {
                bufferedReader.close();
                mDatabase.close();
            }
        }

        //Method to insert the image and video url values in virtual database
        private long addWord(String complete_workout_name, String image_url1, String image_url2,String video_url,
                             String is_distance,String is_weight, String is_time,String is_reps) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_COMPLETE_WORKOUT_NAME, complete_workout_name);
            initialValues.put(COL_IMAGE_URL1, image_url1);
            initialValues.put(COL_IMAGE_URL2, image_url2);
            initialValues.put(COL_VIDEO_URL,video_url);
            initialValues.put(COL_IS_DISTANCE,is_distance);
            initialValues.put(COL_IS_WEIGHT,is_weight);
            initialValues.put(COL_IS_TIME,is_time);
            initialValues.put(COL_IS_REPS,is_reps);
            return mDatabase.insert(FTS_VIRTUAL_FITNOTES_WORKOUT_TABLE, null, initialValues);
        }
    }

    //Methods to search for workout image url and video in virtual table
    public Cursor getUrlMatches(String query, String[] columns) {
        String selection = COL_COMPLETE_WORKOUT_NAME + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_FITNOTES_WORKOUT_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
