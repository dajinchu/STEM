package com.gmail.dajinchu.stem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 1/19/2016.
 */
public class MidnightFailureChecker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FailureChecker","Triggered!!!");

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
        List<Habit> habits = Habit.listAll(Habit.class);
        for(Habit habit : habits){
            if(!habit.getDays()[Habit.calendarDayWeekToDisplay(check.get(Calendar.DAY_OF_WEEK))])continue;
            if(habit.isCompletedAtTime(check))continue;
            Log.d("MidnightFailureChecker","marking "+habit.getName());
            new Completion(check,Completion.FAILED,habit).save();
        }
    }
}
