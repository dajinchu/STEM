package com.gmail.dajinchu.stem;


import android.util.Log;

import java.util.HashMap;

/**
 * Created by Da-Jin on 2/5/2016.
 */
public class Bench {

    private static HashMap<String, Long> starts = new HashMap<String, Long>();
    private static long tmp;

    public static void start(String tag){
        starts.put(tag, System.currentTimeMillis());
    }
    public static long end(String tag){
        tmp = System.currentTimeMillis()-starts.get(tag);
        starts.remove(tag);
        Log.d("Bench",tag+": "+tmp+"\n");
        return tmp;
    }
}
