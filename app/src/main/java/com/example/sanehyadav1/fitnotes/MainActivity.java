package com.example.sanehyadav1.fitnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sanehyadav1.fitnotes.data.WorkoutContract;
import com.example.sanehyadav1.fitnotes.utilities.WorkoutDateUtilities;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        HorizontalCalendarFragment.OnDateClickListener,
        WorkoutAdapter.IsWorkoutCompleteClickHandler {
    private RecyclerView mRecyclerView;
    private WorkoutAdapter mWorkoutAdapter;
    private ProgressBar mLoadingIndicator;
    private TextView mEmptyTextView;
    private int mPosition = mRecyclerView.NO_POSITION;
    private FloatingActionButton mFab;
    private long mSelectedDate;
    private View mEmptyView;
    public static final String SELECTED_DATE_FROM_MAIN = "selected_date_from_main";

    //Firebase analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    //Loader for all the workouts
    private static final int ID_WORKOUT_LOADER = 33;

    public static final String SELECTED_DATE ="clicked_date";

    private static final String[] WORKOUTS_PROJECTION = {
            WorkoutContract.WorkoutEntry._ID,
            WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_CATEGORY,
            WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME,
            WorkoutContract.WorkoutEntry.COLUMN_DIST_IN_METERS,
            WorkoutContract.WorkoutEntry.COLUMN_WEIGHT_IN_KG,
            WorkoutContract.WorkoutEntry.COLUMN_TIME_IN_MINS,
            WorkoutContract.WorkoutEntry.COLUMN_REP_COUNT,
            WorkoutContract.WorkoutEntry.COLUMN_DATE,
            WorkoutContract.WorkoutEntry.COLUMN_IS_WORKOUT_COMPLETE
    };
    public static final int INDEX_WORKOUT_ID = 0;
    public static final int INDEX_WORKOUT_CATEGORY =1;
    public static final int INDEX_WORKOUT_NAME=2;
    public static final int INDEX_DIST_IN_METERS=3;
    public static final int INDEX_WEIGHT_IN_KG = 4;
    public static final int INDEX_TIME_IN_MINS = 5;
    public static final int INDEX_REP_COUNT = 6;
    public static final int INDEX_DATE =7;
    public static final int INDEX_IS_WORKOUT_COMPELETE = 8;

//    //projections for 'WORKOUT_CATEGORY' loader
//    private static final String[] WORKOUT_CATEGORY_PROJECTION = {
//            WorkoutContract.WorkoutCategoryEntry._ID,
//            WorkoutContract.WorkoutCategoryEntry.COLUMN_WORKOUT_CATEGORY
//    };
//    //Column constants for 'WORKOUT_CATEGORY' loader
//    public static final int INDEX_WORKOUT_CAT_ID = 0;
//    public static final int INDEX_WORKOUT_CATEGORY_NAME = 1;
//
//    //projections for 'WORKOUT_DETAILS' loader
//    private static final String[] WORKOUT_DETAIL_PROJECTION = {
//            WorkoutContract.WorkoutDetailsEntry._ID,
//            WorkoutContract.WorkoutDetailsEntry.COLUMN_WORKOUT_NAME,
//            WorkoutContract.WorkoutDetailsEntry.COLUMN_WORKOUT_IMAGE_NAME
//    };
//    //Column constants for 'WORKOUT_DETAILS' loader
//    public static final int INDEX_WORKOUT_DETAIL_ID =1;
//    public static final int INDEX_WORKOUT_NAME =2;
//    public static final int INDEX_WORKOUT_IMAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if(savedInstanceState!=null){
            mSelectedDate = savedInstanceState.getLong(SELECTED_DATE);
        }else {
            mSelectedDate = WorkoutDateUtilities.normalizeDate(System.currentTimeMillis());
        }

        //Check if we got here via save workout
        Intent intent = getIntent();
        if(intent!=null){
            String intent_source = intent.getStringExtra(SetWorkoutActivity.INTENT_ID);
            if(intent_source!=null && intent_source.equals("From_Activity_Set_Workout")){
                mSelectedDate = intent.getLongExtra(MainActivity.SELECTED_DATE,0);

                //Sending the value of 'selectedDate' to "Horizontal Calendar Fragment'
                Bundle bundle = new Bundle();
                bundle.putLong(SELECTED_DATE_FROM_MAIN,mSelectedDate);
                HorizontalCalendarFragment new_horizontalCalendarFragment = new HorizontalCalendarFragment();
                new_horizontalCalendarFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //replacing whatever is in the fragment container with this new fragment
                //adding transaction to back stack so that user can navigate back
                transaction.replace(R.id.horizontal_calendar_fragment,new_horizontalCalendarFragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        }

        mRecyclerView = (RecyclerView)findViewById(R.id.main_workouts_recycler_view);
        mLoadingIndicator = (ProgressBar)findViewById(R.id.pb_loading_indicator);
        mEmptyView = (View)findViewById(R.id.empty_view);
        mEmptyTextView = (TextView)findViewById(R.id.empty_textview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        mWorkoutAdapter = new WorkoutAdapter(this,this);

        mRecyclerView.setAdapter(mWorkoutAdapter);
        showLoading();

        //Initiating all loaders
        getSupportLoaderManager().initLoader(ID_WORKOUT_LOADER,null,this);
        //Set onClick method of FAB
        mFab = (FloatingActionButton)findViewById(R.id.fab_selectWorkout);
        mFab.setVisibility(View.VISIBLE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FirebaseAnalytics event when FAB is clicked
                long selectedDate;
                Bundle fb_bundle = new Bundle();
                fb_bundle.putString(FirebaseAnalytics.Param.ITEM_ID,String.valueOf(mFab.getId()));
                fb_bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,"Add workout FAB");
                fb_bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE,"Floating Action Button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,fb_bundle);

                //StartActivity intent
                Intent intent = new Intent(getApplicationContext(),SelectWorkoutActivity.class);
                if(mSelectedDate< WorkoutDateUtilities.normalizeDate(System.currentTimeMillis())){
                        selectedDate = WorkoutDateUtilities.normalizeDate(System.currentTimeMillis());
                }else {
                    selectedDate = mSelectedDate;
                }
                intent.putExtra(SelectWorkoutActivity.SELECTED_DATE,selectedDate);
                startActivity(intent);
            }
        });

    }
    //Internal method to change visibility when data is loading
    private void showLoading(){
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);


    }
    //Internal method to switch visibility of recycler view and loading indicator when 'Workoutdata' is available
    private void showWorkoutData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
    }
    private void emptyWorkoutData(){
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        if(mSelectedDate >= WorkoutDateUtilities.normalizeDate(System.currentTimeMillis())){
            mEmptyTextView.setText(this.getResources().getString(R.string.empty_view_today_future_text));
        }else{
            mEmptyTextView.setText(this.getResources().getString(R.string.empty_view_past_text));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(SELECTED_DATE,mSelectedDate);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        long selectedDate;
        showLoading();
        if(args!=null && !args.isEmpty()){
            selectedDate = args.getLong(SELECTED_DATE);
        }else{
            selectedDate = WorkoutDateUtilities.normalizeDate(System.currentTimeMillis());
        }
        switch (loaderId){
            case ID_WORKOUT_LOADER: {
                Uri workout_uri = WorkoutContract.WorkoutEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(selectedDate))
                        .build();
                //Sorting first by workout category and then by workout name
                String sortOrder = WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_CATEGORY + " ASC, "+ WorkoutContract.WorkoutEntry.COLUMN_WORKOUT_NAME + " ASC";

                return new CursorLoader(this, workout_uri, WORKOUTS_PROJECTION, null, null, sortOrder);

            }
            default:
                throw new RuntimeException("Loader not implemented"+loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case ID_WORKOUT_LOADER:{
                if(data!=null & data.getCount()!=0){
                    mWorkoutAdapter.swapCursor(data);
                    if(mPosition==mRecyclerView.NO_POSITION){
                        mPosition=0;
                    }
                    mRecyclerView.scrollToPosition(mPosition);
                    showWorkoutData();


                }else{
                    emptyWorkoutData();
                }

                Log.d("Main Activity",String.valueOf(data.getCount())+" record in workout cursor");
                break;
            }
            default:
                throw new RuntimeException("Unknown loader");
        }



    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWorkoutAdapter.swapCursor(null);
    }

    //Implementation of OnDateClickListener
    @Override
    public void onDateClick(Date date, int position) {
        mSelectedDate = WorkoutDateUtilities.normalizeDate(date.getTime());
        //intiate the loader
        Bundle data = new Bundle();
        data.putLong(SELECTED_DATE,mSelectedDate);
        getSupportLoaderManager().restartLoader(ID_WORKOUT_LOADER,data,this);



    }

    @Override
    public void onIsWorkoutCompleteClick(int workout_id,int workoutStatus,int position) {
        ContentValues values = new ContentValues();
        values.put(WorkoutContract.WorkoutEntry.COLUMN_IS_WORKOUT_COMPLETE,workoutStatus);
        String selection = WorkoutContract.WorkoutEntry._ID +" = ?";
        String[] selectionArgs = new String[]{String.valueOf(workout_id)};
        //How to update in bulk? In workoutAdapter we have a map of all WorkoutCompletionStatus, how to use it to bulk update.

        int rowsUpdated = this.getContentResolver().update(WorkoutContract.WorkoutEntry.CONTENT_URI,values,selection,selectionArgs);
        if(rowsUpdated > 0){

            //Toast.makeText(this," Successfully Updated to " + workoutStatus,Toast.LENGTH_SHORT).show();
        }
        mPosition = position;





    }
}
