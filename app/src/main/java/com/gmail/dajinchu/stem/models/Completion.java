package com.gmail.dajinchu.stem.models;

import java.util.Calendar;

/**
 * Created by Da-Jin on 1/13/2016.
 */
public class Completion extends ChildRecord {

    private long completionTime;
    private int successCode;
    private Routine routine;

    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 0;
    public static final int SKIPPED = -1;

    public Completion(){
    }

    public Completion(Calendar time, int successCode, Routine routine){
        setCompletionTime(time);
        setSuccessCode(successCode);
        setRoutine(routine);
    }

    public Calendar getCompletionTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(completionTime);
        return c;
    }

    public void setCompletionTime(Calendar completionTime) {
        this.completionTime = completionTime.getTimeInMillis();
    }

    public int getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(int successCode) {
        this.successCode = successCode;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    @Override
    protected ParentRecord getParent() {
        return routine;
    }
}
