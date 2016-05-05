package com.gmail.dajinchu.stem.view.dialogs;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * Created by Da-Jin on 5/2/2016.
 */
public class MiniTimePicker extends TimePickerDialog {

    public static MiniTimePicker newInstance(OnTimeSetListener callback,
                                             int hourOfDay, int minute, boolean is24HourMode) {
        MiniTimePicker ret = new MiniTimePicker();
        ret.initialize(callback, hourOfDay, minute,0, is24HourMode);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            view.findViewById(com.wdullaer.materialdatetimepicker.R.id.time_display).setVisibility(View.GONE);
        }else {
            view.findViewById(com.wdullaer.materialdatetimepicker.R.id.time_display_background).setVisibility(View.GONE);
        }
        return view;
    }

}
