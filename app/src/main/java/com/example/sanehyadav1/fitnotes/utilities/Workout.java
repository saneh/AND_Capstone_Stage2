package com.example.sanehyadav1.fitnotes.utilities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Workout class
 */

public class Workout implements Parcelable {
    private String mWorkoutCategory;
    private String mWorkoutName;

    private boolean isDistance;
    private boolean isWeight;
    private boolean isTime;
    private boolean isReps;

    private int distance_in_meters;
    private float weight_in_kg;
    private int time_in_min;
    private int rep_count;

    public Workout(String workoutCategory, String workoutName, boolean isDistance, boolean isWeight, boolean isTime, boolean isReps) {
        this.mWorkoutCategory = workoutCategory;
        this.mWorkoutName = workoutName;
        this.isDistance = isDistance;
        this.isWeight = isWeight;
        this.isTime = isTime;
        this.isReps = isReps;

    }

    //Setters
    public void setDistance_in_meters(int distance_in_meters) {
        this.distance_in_meters = distance_in_meters;
    }

    public void setWeight_in_kg(float weight_in_kg) {
        this.weight_in_kg = weight_in_kg;
    }

    public void setTime_in_min(int time_in_min) {
        this.time_in_min = time_in_min;
    }

    public void setRep_count(int rep_count) {
        this.rep_count = rep_count;
    }

    //Getters
    public String getmWorkoutCategory() {
        return mWorkoutCategory;
    }

    public String getmWorkoutName() {
        return mWorkoutName;
    }

    public boolean isDistance() {
        return isDistance;
    }

    public boolean isWeight() {
        return isWeight;
    }

    public boolean isTime() {
        return isTime;
    }

    public boolean isReps() {
        return isReps;
    }

    public int getDistance_in_meters() {
        return distance_in_meters;
    }

    public float getWeight_in_kg() {
        return weight_in_kg;
    }

    public int getTime_in_min() {
        return time_in_min;
    }

    public int getRep_count() {
        return rep_count;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Workout) {
            Workout workout = (Workout) obj;
            if (mWorkoutCategory == workout.getmWorkoutCategory() && mWorkoutName == workout.getmWorkoutName()) {
                return true;
            }
        }
        return false;

    }

    //method to check if this workout is empty
    public boolean isWorkoutEmpty(){
        if(this.distance_in_meters==0 && this.weight_in_kg ==0 && this.time_in_min ==0 && this.rep_count ==0){
            return true;
        }else
            return false;
    }

    //parcelling part


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mWorkoutCategory);
        dest.writeString(mWorkoutName);
        //To store boolean in parcel we are converting it into byte
        dest.writeByte((byte) (isDistance ? 1 : 0));
        dest.writeByte((byte) (isWeight ? 1 : 0));
        dest.writeByte((byte) (isTime ? 1 : 0));
        dest.writeByte((byte) (isReps ? 1 : 0));

    }

    //Retrieving workout data from parceable object
    private Workout(Parcel in) {
        this.mWorkoutCategory = in.readString();
        this.mWorkoutName = in.readString();
        //To convert byte to boolean
        this.isDistance = in.readByte() != 0;
        this.isWeight = in.readByte() != 0;
        this.isTime = in.readByte() != 0;
        this.isReps = in.readByte() != 0;
    }

    //CREATOR
    public static final Parcelable.Creator<Workout> CREATOR
            = new Parcelable.Creator<Workout>() {
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

}
