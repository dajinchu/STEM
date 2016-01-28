package com.gmail.dajinchu.stem;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by Da-Jin on 12/28/2015.
 */
public class NotificationPublisher extends BroadcastReceiver {
    public static final String HABIT_ID = "habitId";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(HABIT_ID,0);

        Habit habit = Habit.findById(Habit.class, id);


        //Check if notification should really go off
        //Don't publish notification if it's already done.
        if(habit.isCompletedNow()){
            return;
        }
        //Don't publish if it's isn't today.
        int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if(!habit.getDays()[Habit.calendarDayWeekToDisplay(currentDayOfWeek)]){
            //not on today
            return;
        }

        //Intent for clicking on the notification
        Intent notificationResultIntent = new Intent(context,MainActivity.class);
        PendingIntent notificationResultPendingIntent = PendingIntent.getActivity(context,id,
                notificationResultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        //Intent for done action button in notification
        Intent notificationDoneActionIntent = new Intent(context, DoneActionReceiver.class);
        notificationDoneActionIntent.putExtra(HABIT_ID,id);
        PendingIntent notificationDoneActionPendingIntent = PendingIntent.getBroadcast(context,id,
                notificationDoneActionIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(habit.getName());
        builder.setContentText("Do it!");
        builder.setContentIntent(notificationResultPendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);//TODO make a real white and transparent icon
        builder.setAutoCancel(true);
        builder.setTicker(habit.getName());
        builder.addAction(R.drawable.ic_add_white_24dp,"Done",notificationDoneActionPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
    }
}
