package com.gmail.dajinchu.stem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Da-Jin on 1/19/2016.
 */
public class MidnightFailureChecker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = Calendar.getInstance();
        int yesterdayOfWeek = now.get(Calendar.DAY_OF_WEEK)-Calendar.MONDAY-1;
        ArrayList<Habit> habits = Habit.getAllHabits();
        for(Habit habit : habits){
            if(!habit.days[yesterdayOfWeek])continue;
            now.add(Calendar.DATE,-1);
            if(habit.completions.size()==0)continue;
            if(habit.completions.get(habit.completions.size()-1).time.after(now))continue;
            Completion.createNewCompletion(now.getTimeInMillis(),habit.getId(),Completion.FAILED);
        }
    }
}
