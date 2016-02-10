package com.gmail.dajinchu.stem.models;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Da-Jin on 2/6/2016.
 */
public abstract class FilteringRoutineListener implements RoutineListener {

    private List<Routine> routineList;

    public abstract boolean shouldKeep(Routine routine);
    public abstract void onListChanged();

    public FilteringRoutineListener(List<Routine> routineList){
        this.routineList = routineList;
        routineList.clear();
        List<Routine> routines = Routine.listAll(Routine.class);
        for (Routine routine :routines) {
            if (shouldKeep(routine)) {
                routineList.add(routine);
            }
        }
        onListChanged();
    }

    @Override
    public void update(Routine record) {
        //TODO this won't support remove a record!!
        Routine routine = (Routine) record;
        Iterator<Routine> iterator = routineList.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getId().equals(routine.getId())){
                iterator.remove();
            }
        }
        if (shouldKeep(routine)) {
            routineList.add(routine);
        }
        onListChanged();
    }

    public List<Routine> getRoutineList(){
        return routineList;
    }
}
