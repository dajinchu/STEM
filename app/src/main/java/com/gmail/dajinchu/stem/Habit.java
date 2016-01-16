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
    String name="";
    ArrayList<Completion> completions = new ArrayList<>();
    private DateFormat format = DateFormat.getDateTimeInstance();

    public int getId() {
        return id;
    }

    boolean[] days = new boolean[7];
    private int storableDays;

    private int id;
    long nextIncomplete=0;

    //TimeToDo is just for alerts.
    //Do NOT rely on this being on the right day, since it works with AlarmManager's setRepeating
    Calendar timeToDo = Calendar.getInstance();

    private boolean neverSaved = false;

    private Habit(){
        //Actually new Habit
        neverSaved = true;
        timeToDo.set(Calendar.SECOND, 0);
        days = new boolean[]{true,true,true,true,true,true,true};
    }
    private Habit(Cursor habitcurs){
        //New habit instance, modeling an already made habit in the database
        name = habitcurs.getString(habitcurs.getColumnIndex(HabitContract.HabitEntry.COLUMN_NAME));
        timeToDo.setTimeInMillis(habitcurs.getLong(habitcurs.getColumnIndex(HabitContract.HabitEntry.COLUMN_TIME_TO_DO)));
        storableDays = habitcurs.getInt(habitcurs.getColumnIndex(HabitContract.HabitEntry.COLUMN_DAYS_OF_WEEK));
        nextIncomplete = habitcurs.getLong(habitcurs.getColumnIndex(HabitContract.HabitEntry.COLUMN_NEXT_INCOMPLETE));
        id = habitcurs.getInt(habitcurs.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT_ID));

        //Get completions using completion model/contract class
        completions = Completion.loadCompletionsOfHabit(id);

        //Convert the int representing days it should happen on into boolean array form
        for(int i = 0; i < 7; i++){
            days[6-i] = (storableDays & (1<<i))!=0;
        }
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
        String sortOrder = HabitContract.HabitEntry.COLUMN_TIME_TO_DO + " ASC";

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

        Completion.createNewCompletion(c.getTimeInMillis(),this.id,Completion.SUCCESSFUL).save();

        updateTimeToDo();
        save();
        for(Completion completion:completions){
            Log.d("Habit",completion.time.toString());
        }

        //TODO unsure if this will be needed
        /*c.add(Calendar.DATE, 1);
        nextIncomplete = c.getTimeInMillis();
        save();*/
    }

    public boolean isCompletedNow(){
        if(completions.size()==0)return false;
        int dayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2)%7;
        int offset = 0;
        while(!days[(dayIndex+offset)%7]){
            offset--;
        }
        Calendar lastOccurence = Calendar.getInstance();
        lastOccurence.set(Calendar.MILLISECOND, 0);
        lastOccurence.set(Calendar.SECOND, 0);
        lastOccurence.set(Calendar.MINUTE, 0);
        lastOccurence.set(Calendar.HOUR_OF_DAY, 0);
        lastOccurence.add(Calendar.DATE, offset);

        Log.d("Habit","last completed "+lastOccurence.toString());
        return completions.get(completions.size()-1).time.after(lastOccurence);
    }

    public int daysToNextOccurence(){
        int todayIndex = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2)%7;
        int offset = 1;
        while(!days[(todayIndex+offset)%7]){
            offset++;
        }
        return offset;
    }

    public void setDayOfWeek(int dayNum, boolean isOnThisDay){
        days[dayNum]=isOnThisDay;
    }

    public void save() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

        for(int i = 0; i < 7; i++){
            storableDays = (storableDays << 1) + (days[i] ? 1 : 0);
        }

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_NAME, name);
        values.put(HabitContract.HabitEntry.COLUMN_TIME_TO_DO, timeToDo.getTimeInMillis());
        values.put(HabitContract.HabitEntry.COLUMN_DAYS_OF_WEEK, storableDays);

        if(neverSaved) {
            id = (int) db.insert(HabitContract.HabitEntry.TABLE_NAME, "null", values);
            neverSaved=false;
        }else{
            db.update(HabitContract.HabitEntry.TABLE_NAME, values,
                    HabitContract.HabitEntry.COLUMN_HABIT_ID+"=?",new String[]{String.valueOf(id)});
        }
        db.close();
    }

    public void updateTimeToDo(){
        Calendar now = Calendar.getInstance();
        timeToDo.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
        Log.d("Habit", format.format(timeToDo.getTime()));

        if(timeToDo.before(now)){
            //timeToDo already happened today, set to next
            timeToDo.add(Calendar.DATE, daysToNextOccurence());
            Log.d("Habit",format.format(timeToDo.getTime()));
        }
    }

    public void updateNotification(Context context){

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, TimeToDoReceiver.class);
        intent.putExtra(NotificationPublisher.HABIT_ID, id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        am.cancel(alarmIntent);

        updateTimeToDo();

        am.setRepeating(AlarmManager.RTC_WAKEUP,timeToDo.getTimeInMillis(),24*60*60*1000,alarmIntent);
    }
}
