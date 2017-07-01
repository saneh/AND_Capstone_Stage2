package com.example.sanehyadav1.fitnotes.utilities;

import android.content.Context;
import android.content.res.TypedArray;

import com.example.sanehyadav1.fitnotes.R;

/**
 * Workout utitlity class
 */

public class WorkoutUtilities {

    //Method for 'complete workout name'
    public static String getCompleteWorkoutName(String workout_category,String workout_name){
        return internalWorkoutName(workout_category,workout_name);
    }

    // Method for computing internal workout name from string resources
    private static String internalWorkoutName(String workout_category,String workout_name){
        String lower_workout_category = workout_category.toLowerCase();
        String lower_workout_name = workout_name.toLowerCase();
        lower_workout_name = lower_workout_name.replace("-"," ");
        String internal_workout_name = lower_workout_category.replace(" ","_") + "_"+lower_workout_name.replace(" ","_");
        return internal_workout_name;
    }

    //Method to fetch a random image for widget
    public static String getWidgetImageUrl(Context context){
        String workout_image;
        TypedArray ta = context.getResources().obtainTypedArray(R.array.widget_images);
        int id = ta.getResourceId((int)Math.random()*20, 0);
        if (id > 0) {
            workout_image = context.getResources().getString(id);
            return workout_image;
        } else {
            return null;
        }
    }

}
