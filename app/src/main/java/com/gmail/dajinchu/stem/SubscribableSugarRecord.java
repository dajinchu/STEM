package com.gmail.dajinchu.stem;

import android.os.Handler;
import android.os.Looper;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Da-Jin on 2/1/2016.
 */
public class SubscribableSugarRecord extends SugarRecord {
    private static List<Subscriber> subs = new ArrayList<Subscriber>();
    //TODO this is terrible way to do it, just move all this code to Routine, because static doesn't extend ya dumb butt

    public void notifyAllSubscribers() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (Subscriber sub : subs) {//TODO use a central model class that caches
                    sub.update(SubscribableSugarRecord.this);
                }
            }
        });

    }

    public static void unsubscribe(Subscriber subscriber) {
        subs.remove(subscriber);
    }

    public static void subscribe(Subscriber subscriber) {
        subs.add(subscriber);
    }
}