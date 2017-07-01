package com.example.sanehyadav1.fitnotes;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.sanehyadav1.fitnotes.data.VirtualDatabaseTable;
import com.example.sanehyadav1.fitnotes.utilities.WorkoutUtilities;
import com.example.sanehyadav1.fitnotes.videoplaylib.VideoPlayerActivity;

/**
 * Adapter for Inner horizontal Recycler View in "SelectWorkout"
 */

public class CategoryWorkoutListAdapter extends RecyclerView.Adapter<CategoryWorkoutListAdapter.CategoryWorkoutListViewHolder> {

    private Context mContext;
    private int mWorkoutCategoryPosition;
    private String[] workout_list;
    private String mWorkoutCategory;
    private String TAG = CategoryWorkoutListAdapter.class.getSimpleName();
    private VirtualDatabaseTable mVirtualDatabaseTable;
    private boolean[] isWorkoutSelected;
    /*
         * Below, we've defined an interface to handle clicks on items within this Adapter. In the
         * constructor of our ForecastAdapter, we receive an instance of a class that has implemented
         * said interface. We store that instance in this variable to call the onClick method whenever
         * an item is clicked in the list.
         */
    final private CategoryWorkoutListAdapterOnClickHandler mClickHandler;
    /*
    interface that receives onClick messages
     */

    public interface CategoryWorkoutListAdapterOnClickHandler {
        void onClick(String category, String workout_name, String isDistance, String isWeight, String isTime, String isReps, boolean add_workout);
    }


    CategoryWorkoutListAdapter(Context context, int workoutCategoryposition, CategoryWorkoutListAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mWorkoutCategoryPosition = workoutCategoryposition;
        mClickHandler = onClickHandler;

        workout_list = getWorkoutListOfThisCategory(mWorkoutCategoryPosition);
        //using a boolean array to check if particular workout is selected
        isWorkoutSelected = new boolean[workout_list.length];
        mWorkoutCategory = mContext.getResources().getStringArray(R.array.workout_categories)[mWorkoutCategoryPosition];
        mVirtualDatabaseTable = new VirtualDatabaseTable(mContext);

    }

