package com.gmail.dajinchu.stem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Da-Jin on 12/28/2015.
 */
public class NotificationPublisher extends BroadcastReceiver {
    public static final String HABIT_ID = "habitId";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationPublisher","onReceive!");

        int id = intent.getIntExtra(HABIT_ID,0);

        Habit habit = Habit.getHabitFromId(id);

        Intent notificationResultIntent = new Intent(context,MainActivity.class);
        PendingIntent notificationResultPendingIntent = PendingIntent.getActivity(context,id,
                notificationResultIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(habit.name);
        builder.setContentText("Do it!");
        builder.setContentIntent(notificationResultPendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);//TODO make a real white and transparent icon
        builder.setAutoCancel(true);
        builder.setTicker(habit.name);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
    }
}
