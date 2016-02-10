package com.gmail.dajinchu.stem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.view.NotificationPublisher;

import java.util.Calendar;

/**
 * Created by Da-Jin on 2/9/2016.
 */
public class BackupAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*TODO this could be merged with TimeTODo but stay separate,
        mainly because to prevent backup and timetodo pendingIntents from filterEqual to be true*/

        Log.d("Backup","received");
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
        Intent i = new Intent(context,NotificationPublisher.class);
        i.putExtra(NotificationPublisher.ROUTINE_ID, id);
        i.putExtra(NotificationPublisher.NOTIF_TYPE, NotificationPublisher.BACKUP_ALARM);
        context.sendBroadcast(i);
    }
}
