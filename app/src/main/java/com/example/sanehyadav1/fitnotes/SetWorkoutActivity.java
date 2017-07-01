package com.example.sanehyadav1.fitnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.sanehyadav1.fitnotes.data.WorkoutContract;
import com.example.sanehyadav1.fitnotes.utilities.Workout;
import com.example.sanehyadav1.fitnotes.utilities.WorkoutDateUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//Set Workout Activity

public class SetWorkoutActivity extends AppCompatActivity implements SetWorkoutAdapter.AddRemoveWorkoutOnClickHandler,
        SetWorkoutAdapter.SaveWorkoutOnClickHandler {
    private static final String TAG = SetWorkoutActivity.class.getSimpleName();

    public static final String LIST_WORKOUTS = "list_workouts";
    public static final String LIST_BUNDLE = "list_bundle";
    private Bundle selectedWorkouts;
    private ArrayList<Workout> list_workouts = new ArrayList<Workout>();
    private long mWorkoutDate;
    private SetWorkoutAdapter mSetWorkoutAdapter;

    private RecyclerView mSetWorkoutRecyclerView;
    public static final String INTENT_ID = "intent_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_workout);

        Toolbar setWorkoutToolbar = (Toolbar) findViewById(R.id.toolbar_set_activity);
        setSupportActionBar(setWorkoutToolbar);

        ActionBar ab_set_workout = getSupportActionBar();
        ab_set_workout.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(SetWorkoutActivity.LIST_BUNDLE)) {
            selectedWorkouts = intent.getBundleExtra(SetWorkoutActivity.LIST_BUNDLE);
            mWorkoutDate = intent.getLongExtra(SelectWorkoutActivity.SELECTED_DATE, WorkoutDateUtilities.normalizeDate(System.currentTimeMillis()));
            Log.d(TAG, mWorkoutDate + " date in long format");
            list_workouts = selectedWorkouts.getParcelableArrayList(SetWorkoutActivity.LIST_WORKOUTS);
            Log.d(TAG, list_workouts.size() + " Number of workouts passed");
            sortWorkoutList(list_workouts);
        }
        mSetWorkoutRecyclerView = (RecyclerView) findViewById(R.id.rv_setWorkout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSetWorkoutRecyclerView.setLayoutManager(layoutManager);
        mSetWorkoutAdapter = new SetWorkoutAdapter(this, list_workouts, this, this);

        mSetWorkoutRecyclerView.setAdapter(mSetWorkoutAdapter);


    }

    private void sortWorkoutList(ArrayList<Workout> workoutArrayList) {
        Collections.sort(workoutArrayList, new Comparator<Workout>() {
            @Override
            public int compare(Workout o1, Workout o2) {
                int category_comparison = o1.getmWorkoutCategory().compareTo(o2.getmWorkoutCategory());
                if (category_comparison != 0) {
                    return category_comparison;
                } else {
                    return o1.getmWorkoutName().compareTo(o2.getmWorkoutName());
                }

            }

        });
    }

    @Override
    public void onAddRemoveWorkoutClick(int position, Workout workout, boolean isWorkoutAdd) {
        if (workout != null && isWorkoutAdd) {
            list_workouts.add(position + 1, workout);
            mSetWorkoutAdapter.notifyItemInserted(position + 1);
        } else if (!isWorkoutAdd) {
            list_workouts.remove(position);
            mSetWorkoutAdapter.notifyItemRemoved(position);
        }
        Log.d(TAG, list_workouts.size() + "Number of workouts after add remove operation");


    }

    @Override
    public void onSaveWorkoutClick(ArrayList<Workout> workouts) {
        Log.d(TAG, workouts.size() + " workouts passed for saving");
        ArrayList<ContentValues> workout_values = new ArrayList<ContentValues>();

        if (!workouts.isEmpty()) {
            for (Workout workout : workouts) {
                ContentValues values = new ContentValues();
                if (!workout.isWorkoutEmpty()) {
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_CATEGORY, workout.getmWorkoutCategory());
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME, workout.getmWorkoutName());
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_DIST_IN_METERS, workout.getDistance_in_meters());
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_WEIGHT_IN_KG, workout.getWeight_in_kg());
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_TIME_IN_MINS, workout.getTime_in_min());
                    values.put(WorkoutContract.WorkoutEntry.COLUMN_REP_COUNT, workout.getRep_count());
                    if (WorkoutDateUtilities.isDateNormalized(mWorkoutDate)) {
                        values.put(WorkoutContract.WorkoutEntry.COLUMN_DATE, mWorkoutDate);
                    } else {
                        values.put(WorkoutContract.WorkoutEntry.COLUMN_DATE, WorkoutDateUtilities.normalizeDate(mWorkoutDate));
                    }

                    workout_values.add(values);
                }

            }
        }
        ContentValues[] workouts_values_array = new ContentValues[workout_values.size()];
        workouts_values_array = workout_values.toArray(workouts_values_array);
        Log.d(TAG, "number of elements in content value array " + workouts_values_array.length);


        int rows = this.getContentResolver().bulkInsert(WorkoutContract.WorkoutEntry.CONTENT_URI, workouts_values_array);
        if (rows > 0) {
            Log.d(TAG, "Successfully inserted " + rows + " in database");
            Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.SELECTED_DATE, mWorkoutDate);
            intent.putExtra(INTENT_ID, "From_Activity_Set_Workout");
            startActivity(intent);
            //Pop SelectWorkoutActivity from stack once user is done saving workout
            SelectWorkoutActivity.getInstance().finish();
            //pop SetWorkout activity from stack once user is done adding workout
            finish();
        }
    }
}
