package com.example.sanehyadav1.fitnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.sanehyadav1.fitnotes.utilities.Workout;

import java.util.ArrayList;

//Select Workout from across workout categories

public class SelectWorkoutActivity extends AppCompatActivity implements CategoryWorkoutListAdapter.CategoryWorkoutListAdapterOnClickHandler {

    //Currently on clicking fab only one item is showing in recycler View. Check it

    private RecyclerView mSelectWorkoutRecyclerView;
    private FloatingActionButton mFabGoToSetWorkout;
    private Bundle selectedWorkouts;
    private ArrayList<Workout> mListWorkouts;
    private static final String TAG = SelectWorkoutActivity.class.getSimpleName();
    public static final String SELECTED_DATE = "selected_date";
    private long mSelectedDate;
    public static SelectWorkoutActivity ref_Select_Workout_activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_workout);
        ref_Select_Workout_activity = this;

        Toolbar selectWorkoutToolbar = (Toolbar) findViewById(R.id.toolbar_select_activity);
        setSupportActionBar(selectWorkoutToolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);



        final Intent intent = getIntent();
        if(intent!=null && intent.hasExtra(SelectWorkoutActivity.SELECTED_DATE)){
            mSelectedDate = intent.getLongExtra(SelectWorkoutActivity.SELECTED_DATE,0);
        }
        selectedWorkouts = new Bundle();
        mListWorkouts = new ArrayList<Workout>();


       //setting up recycler view
        mSelectWorkoutRecyclerView = (RecyclerView) findViewById(R.id.rv_select_workout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSelectWorkoutRecyclerView.setLayoutManager(linearLayoutManager);
        mSelectWorkoutRecyclerView.setAdapter(new SelectWorkoutAdapter(this, this));
        mSelectWorkoutRecyclerView.setHasFixedSize(true);

        //setting FAB
        mFabGoToSetWorkout = (FloatingActionButton)findViewById(R.id.fab_goToSetWorkout);
        mFabGoToSetWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedDate!=0){
                    if(!mListWorkouts.isEmpty()){
                        Intent intent_setWorkout = new Intent(getApplicationContext(),SetWorkoutActivity.class);
                        selectedWorkouts.putParcelableArrayList(SetWorkoutActivity.LIST_WORKOUTS,mListWorkouts);
                        intent_setWorkout.putExtra(SetWorkoutActivity.LIST_BUNDLE,selectedWorkouts);
                        intent_setWorkout.putExtra(SelectWorkoutActivity.SELECTED_DATE,mSelectedDate);
                        startActivity(intent_setWorkout);
                    }else{
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_selectWorkout),Toast.LENGTH_LONG).show();

                    }
                }else{
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_dateNotSelected),Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    @Override
    public void onClick(String workout_category, String workout_name,String isDistance, String isWeight, String isTime, String isReps,boolean add_workout) {
        if (workout_category != null && workout_name != null) {
            Workout workout = new Workout(workout_category,workout_name,isDistance.equals("1"),isWeight.equals("1"),isTime.equals("1"),isReps.equals("1"));
            if(add_workout){
                mListWorkouts.add(workout);
            }else {
                mListWorkouts.remove(workout);

            }
        }
        Log.d(TAG,mListWorkouts.size()+" workouts selected");
    }
    protected static SelectWorkoutActivity getInstance(){
        return ref_Select_Workout_activity;
    }
}
