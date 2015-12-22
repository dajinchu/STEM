package com.gmail.dajinchu.stem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Habit {
    String name="", frequency="", completionTimes="";
    final String id;
    long nextIncomplete=0;

    private boolean neverSaved = false;

    private Habit(){
        //Actually new Habit
        id = UUID.randomUUID().toString();
        neverSaved = true;
    }
    private Habit(String name, String frequency, String completionTimes, long nextIncomplete, String id){
        //New habit instance, modeling an already made habit in the database
        this.name = name;
        this.frequency = frequency;
        this.completionTimes = completionTimes;
        this.nextIncomplete = nextIncomplete;
        this.id = id;
    }

    public static Habit createNewHabit(){
        return new Habit();
    }
    public static Habit getHabitFromId(String id){
        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

        String[] projection = null;
        String selection = HabitContract.HabitEntry.COLUMN_HABIT_ID+"='"+id+"'";
        Cursor c = db.query(HabitContract.HabitEntry.TABLE_NAME,projection,selection,null,null,null,null);

        c.moveToFirst();
        Log.d("Habit","id = "+id);
        Habit habit = getHabitFromCursor(c);
        c.close();
        return habit;
    }
    public static Habit getHabitFromCursor(Cursor c){
        Habit habit = new Habit(c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NAME)),
                c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_FREQUENCY)),
                c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_COMPLETION_TIMES)),
                c.getLong(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE)),
                c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_ID)));
        return habit;
    }

    public void addCompletionNow(){
        Calendar c = Calendar.getInstance();
        if(completionTimes == null || completionTimes.isEmpty()){
            completionTimes = "";
        }
        completionTimes += c.getTimeInMillis()+" ";
        c.add(Calendar.DATE,1);
        nextIncomplete = c.getTimeInMillis();
        save();
    }

    public void save() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_HABIT_ID, id);
        values.put(HabitContract.HabitEntry.COLUMN_NAME, name);
        values.put(HabitContract.HabitEntry.COLUMN_FREQUENCY, frequency);
        values.put(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE, nextIncomplete);

        if(neverSaved) {
            db.insert(HabitContract.HabitEntry.TABLE_NAME, "null", values);
            neverSaved=false;
        }else{
            db.update(HabitContract.HabitEntry.TABLE_NAME, values, HabitContract.HabitEntry.COLUMN_HABIT_ID+"=?",new String[]{id});
        }

        db.close();
    }
}
