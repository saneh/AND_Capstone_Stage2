package com.example.sanehyadav1.fitnotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.example.sanehyadav1.fitnotes.utilities.WorkoutUtilities;


/**
 * Implementation of App Widget functionality.
 */
public class WorkoutWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int todays_workout_count,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.workout_widget);


        //Create an intent to launch Main Activity when clicked
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        views.setOnClickPendingIntent(R.id.widget_add_workout,pendingIntent);

        //Set textview using workout_count passed

        if(todays_workout_count >0){
            views.setTextViewText(R.id.tv_widget,String.format(context.getString(R.string.non_zero_workout_count_for_today),todays_workout_count));
        }else{
            views.setTextViewText(R.id.tv_widget,context.getString(R.string.no_workouts_for_today));
        }



        //String widget_image_url = WorkoutUtilities.getWidgetImageUrl(context);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        WorkoutService.startActionGetTodaysWorkout(context);
//
    }

    public static void updateWorkoutWidgets(Context context,AppWidgetManager appWidgetManager,int todays_workout_count,int[] appWidgetIds){
        for (int appWidgetId:appWidgetIds){
           updateAppWidget(context,appWidgetManager,todays_workout_count,appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

