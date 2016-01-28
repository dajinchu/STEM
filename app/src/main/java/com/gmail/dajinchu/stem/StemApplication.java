package com.gmail.dajinchu.stem;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.orm.SugarApp;

/**
 * Created by Da-Jin on 1/27/2016.
 * This class merely combines SugarApp with MultiDex
 * Both needed to be the application in the manifest,
 * but there can only be one.
 * This extends Sugar, and implements MultiDex
 */
public class StemApplication extends SugarApp{
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
