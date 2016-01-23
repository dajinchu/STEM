package com.gmail.dajinchu.stem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 1/19/2016.
 */
public class MidnightFailureChecker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = Calendar.getInstance();
        int yesterdayOfWeek = now.get(Calendar.DAY_OF_WEEK)-Calendar.MONDAY-1;
        List<Habit> habits = Habit.listAll(Habit.class);
        for(Habit habit : habits){
            if(!habit.getDays()[yesterdayOfWeek])continue;
            now.add(Calendar.DATE,-1);
            if(habit.getCompletions().size()==0)continue;
            if(habit.getCompletions().get(habit.getCompletions().size()-1).getCompletionTime().after(now))continue;
            new Completion(now,Completion.FAILED,habit).save();
        }
    }
}
