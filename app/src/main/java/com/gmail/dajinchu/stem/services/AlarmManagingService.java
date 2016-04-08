package com.gmail.dajinchu.stem.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.gmail.dajinchu.stem.models.Routine;
import com.gmail.dajinchu.stem.receivers.MidnightFailureChecker;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Da-Jin on 4/7/2016.
 */
public class AlarmManagingService extends Service {
    private AlarmManager am;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        setMidnightFailure();

        List<Routine> routines = Routine.listAll(Routine.class);
        for(Routine r : routines){
            r.updateNotification(this);
        }

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }


    private void setMidnightFailure(){
        Intent intent = new Intent(this, MidnightFailureChecker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        am.cancel(pendingIntent);

        Calendar triggerTime = Calendar.getInstance();
        triggerTime.set(Calendar.HOUR_OF_DAY,23);
        triggerTime.set(Calendar.MINUTE,59);
        triggerTime.set(Calendar.SECOND,50);

        am.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime.getTimeInMillis(),24*60*60*1000,pendingIntent);

    }
}