    @Override
    public CategoryWorkoutListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.single_workout_list_item;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new CategoryWorkoutListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CategoryWorkoutListViewHolder holder, int position) {
        String workout_name = workout_list[position];
        boolean isSelected = isWorkoutSelected[position];
        if (workout_name != null) {
            holder.tv_ind_workout_name.setText(workout_name);
        } else {
            Log.d(TAG, "Some issue in fetching workout list");
        }
        if (isSelected) {
            holder.iv_selected_workout_image.setVisibility(View.VISIBLE);
        } else {
            holder.iv_selected_workout_image.setVisibility(View.GONE);
        }


        //Utitlity method to form "image name" from workoutcategory and workout name
        String complete_workout_name = WorkoutUtilities.getCompleteWorkoutName(mWorkoutCategory, workout_name);

        //Load image in ImageView using Glide, fetch image url from virtual table
        Cursor cursorForImageVideoUrl = mVirtualDatabaseTable.getUrlMatches(complete_workout_name, new String[]{VirtualDatabaseTable.COL_COMPLETE_WORKOUT_NAME, VirtualDatabaseTable.COL_IMAGE_URL1, VirtualDatabaseTable.COL_IMAGE_URL2, VirtualDatabaseTable.COL_VIDEO_URL});
        if (cursorForImageVideoUrl != null && cursorForImageVideoUrl.moveToFirst()) {
            String image_url1 = cursorForImageVideoUrl.getString(VirtualDatabaseTable.INDEX_IMAGE_URL1);
            if (image_url1 != null) {
                Glide.with(mContext)
                        .load(image_url1)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.img_loading_indicator.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(holder.iv_ind_workout_image);
                cursorForImageVideoUrl.close();
            }

        } else {
            Log.d(TAG, "Some issue in image Url cursor");
        }


    }

    @Override
    public int getItemCount() {
        if (workout_list != null) {
            return workout_list.length;
        } else {
            return 0;
        }

    }

    class CategoryWorkoutListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_ind_workout_name;
        private ImageView iv_ind_workout_image;
        private ProgressBar img_loading_indicator;
        private ImageView iv_selected_workout_image;
        private FrameLayout workout_options;

        private PopupWindow popupWindow;
        private Point point;
        String video_url;
        String image_url2;


        CategoryWorkoutListViewHolder(View view) {
            super(view);
            //Create a layout of ctegory workoutlist item
            tv_ind_workout_name = (TextView) view.findViewById(R.id.tv_individual_workout_name);
            iv_ind_workout_image = (ImageView) view.findViewById(R.id.individual_workout_image);
            img_loading_indicator = (ProgressBar) view.findViewById(R.id.image_loading_indicator);
            iv_selected_workout_image = (ImageView) view.findViewById(R.id.selected_workout_image);
            workout_options = (FrameLayout) view.findViewById(R.id.workout_options);

            view.setOnClickListener(this);

            workout_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] location = new int[2];
                    v.getLocationOnScreen(location);
                    point = new Point();
                    point.x = location[0];
                    point.y = location[1];
                    Log.d(TAG, "Menu is clicked at X " + point.x + " Y : " + point.y);
                    showWorkoutOptionPopup(mContext, point);


                }
            });
        }


        @Override
        public void onClick(View v) {
            int adapterposition = getAdapterPosition();
            String workout_name = workout_list[adapterposition];
            String complete_workout_name = WorkoutUtilities.getCompleteWorkoutName(mWorkoutCategory, workout_name);
            Cursor cursorForBools = mVirtualDatabaseTable.getUrlMatches(complete_workout_name, new String[]{
                    VirtualDatabaseTable.COL_IS_DISTANCE,
                    VirtualDatabaseTable.COL_IS_WEIGHT,
                    VirtualDatabaseTable.COL_IS_TIME,
                    VirtualDatabaseTable.COL_IS_REPS
            });
            if (cursorForBools != null && cursorForBools.moveToFirst()) {
                String isDistance = cursorForBools.getString(0);
                String isWeight = cursorForBools.getString(1);
                String isTime = cursorForBools.getString(2);
                String isReps = cursorForBools.getString(3);
                ImageView iv_workoutSelected = (ImageView) v.findViewById(R.id.selected_workout_image);
                //Toggling the workoutSelection, set to false if already selected and vice-versa
                if (isWorkoutSelected[adapterposition] == false) {
                    isWorkoutSelected[adapterposition] = true;
                    iv_workoutSelected.setVisibility(View.VISIBLE);
                    mClickHandler.onClick(mWorkoutCategory, workout_name, isDistance, isWeight, isTime, isReps, true);
                } else {
                    isWorkoutSelected[adapterposition] = false;
                    iv_workoutSelected.setVisibility(View.GONE);
                    mClickHandler.onClick(mWorkoutCategory, workout_name, isDistance, isWeight, isTime, isReps, false);
                }

            }
        }

        private void showWorkoutOptionPopup(final Context context, Point p) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupWindowLayout = inflater.inflate(R.layout.workout_options_popup_window_layout, null);

            popupWindow = new PopupWindow(context);
            popupWindow.setContentView(popupWindowLayout);
            popupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);

            int OFFSET_X = -20;
            int OFFSET_Y = 50;

            //Clear the default translucent background
            popupWindow.setBackgroundDrawable(new BitmapDrawable());

            //Displaying the popup at specified location +offset
            popupWindow.showAtLocation(popupWindowLayout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

            //Fetching the video url
            int adapterposition = getAdapterPosition();
            String workout_name = workout_list[adapterposition];
            String complete_workout_name = WorkoutUtilities.getCompleteWorkoutName(mWorkoutCategory, workout_name);
            Cursor cursorForVideoUrl = mVirtualDatabaseTable.getUrlMatches(complete_workout_name, new String[]{
                    VirtualDatabaseTable.COL_VIDEO_URL,
                    VirtualDatabaseTable.COL_IMAGE_URL2
            });
            if (cursorForVideoUrl != null && cursorForVideoUrl.moveToFirst()) {
                video_url = cursorForVideoUrl.getString(0);
                image_url2 = cursorForVideoUrl.getString(1);
            }
            //Setting imageview using glide and image_url2


            final ImageView iv_workoutpreview = (ImageView) popupWindowLayout.findViewById(R.id.workout_video_image_preview);
            iv_workoutpreview.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.image_is_loading_background));

            if (image_url2 != null) {
                Glide.with(mContext)
                        .load(image_url2)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }

                        })
                        .into(iv_workoutpreview);
            }
            //closing the cursor
            cursorForVideoUrl.close();


            //Setting onCLickListener of the popUpWindow
            popupWindowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (video_url.length() != 0) {
                        Intent intent = new Intent(context, VideoPlayerActivity.class);
                        intent.putExtra(VideoPlayerActivity.WORKOUT_VIDEO_URL, video_url);
                        context.startActivity(intent);
                    }
                }
            });


        }

    }

    private String[] getWorkoutListOfThisCategory(int workoutCategoryPosition) {
        String[] workouts;
        TypedArray ta = mContext.getResources().obtainTypedArray(R.array.all_workout_list);
        int id = ta.getResourceId(workoutCategoryPosition, 0);
        if (id > 0) {
            workouts = mContext.getResources().getStringArray(id);
            return workouts;
        } else {
            return null;
        }

    }

}
