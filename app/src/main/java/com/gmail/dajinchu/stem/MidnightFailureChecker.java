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
        Calendar now = Calendar.getInstance();
        Calendar check = Calendar.getInstance();
        check.set(Calendar.DATE,check.get(Calendar.DATE)-1);
        int yesterdayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        List<Habit> habits = Habit.listAll(Habit.class);
        for(Habit habit : habits){
            if(!habit.getDays()[Habit.calendarDayWeekToDisplay(yesterdayOfWeek)])continue;
            if(habit.getCompletions().size()>0&&
                    habit.getCompletions().get(habit.getCompletions().size()-1).getCompletionTime().after(check))continue;
            Log.d("MidnightFailureChecker","marking "+habit.getName());
            new Completion(check,Completion.FAILED,habit).save();
        }
    }
}
