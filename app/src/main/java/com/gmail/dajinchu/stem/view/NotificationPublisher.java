package com.gmail.dajinchu.stem.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.dajinchu.stem.receivers.DoneActionReceiver;
import com.gmail.dajinchu.stem.R;
import com.gmail.dajinchu.stem.models.Routine;

/**
 * Created by Da-Jin on 12/28/2015.
 */
public class NotificationPublisher extends BroadcastReceiver {
    public static final String ROUTINE_ID = "routineId";
    public static final String NOTIF_TYPE = "notifType";
    public static final int TIME_TO_DO = 1, BACKUP_ALARM = 2;
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(ROUTINE_ID,0);
        int type = intent.getIntExtra(NOTIF_TYPE,1);
        Resources res = context.getResources();

        Routine routine = Routine.findById(Routine.class, id);

        //Intent for clicking on the notification
        Intent notificationResultIntent = new Intent(context,MainActivity.class);
        PendingIntent notificationResultPendingIntent = PendingIntent.getActivity(context,id,
                notificationResultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        //Notification Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(res.getString(R.string.app_name));
        builder.setContentIntent(notificationResultPendingIntent);
        builder.setSmallIcon(R.mipmap.notif_xxxhdpi);
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_HIGH);

        if(type == TIME_TO_DO) {
            builder.setContentText(res.getString(R.string.gentle_remind)+" "+
                    routine.getName()+" "+
                    routine.getRelativity()+" "+
                    routine.getCue());
        }
        if(type == BACKUP_ALARM){
            //Intent for done action button in notification
            Intent notificationDoneActionIntent = new Intent(context, DoneActionReceiver.class);
            notificationDoneActionIntent.putExtra(ROUTINE_ID, id);
            PendingIntent notificationDoneActionPendingIntent = PendingIntent.getBroadcast(context, id,
                    notificationDoneActionIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentText(res.getString(R.string.backup_notif)+" "+routine.getName());
            builder.addAction(R.drawable.ic_done_white_24dp, "I did", notificationDoneActionPendingIntent);

        }
        Log.d("NotifPublish",type+"");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id,builder.build());
    }
}
