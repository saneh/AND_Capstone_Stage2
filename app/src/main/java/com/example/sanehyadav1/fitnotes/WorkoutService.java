package com.example.sanehyadav1.fitnotes;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.sanehyadav1.fitnotes.data.WorkoutContract;
import com.example.sanehyadav1.fitnotes.utilities.WorkoutDateUtilities;

/**
 * Created by sanehyadav1 on 7/3/17.
 */

public class WorkoutService extends IntentService{

    public static final String ACTION_GET_TODAYS_WORKOUT = "com.example.sanehyadav1.fitnotes.action_get_today_workout";
    private static final String WORKOUT_COUNT = "COUNT("+ WorkoutContract.WorkoutEntry._ID+")";
    private String[] COULMN_PROJECTION = new String[]{
            WORKOUT_COUNT
    };
    private static final int INDEX_WORKOUT_CATEGORY = 0;
    private static final int INDEX_WORKOUT_COUNT = 1;

    public WorkoutService(){
        super("Workout Service");
    }

    /*
    Starts this service to get today's workout details
     */
    public static void startActionGetTodaysWorkout(Context context){
        Intent intent = new Intent(context,WorkoutService.class);
        intent.setAction(ACTION_GET_TODAYS_WORKOUT);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if(ACTION_GET_TODAYS_WORKOUT.equals(action)){
                handleActionGetTodaysWorkout();
            }

        }
    }

    private void handleActionGetTodaysWorkout(){
        Uri TODAYS_WORKOUT_URI  = WorkoutContract.WorkoutEntry.CONTENT_URI.buildUpon()
                                .appendPath(String.valueOf(WorkoutDateUtilities.normalizeDate(System.currentTimeMillis())))
                                .build();
        Cursor cursor = getContentResolver().query(TODAYS_WORKOUT_URI,
                            COULMN_PROJECTION,
                            null,
                            null,
                            null);
        int todays_workout_count=0;

        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            todays_workout_count = cursor.getInt(0);
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WorkoutWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.tv_widget);
        //Now update all widgets
        WorkoutWidgetProvider.updateWorkoutWidgets(this, appWidgetManager, todays_workout_count,appWidgetIds);
    }
}
