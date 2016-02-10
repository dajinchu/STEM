package com.gmail.dajinchu.stem.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.view.NotificationPublisher;

/**
 * Created by Da-Jin on 1/15/2016.
 */
public class DoneActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NotificationPublisher.ROUTINE_ID,0);
        Routine.findById(Routine.class,id).addCompletionNow();
        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(id);
    }
}
