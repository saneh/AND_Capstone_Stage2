package com.example.sanehyadav1.fitnotes;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanehyadav1.fitnotes.utilities.WorkoutDateUtilities;

import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

/**
 * Horizontal Calendar Fragment, used in MainActivity
 */

public class HorizontalCalendarFragment extends Fragment {
    private HorizontalCalendar mHorizontalCalendar;

    public HorizontalCalendarFragment() {
    }

    private Long mSelectedDate;
    private static final String FRAG_SELECTED_DATE = "selected_date";


    OnDateClickListener mDateClickListener;

    //Interface to communicate with main activity , to pass the clicked date
    public interface OnDateClickListener {
        void onDateClick(Date date, int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.horizontal_calendar_fragment, container, false);

        //Setting the horizontal calendar
        /*end 1 month from now*/
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        /*start 1 month before from now*/
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        mHorizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .textSize(14f, 24f, 14f)
                .showDayName(true)
                .showMonthName(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .selectedDateBackground(Color.TRANSPARENT)
                .build();
        if (savedInstanceState != null) {
            mSelectedDate = savedInstanceState.getLong(FRAG_SELECTED_DATE, 0);
            mHorizontalCalendar.selectDate(new Date(mSelectedDate), true);
        } else {
            mHorizontalCalendar.goToday(true);
            mSelectedDate = WorkoutDateUtilities.normalizeDate(System.currentTimeMillis());
        }

        //Check for date from Main Activity

        Bundle arguments = getArguments();
        if (arguments != null) {
            Long selectedDate = arguments.getLong(MainActivity.SELECTED_DATE_FROM_MAIN);
            if (selectedDate != null) {
                mSelectedDate = selectedDate;
                mHorizontalCalendar.selectDate(new Date(mSelectedDate), true);
            }
        }


        mHorizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
                //Trigger the callback and pass the date and position that was clicked
                mSelectedDate = WorkoutDateUtilities.normalizeDate(date.getTime());
                mDateClickListener.onDateClick(date, position);
                //
            }

        });


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(FRAG_SELECTED_DATE, WorkoutDateUtilities.normalizeDate(mSelectedDate));
        super.onSaveInstanceState(outState);
    }

    //To ensure that MainActivity implement 'OnDateClickListener'
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mDateClickListener = (OnDateClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Main activity must implement OnDateClickListener");
        }
    }
}
