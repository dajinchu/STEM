package com.gmail.dajinchu.stem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gmail.dajinchu.stem.services.AlarmManagingService;

/**
 * Created by Da-Jin on 4/7/2016.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, AlarmManagingService.class));
    }
}
