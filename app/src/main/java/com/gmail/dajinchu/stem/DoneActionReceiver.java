package com.gmail.dajinchu.stem;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Da-Jin on 1/15/2016.
 */
public class DoneActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NotificationPublisher.HABIT_ID,0);
        Log.d("done action receiver",id+"received");
        Habit.getHabitFromId(id).addCompletionNow();
        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(id);
    }
}
