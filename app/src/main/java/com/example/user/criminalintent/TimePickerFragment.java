package com.example.user.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by user on 1/6/2018.
 */

public class TimePickerFragment extends DialogFragment {

    private TimePicker mTimePicker;
    private static final String ARGS_TIME = "time";
    public static final String EXTRA_TIME = "time";

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARGS_TIME,date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){

        Date date = (Date) getArguments().getSerializable(ARGS_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int date1 = calendar.get(Calendar.DATE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time,null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.time_picker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = mTimePicker.getHour();
                int minute = mTimePicker.getMinute();

                sendResult(Activity.RESULT_OK,year,month,date1,hour,minute);
            }
        }).create();


    }

    private void sendResult(int resultCode,int year,int month,int date, int hour, int minute){
        if (getTargetFragment()==null) return;

        Intent intent = new Intent();
        Date date1 = new GregorianCalendar(year,month,date,hour,minute).getTime();
        intent.putExtra(EXTRA_TIME,date1);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

}