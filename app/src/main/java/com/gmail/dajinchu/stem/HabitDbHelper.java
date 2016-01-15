package com.gmail.dajinchu.stem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Da-Jin on 11/28/2015.
 */
public class HabitDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Habit.db";

    public HabitDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("HabitDbHelper","CODE:"+ HabitContract.HabitEntry.SQL_CREATE_HABIT);
        db.execSQL(HabitContract.HabitEntry.SQL_CREATE_HABIT);
        db.execSQL(HabitContract.CompletionEntry.SQL_CREATE_COMPLETION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO figure this out
        db.execSQL(HabitContract.HabitEntry.SQL_DELETE_ENTRIES);
        db.execSQL(HabitContract.CompletionEntry.SQL_DELETE_ENTRIES);
    }
}
