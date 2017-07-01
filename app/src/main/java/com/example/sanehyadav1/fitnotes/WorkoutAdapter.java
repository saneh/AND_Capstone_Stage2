package com.example.sanehyadav1.fitnotes;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Adapter for recyclerview in 'MainActivity'
 */

public class WorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = WorkoutAdapter.class.getSimpleName();

    private final int WORKOUT_ITEM = 0;

    //Declaring a private final Context field
    private final Context mContext;
    //Declaring a private Cursor field
    private Cursor mCursor;

    private HashMap<Integer,Integer> workoutCompletionStatusMap = new HashMap<Integer,Integer>();

    private IsWorkoutCompleteClickHandler mIsWorkoutCompleteClickHandler;

    public interface IsWorkoutCompleteClickHandler{
        public void onIsWorkoutCompleteClick(int workout_id,int workout_completion_status,int position);
    }
    public WorkoutAdapter(Context context,IsWorkoutCompleteClickHandler isWorkoutCompleteClickHandler) {
        this.mContext = context;
        this.mIsWorkoutCompleteClickHandler = isWorkoutCompleteClickHandler;

    }

    /*
   Select approproate viewHolder based on viewType
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case WORKOUT_ITEM:
                View view_workout_item = inflater.inflate(R.layout.main_activity_workout_list_item, parent, false);
                viewHolder = new WorkoutItemViewHolder(view_workout_item);
                break;
            default:
                throw new IllegalArgumentException("Undefined View Type");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case WORKOUT_ITEM:
                WorkoutItemViewHolder workoutItemViewHolder = (WorkoutItemViewHolder) holder;
                configureWorkoutItemViewHolder(workoutItemViewHolder, position);
                break;
            default:
                throw new IllegalArgumentException("Undefined View Type in OnBindView");
        }
    }

    //Setting workoutItemViewHolder Textview
    private void configureWorkoutItemViewHolder(WorkoutItemViewHolder workoutItemViewHolder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
            String workout_category = mCursor.getString(MainActivity.INDEX_WORKOUT_CATEGORY);
            String workout_name = mCursor.getString(MainActivity.INDEX_WORKOUT_NAME);

            workoutItemViewHolder.mWorkoutCategory.setText(workout_category);
            workoutItemViewHolder.mWorkoutName.setText(workout_name);

            if (!mCursor.isNull(MainActivity.INDEX_DIST_IN_METERS)) {
                int dist_in_meters = mCursor.getInt(MainActivity.INDEX_DIST_IN_METERS);
                if(dist_in_meters!=0){
                    workoutItemViewHolder.mDistanceInMeter.setVisibility(View.VISIBLE);
                    workoutItemViewHolder.mDistanceInMeter.setText(String.valueOf(dist_in_meters)+ " METERS");
                }else{
                    workoutItemViewHolder.mDistanceInMeter.setVisibility(View.GONE);
                }
            }
            if (!mCursor.isNull(MainActivity.INDEX_WEIGHT_IN_KG)) {
                float weight_in_kg = mCursor.getFloat(MainActivity.INDEX_WEIGHT_IN_KG);
                if(weight_in_kg!=0.0){
                    workoutItemViewHolder.mWeightInKg.setVisibility(View.VISIBLE);
                    workoutItemViewHolder.mWeightInKg.setText(String.valueOf(weight_in_kg) + " KG");
                }else{
                    workoutItemViewHolder.mWeightInKg.setVisibility(View.GONE);
                }
            }
            if (!mCursor.isNull(MainActivity.INDEX_TIME_IN_MINS)) {
                int time_in_mins = mCursor.getInt(MainActivity.INDEX_TIME_IN_MINS);
                if(time_in_mins!=0){
                    workoutItemViewHolder.mTimeInMin.setVisibility(View.VISIBLE);
                    workoutItemViewHolder.mTimeInMin.setText(String.valueOf(time_in_mins) + " MINS");
                }else {
                    workoutItemViewHolder.mTimeInMin.setVisibility(View.GONE);
                }
            }
            if (!mCursor.isNull(MainActivity.INDEX_REP_COUNT)) {
                int rep_count = mCursor.getInt(MainActivity.INDEX_REP_COUNT);
                if(rep_count!=0){
                    workoutItemViewHolder.mRepCount.setVisibility(View.VISIBLE);
                    workoutItemViewHolder.mRepCount.setText(String.valueOf(rep_count) + " REPS");
                }else {
                    workoutItemViewHolder.mRepCount.setVisibility(View.GONE);
                }
            }
            if(!mCursor.isNull(MainActivity.INDEX_IS_WORKOUT_COMPELETE)){
                int is_workout_complete = workoutCompletionStatusMap.get(mCursor.getInt(MainActivity.INDEX_WORKOUT_ID));
                if(is_workout_complete==1){
                    Log.d(TAG,is_workout_complete + " is workout complete");
                    workoutItemViewHolder.mIsWorkoutComplete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_workout_complete));
                } else if(is_workout_complete==0){
                    workoutItemViewHolder.mIsWorkoutComplete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_workout_not_complete));
                    Log.d(TAG,is_workout_complete + " is workout complete");
                }
            }
            int date = mCursor.getInt(MainActivity.INDEX_DATE);

            //Setting Workout category and Workout name visibility based on previos workout category and workout name
            if(mCursor.moveToPrevious()){
                String prev_workout_category = mCursor.getString(MainActivity.INDEX_WORKOUT_CATEGORY);
                String prev_workout_name = mCursor.getString(MainActivity.INDEX_WORKOUT_NAME);

                if(prev_workout_category.equals(workout_category)){
                    workoutItemViewHolder.mWorkoutCategory.setVisibility(View.GONE);
                }else {
                    workoutItemViewHolder.mWorkoutCategory.setVisibility(View.VISIBLE);
                }
                if(prev_workout_name.equals(workout_name)){
                    workoutItemViewHolder.mWorkoutName.setVisibility(View.GONE);
                }
                else{
                    workoutItemViewHolder.mWorkoutName.setVisibility(View.VISIBLE);
                }
            }else{
                workoutItemViewHolder.mWorkoutName.setVisibility(View.VISIBLE);
                workoutItemViewHolder.mWorkoutCategory.setVisibility(View.VISIBLE);
            }



        }


    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return WORKOUT_ITEM;
    }

    //Swap cursor
    public void swapCursor(Cursor newCursor) {
        if(newCursor!=null){
            mCursor = newCursor;
            notifyDataSetChanged();
            //Creating a map of all workout_id and there completion status, this is used to set isWorkoutComplete ImageView correctly
            workoutCompletionStatusMap.clear();
            for(int i=0;i<newCursor.getCount();i++){
                newCursor.moveToPosition(i);
                workoutCompletionStatusMap.put(newCursor.getInt(MainActivity.INDEX_WORKOUT_ID),newCursor.getInt(MainActivity.INDEX_IS_WORKOUT_COMPELETE));
            }
        }

    }


    //Workout Item view holder
    class WorkoutItemViewHolder extends RecyclerView.ViewHolder {
        final TextView mWorkoutCategory;
        final TextView mWorkoutName;
        final TextView mDistanceInMeter;
        final TextView mWeightInKg;
        final TextView mTimeInMin;
        final TextView mRepCount;
        final ImageView mIsWorkoutComplete;

        public WorkoutItemViewHolder(View view) {
            super(view);
            mWorkoutCategory = (TextView)view.findViewById(R.id.tv_main_workout_category);
            mWorkoutName = (TextView) view.findViewById(R.id.tv_main_workout_name);
            mDistanceInMeter = (TextView) view.findViewById(R.id.tv_main_distance);
            mWeightInKg = (TextView) view.findViewById(R.id.tv_main_weight);
            mTimeInMin = (TextView) view.findViewById(R.id.tv_main_time);
            mRepCount = (TextView) view.findViewById(R.id.tv_main_reps);
            mIsWorkoutComplete = (ImageView)view.findViewById(R.id.iv_main_is_workout_complete);

            mIsWorkoutComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv_isWorkoutComplete = (ImageView)v.findViewById(R.id.iv_main_is_workout_complete);
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && mCursor!=null && mCursor.moveToPosition(position)){
                        int workout_id = mCursor.getInt(MainActivity.INDEX_WORKOUT_ID);
                        Log.d(TAG,workout_id + " Workout ID");
                        int workoutCompletionStatus = workoutCompletionStatusMap.get(workout_id);
                        if(workoutCompletionStatus==0){
                            workoutCompletionStatusMap.put(workout_id,1);
                            iv_isWorkoutComplete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_workout_complete));

                        }else{
                            workoutCompletionStatusMap.put(workout_id,0);
                            iv_isWorkoutComplete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_workout_not_complete));
                        }
                        mIsWorkoutCompleteClickHandler.onIsWorkoutCompleteClick(workout_id,workoutCompletionStatusMap.get(workout_id),position);
                    }
                }
            });

        }
    }
}
