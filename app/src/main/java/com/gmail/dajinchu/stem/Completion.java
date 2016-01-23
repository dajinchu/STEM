package com.gmail.dajinchu.stem;

import com.orm.SugarRecord;

import java.util.Calendar;

/**
 * Created by Da-Jin on 1/13/2016.
 */
public class Completion extends SugarRecord {

    private long completionTime;
    private int successCode;
    private Habit habit;

    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 0;
    public static final int SKIPPED = -1;

    public Completion(){
    }

    public Completion(Calendar time, int successCode, Habit habit){
        setCompletionTime(time);
        setSuccessCode(successCode);
        setHabit(habit);
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

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
}
