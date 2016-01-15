package com.gmail.dajinchu.stem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Da-Jin on 1/13/2016.
 */
public class Completion {

    Calendar time = Calendar.getInstance();
    int habitID, successCode;
    public static final int SUCCESSFUL = 1;
    public static final int FAILED = 0;
    public static final int SKIPPED = -1;

    private Completion(long time, int id, int successCode){
        this.time.setTimeInMillis(time);
        this.habitID = id;
        this.successCode = successCode;
    }

    private Completion(Cursor cc){
        this(cc.getLong(cc.getColumnIndex(HabitContract.CompletionEntry.COLUMN_COMPLETION_TIME)),
                cc.getInt(cc.getColumnIndex(HabitContract.CompletionEntry.COLUMN_HABIT_ID)),
                cc.getInt(cc.getColumnIndex(HabitContract.CompletionEntry.COLUMN_SUCCESS)));
    }

    public static Completion createNewCompletion(long time, int id, int successCode){
        return new Completion(time,id,successCode);
    }
    public static ArrayList<Completion> loadCompletionsOfHabit(int id){
        ArrayList<Completion> completions = new ArrayList<>();
        SQLiteDatabase db = MainActivity.dbHelper.getReadableDatabase();
        String select = HabitContract.HabitEntry.COLUMN_HABIT_ID+"="+id;
        String order = HabitContract.CompletionEntry.COLUMN_COMPLETION_TIME+" ASC";
        Cursor compcurs = db.query(HabitContract.CompletionEntry.TABLE_NAME, null, select,null,null,null,order);

        for (compcurs.moveToFirst(); !compcurs.isAfterLast(); compcurs.moveToNext()) {
            completions.add(new Completion(compcurs));
        }
        compcurs.close();

        return completions;
    }

    public void save(){
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HabitContract.CompletionEntry.COLUMN_COMPLETION_TIME,time.getTimeInMillis());
        values.put(HabitContract.CompletionEntry.COLUMN_HABIT_ID,habitID);
        values.put(HabitContract.CompletionEntry.COLUMN_SUCCESS,successCode);
        db.insert(HabitContract.CompletionEntry.TABLE_NAME, "null", values);
    }
}
