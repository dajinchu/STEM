package com.gmail.dajinchu.stem.model;

import java.util.Comparator;

/**
 * Created by Da-Jin on 1/16/2016.
 */
public class RoutineComparator implements Comparator<Routine> {

    private static TimeComparator tc = new TimeComparator();

    @Override
    public int compare(Routine a, Routine b) {
        return tc.compare(a.getTimeToDo(),b.getTimeToDo());
    }
}
