package com.gmail.dajinchu.stem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class TimeToDoReceiver extends BroadcastReceiver {
    public static final String ACTION_TIME_TO_DO = "com.dajinchu.stem.TIME_TO_DO";
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent orderedIntent = new Intent(ACTION_TIME_TO_DO);
        orderedIntent.putExtra(NotificationPublisher.HABIT_ID,intent.getIntExtra(NotificationPublisher.HABIT_ID,0));
        context.sendOrderedBroadcast(orderedIntent, null);
    }
}
