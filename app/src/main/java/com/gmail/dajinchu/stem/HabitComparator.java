package com.gmail.dajinchu.stem;

import java.util.Comparator;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class HabitComparator implements Comparator<Habit> {

    private static TimeComparator tc = new TimeComparator();

    @Override
    public int compare(Habit a, Habit b) {
        return tc.compare(a.timeToDo,b.timeToDo);
    }
}
