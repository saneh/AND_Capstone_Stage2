package com.example.sanehyadav1.fitnotes.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Workout Contract class
 */

public class WorkoutContract {
    public static final String CONTENT_AUTHORITY = com.example.sanehyadav1.fitnotes.BuildConfig.AUTHORITY;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_WORKOUT = "workout";

    //Table for workout as input by the user
    public static final class WorkoutEntry implements BaseColumns{

        /*Base content URI used to query the workout table from content provider*/
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                                                .appendPath(PATH_WORKOUT)
                                                .build();
        /*Used internally as the name of the table*/
        public static final String TABLE_NAME = "workout";

        public static final String COLUMN_DATE = "date";

        public static final String COLUMN_WORKOUT_CATEGORY = "workout_category";

        public static final String COLUMN_WORKOUT_NAME = "workout_name_ref";

        public static final String COLUMN_DIST_IN_METERS = "dist_in_meters";

        public static final String COLUMN_WEIGHT_IN_KG = "weight_in_kgs";

        public static final String COLUMN_TIME_IN_MINS = "time_in_mins";

        public static final String COLUMN_REP_COUNT = "reps";

        public static final String COLUMN_IS_WORKOUT_COMPLETE = "is_workout_complete";



        public static Uri buildWorkoutUriWithDate(long date){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }


    }


}
