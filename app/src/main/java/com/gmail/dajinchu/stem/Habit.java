package com.gmail.dajinchu.stem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Habit {
    String name, frequency, completionTimes;
    long nextIncomplete;
    public Habit(String name, String frequency, String completionTimes, long nextIncomplete){
        this.name = name;
        this.frequency = frequency;
        this.completionTimes = completionTimes;
        this.nextIncomplete = nextIncomplete;
    }
    //Alternate convenience constructor for when loading from database
    public Habit(Cursor c){
        this(c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NAME)),
                c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_FREQUENCY)),
                c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_COMPLETION_TIMES)),
                c.getLong(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE)));
    }
    public void addCompletionNow(){
        Calendar c = Calendar.getInstance();
        if(completionTimes == null || completionTimes.isEmpty()){
            completionTimes = "";
        }
        completionTimes += c.getTimeInMillis()+" ";
        c.add(Calendar.DATE,1);
        nextIncomplete = c.getTimeInMillis();

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_COMPLETION_TIMES, completionTimes);
        values.put(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE, nextIncomplete);
        db.update(HabitContract.HabitEntry.TABLE_NAME, values, HabitContract.HabitEntry.COLUMN_NAME+"=?",new String[]{name});

        db.close();
    }
}
