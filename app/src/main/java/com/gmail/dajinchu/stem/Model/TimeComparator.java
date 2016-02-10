package com.gmail.dajinchu.stem.model;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class TimeComparator implements Comparator<Calendar> {

    Calendar aclone = Calendar.getInstance(), bclone = Calendar.getInstance();

    @Override
    public int compare(Calendar a, Calendar b) {
        aclone.setTime(a.getTime());
        bclone.setTime(b.getTime());

        aclone.set(0,0,0);
        bclone.set(0,0,0);

        return aclone.compareTo(bclone);
    }
}
