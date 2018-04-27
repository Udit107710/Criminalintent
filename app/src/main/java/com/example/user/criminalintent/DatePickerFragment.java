package com.example.user.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by user on 1/6/2018.
 */

public class DatePickerFragment extends DialogFragment {

    private static final String ARGS_DATE = "date";
    private DatePicker mDatePicker;
    public static final String EXTRA_DATE = "com.example.user.criminalintent.date";

    public static DatePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE,date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){

        Date date = (Date) getArguments().getSerializable(ARGS_DATE);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dailog_date,null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year,month,day,null);


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();

                Time currentTime = new Time();
                currentTime.setToNow();

                int hour = currentTime.hour;
                int minute = currentTime.minute;

                Date date = new GregorianCalendar(year,month,day,hour,minute).getTime();
                sendResult(Activity.RESULT_OK,date);
            }
        }).create();
    }

    private void sendResult(int resultCode, Date date){
        if (getTargetFragment()==null) return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
