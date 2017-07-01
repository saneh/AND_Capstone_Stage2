package com.example.sanehyadav1.fitnotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanehyadav1.fitnotes.utilities.Workout;

import java.util.ArrayList;

/**
 * Adapter for recyclerview in 'SetWorkoutActivity'
 */

public class SetWorkoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Workout> mWorkouts;
    private Context mContext;
    private static final int VIEW_TYPE_WORKOUT = 0;
    private static final int VIEW_TYPE_FOOTER = 1;
    private ArrayList<Boolean> mIsAddWorkoutList;

    private static final String TAG = SetWorkoutAdapter.class.getSimpleName();

    private AddRemoveWorkoutOnClickHandler mAddRemoveWorkoutClickHandler;
    private SaveWorkoutOnClickHandler mSaveWorkoutOnClickHandler;


    public interface AddRemoveWorkoutOnClickHandler {
        void onAddRemoveWorkoutClick(int position, Workout workout, boolean isWorkoutAdd);
    }

    public interface SaveWorkoutOnClickHandler {
        void onSaveWorkoutClick(ArrayList<Workout> workouts);
    }

    public SetWorkoutAdapter(Context context, ArrayList<Workout> workouts,
                             AddRemoveWorkoutOnClickHandler addRemoveWorkoutOnClickHandler,SaveWorkoutOnClickHandler saveWorkoutOnClickHandler) {
        mContext = context;
        mWorkouts = workouts;
        mAddRemoveWorkoutClickHandler = addRemoveWorkoutOnClickHandler;
        mSaveWorkoutOnClickHandler = saveWorkoutOnClickHandler;
        mIsAddWorkoutList = new ArrayList<Boolean>();
        for (int i = 0; i < mWorkouts.size(); i++) {
            mIsAddWorkoutList.add(true);
        }
        Log.d(TAG, mWorkouts.size() + " workout in setworkout arraylist");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_WORKOUT:
                viewHolder = new SetWorkoutViewHolder(inflater.inflate(R.layout.set_workout_list_item, parent, false));
                break;
            case VIEW_TYPE_FOOTER:
                viewHolder = new SaveButtonViewHolder(inflater.inflate(R.layout.set_workout_rv_footer, parent, false));
                break;
            default:
                new IllegalArgumentException("Unknown view type");

        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder == null) {
            return;
        }
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_WORKOUT: {
                SetWorkoutViewHolder holder = (SetWorkoutViewHolder) viewHolder;
                Workout workout = mWorkouts.get(position);
                Log.d(TAG, mWorkouts.size() + " workout in setworkout arraylist inside bind view holder");


                holder.tv_sw_workout_category.setText(workout.getmWorkoutCategory());
                holder.tv_sw_workout_name.setText(workout.getmWorkoutName());
                if (mIsAddWorkoutList.get(position)) {
                    holder.iv_sw_add_remove_workout.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add_workout));
                } else {
                    holder.iv_sw_add_remove_workout.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_remove_workout));
                }
                if (position > 0) {
                    Workout prev_workout = mWorkouts.get(position - 1);
                    if (workout.getmWorkoutCategory().equals(prev_workout.getmWorkoutCategory())) {
                        holder.tv_sw_workout_category.setVisibility(View.GONE);
                    } else holder.tv_sw_workout_category.setVisibility(View.VISIBLE);
                    if (workout.getmWorkoutName().equals(prev_workout.getmWorkoutName())) {
                        holder.tv_sw_workout_name.setVisibility(View.GONE);
                    } else holder.tv_sw_workout_name.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_sw_workout_category.setVisibility(View.VISIBLE);
                    holder.tv_sw_workout_name.setVisibility(View.VISIBLE);
                }

                //Setting Edittext values depending on values stored in workout list
                if (workout.isDistance()) {
                    holder.et_sw_distance.setVisibility(View.VISIBLE);
                    int distance = workout.getDistance_in_meters();
                    if (distance != 0) {
                        holder.et_sw_distance.setText(String.valueOf(distance));
                    }else{
                        holder.et_sw_distance.setText("");
                    }

                } else holder.et_sw_distance.setVisibility(View.GONE);
                if (workout.isWeight()) {
                    holder.et_sw_weight.setVisibility(View.VISIBLE);
                    float weight = workout.getWeight_in_kg();
                    if (weight != 0) {
                        holder.et_sw_weight.setText(String.valueOf(weight));
                    }else {
                        holder.et_sw_weight.setText("");
                    }
                } else holder.et_sw_weight.setVisibility(View.GONE);
                if (workout.isTime()) {
                    holder.et_sw_time.setVisibility(View.VISIBLE);
                    int time = workout.getTime_in_min();
                    if (time != 0) {
                        holder.et_sw_time.setText(String.valueOf(time));
                    }else{
                        holder.et_sw_time.setText("");
                    }
                } else holder.et_sw_time.setVisibility(View.GONE);
                if (workout.isReps()) {
                    holder.et_sw_reps.setVisibility(View.VISIBLE);
                    int reps = workout.getRep_count();
                    if (reps != 0) {
                        holder.et_sw_reps.setText(String.valueOf(reps));
                    }else{
                        holder.et_sw_reps.setText("");
                    }
                } else holder.et_sw_reps.setVisibility(View.GONE);
                break;
            }
            case VIEW_TYPE_FOOTER: {
                SaveButtonViewHolder saveholder = (SaveButtonViewHolder) viewHolder;
                break;
            }
            default:
                new IllegalArgumentException("Unknown View Type");
        }


    }

    @Override
    public int getItemCount() {
        return mWorkouts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mWorkouts.size() ? VIEW_TYPE_FOOTER : VIEW_TYPE_WORKOUT);
    }

    protected class SetWorkoutViewHolder extends RecyclerView.ViewHolder {

        final TextView tv_sw_workout_category;
        final TextView tv_sw_workout_name;
        final EditText et_sw_distance;
        final EditText et_sw_weight;
        final EditText et_sw_time;
        final EditText et_sw_reps;
        final ImageView iv_sw_add_remove_workout;

        SetWorkoutViewHolder(View view) {
            super(view);
            tv_sw_workout_category = (TextView) view.findViewById(R.id.tv_sw_workout_category);
            tv_sw_workout_name = (TextView) view.findViewById(R.id.tv_sw_workout_name);
            et_sw_distance = (EditText) view.findViewById(R.id.et_sw_distance);
            et_sw_weight = (EditText) view.findViewById(R.id.et_sw_weight);
            et_sw_time = (EditText) view.findViewById(R.id.et_sw_time);
            et_sw_reps = (EditText) view.findViewById(R.id.et_sw_reps);
            iv_sw_add_remove_workout = (ImageView) view.findViewById(R.id.iv_sw_add_remove_workout);

            iv_sw_add_remove_workout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView iv_add_remove_workout = (ImageView) v.findViewById(R.id.iv_sw_add_remove_workout);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        boolean isAddWorkout = mIsAddWorkoutList.get(position);
                        if (isAddWorkout) {
                            Workout workout_temp = mWorkouts.get(position);
                            //A new workout is created using workout at 'position'
                            Workout workout = new Workout(workout_temp.getmWorkoutCategory(),
                                                            workout_temp.getmWorkoutName(),
                                                            workout_temp.isDistance(),
                                                            workout_temp.isWeight(),
                                                            workout_temp.isTime(),
                                                            workout_temp.isReps());
                            mIsAddWorkoutList.add(position, false);
                            mAddRemoveWorkoutClickHandler.onAddRemoveWorkoutClick(position, workout, isAddWorkout);
                            iv_add_remove_workout.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_remove_workout));

                        } else {
                            mIsAddWorkoutList.remove(position);
                            mAddRemoveWorkoutClickHandler.onAddRemoveWorkoutClick(position, null, false);

                        }
                        notifyDataSetChanged();

                    }


                }
            });
            //EditText text change listeners
            et_sw_distance.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (isInteger(s.toString())) {
                        int position = getAdapterPosition();
                        Workout workout = mWorkouts.get(position);
                        workout.setDistance_in_meters(Integer.parseInt(s.toString()));
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            et_sw_weight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isFloat(s.toString())) {
                        int position = getAdapterPosition();
                        Workout workout = mWorkouts.get(position);
                        workout.setWeight_in_kg(Float.parseFloat(s.toString()));
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            et_sw_time.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isInteger(s.toString())) {
                        int position = getAdapterPosition();
                        Workout workout = mWorkouts.get(position);
                        workout.setTime_in_min(Integer.parseInt(s.toString()));
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            et_sw_reps.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (isInteger(s.toString())) {
                        int position = getAdapterPosition();
                        Workout workout = mWorkouts.get(position);
                        workout.setRep_count(Integer.parseInt(s.toString()));
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        //to check if the string is an integer
        private boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
            // only got here if we didn't return false
            return true;
        }

        private boolean isFloat(String s) {
            try {
                Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            } catch (Exception e){
                return false;
            }
            return true;
        }

    }

    protected class SaveButtonViewHolder extends RecyclerView.ViewHolder {
        final Button saveButton;

        SaveButtonViewHolder(View view) {
            super(view);
            saveButton = (Button) view.findViewById(R.id.save_workout_plan);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSaveWorkoutOnClickHandler.onSaveWorkoutClick(mWorkouts);

                }
            });
        }
    }
}
