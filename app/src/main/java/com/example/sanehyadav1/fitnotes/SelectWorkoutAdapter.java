package com.example.sanehyadav1.fitnotes;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *
 * Adapter for Vertical RecyclerView in SelectWorkoutActivity
 */

public class SelectWorkoutAdapter extends RecyclerView.Adapter<SelectWorkoutAdapter.SelectWorkoutViewHolder> {

    private static final int VIEW_TYPE = 0;
    private String[] workout_categories;
    private Context mContext;
    private String TAG = SelectWorkoutAdapter.class.getSimpleName();
    //CategoryWOrkoutListAdapter view click handler
    private final CategoryWorkoutListAdapter.CategoryWorkoutListAdapterOnClickHandler mClickHandler;

    public SelectWorkoutAdapter(Context context, CategoryWorkoutListAdapter.CategoryWorkoutListAdapterOnClickHandler onClickHandler){
        mContext = context;
        mClickHandler = onClickHandler;
        workout_categories = mContext.getResources().getStringArray(R.array.workout_categories);
        Log.d(TAG,workout_categories.length + " number of workout categories");

    }
    @Override
    public SelectWorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType){
            case VIEW_TYPE:
                layoutId = R.layout.select_workout_list_item;
                break;
            default:
                throw new IllegalArgumentException("Unknown View type");

        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);

        return new SelectWorkoutViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SelectWorkoutViewHolder holder, int position) {
        holder.tv_workoutCategory.setText(workout_categories[position]);
        //Need to set recycler view rv_workoutList
        holder.rv_workoutList.setAdapter(new CategoryWorkoutListAdapter(mContext,position,mClickHandler));

    }

    @Override
    public int getItemCount() {
        if(workout_categories!=null){
            return workout_categories.length;
        }else {
            return 0;
        }
    }

    public static int getViewType() {
        return VIEW_TYPE;
    }

    class SelectWorkoutViewHolder extends RecyclerView.ViewHolder{
        final TextView tv_workoutCategory;
        final RecyclerView rv_workoutList;
        SelectWorkoutViewHolder(View view){
            super(view);
            tv_workoutCategory = (TextView)view.findViewById(R.id.tv_SelectWorkout_category);
            rv_workoutList  = (RecyclerView)view.findViewById(R.id.rv_category_workout_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false);
            //Setting horizontal recycler view3
            rv_workoutList.setLayoutManager(linearLayoutManager);
            rv_workoutList.setHasFixedSize(true);

        }

    }
}
