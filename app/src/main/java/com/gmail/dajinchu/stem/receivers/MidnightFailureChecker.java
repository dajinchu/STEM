package com.gmail.dajinchu.stem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.dajinchu.stem.models.Completion;
import com.gmail.dajinchu.stem.models.Routine;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 1/19/2016.
 */
public class MidnightFailureChecker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get Calendar for time of the beginning of the day that just finished.
        //Things are checked relative to this, so this day of week is used,
        // and success that day is checked
        Calendar check = Calendar.getInstance();
        if(check.get(Calendar.AM_PM)==Calendar.AM){
            //If it's morning, check yesterday, because today hasn't finished
            //TODO change this to something like 12am-1am time range. it's like this for testing now
            check.add(Calendar.DATE,-1);
        }
        check.set(Calendar.HOUR_OF_DAY,23);
        check.set(Calendar.MINUTE,59);
        check.set(Calendar.SECOND,59);
        List<Routine> routines = Routine.listAll(Routine.class);
        for(Routine routine : routines){
            if(!routine.getDays()[Routine.calendarDayWeekToDisplay(check.get(Calendar.DAY_OF_WEEK))])continue;
            if(routine.isCompletedAtTime(check))continue;
            new Completion(check,Completion.FAILED,routine).save();
        }
    }
}
