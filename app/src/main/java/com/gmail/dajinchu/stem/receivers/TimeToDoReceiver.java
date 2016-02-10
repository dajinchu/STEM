package com.gmail.dajinchu.stem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.dajinchu.stem.model.Routine;
import com.gmail.dajinchu.stem.view.NotificationPublisher;

import java.util.Calendar;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class TimeToDoReceiver extends BroadcastReceiver {
    public static final String ACTION_TIME_TO_DO = "com.dajinchu.stem.TIME_TO_DO";
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NotificationPublisher.ROUTINE_ID, 0);
        Routine routine = Routine.findById(Routine.class,id);
        //Check if notification should really go off
        //Don't publish notification if it's already done.
        if(routine.isCompletedNow()){
            return;
        }
        //Don't publish if it's isn't today.
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(!routine.getDays()[Routine.calendarDayWeekToDisplay(currentDayOfWeek)]){
            //not on today
            return;
        }
        Intent orderedIntent = new Intent(ACTION_TIME_TO_DO);
        orderedIntent.putExtra(NotificationPublisher.ROUTINE_ID, id);
        orderedIntent.putExtra(NotificationPublisher.NOTIF_TYPE,NotificationPublisher.TIME_TO_DO);
        context.sendOrderedBroadcast(orderedIntent, null);
    }
}
