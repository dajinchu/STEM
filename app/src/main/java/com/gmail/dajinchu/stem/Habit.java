package com.gmail.dajinchu.stem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Da-Jin on 12/7/2015.
 */
public class Habit {
    String name="", daysOfTheWeek="", completionTimes="";

    public int getId() {
        return id;
    }

    private int id;
    long nextIncomplete=0, timeToDo=24*60*60*1000;

    private boolean neverSaved = false;

    private Habit(){
        //Actually new Habit
        neverSaved = true;
    }
    private Habit(Cursor c){
        //New habit instance, modeling an already made habit in the database
        name = c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NAME));
        timeToDo = c.getLong(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_TIME_TO_DO));
        daysOfTheWeek = c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_DAYS_OF_WEEK));
        completionTimes = c.getString(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_COMPLETION_TIMES));
        nextIncomplete = c.getLong(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE));
        id = c.getInt(c.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_ID));
    }

    public static Habit createNewHabit(){
        return new Habit();
    }
    public static Habit getHabitFromId(int id){
        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

        String selection = HabitContract.HabitEntry.COLUMN_HABIT_ID+"="+id;
        Cursor c = db.query(HabitContract.HabitEntry.TABLE_NAME, null,selection,null,null,null,null);

        c.moveToFirst();
        Log.d("Habit","id = "+id);
        Habit habit = getHabitFromCursor(c);
        c.close();
        return habit;
    }
    public static Habit getHabitFromCursor(Cursor c){
        return new Habit(c);
    }
    public static ArrayList<Habit> getAllHabits(){
        ArrayList<Habit> habitList = new ArrayList<>();
        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();

        /*Calendar now = Calendar.getInstance();
        //Define selection which filters which rows. SQL WHERE clause
        String selection = HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE+"<"+now.getTimeInMillis();
        */
        // How you want the results sorted in the resulting Cursor
        String sortOrder = HabitContract.HabitEntry.COLUMN_NAME + " DESC";

        Cursor c = db.query(HabitContract.HabitEntry.TABLE_NAME,
                null,null, null, null, null, sortOrder);
        c.moveToFirst();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            habitList.add(getHabitFromCursor(c));
        }
        c.close();
        return habitList;
    }

    public void addCompletionNow(){
        Calendar c = Calendar.getInstance();
        if(completionTimes == null || completionTimes.isEmpty()){
            completionTimes = "";
            //TODO I don't think this'll ever happen
        }
        completionTimes += c.getTimeInMillis()+" ";
        c.add(Calendar.DATE, 1);
        nextIncomplete = c.getTimeInMillis();
        save();
    }

    public void save() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_NAME, name);
        values.put(HabitContract.HabitEntry.COLUMN_TIME_TO_DO, timeToDo);
        values.put(HabitContract.HabitEntry.COLUMN_DAYS_OF_WEEK, daysOfTheWeek);
        values.put(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE, nextIncomplete);

        if(neverSaved) {
            id = (int) db.insert(HabitContract.HabitEntry.TABLE_NAME, "null", values);
            neverSaved=false;
        }else{
            db.update(HabitContract.HabitEntry.TABLE_NAME, values,
                    HabitContract.HabitEntry.COLUMN_HABIT_ID+"=?",new String[]{String.valueOf(id)});
        }
        db.close();
    }

    public void updateNotification(Context context){

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.HABIT_ID,id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        am.cancel(alarmIntent);


        DateFormat format = DateFormat.getDateTimeInstance();

        Calendar c = Calendar.getInstance();
        int todayTime = (int) (c.getTimeInMillis()%(60*60*24*1000));
        Log.d("Habit",format.format(c.getTime()));
        c.add(Calendar.MILLISECOND,-todayTime);//subtract today's time from today, to get today at 0:00 AM
        Log.d("Habit",format.format(c.getTime()));
        c.add(Calendar.MILLISECOND, (int) timeToDo);

        Log.d("Habit",format.format(c.getTime()));

        am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),24*60*60*1000,alarmIntent);
    }
}
