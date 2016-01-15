package com.gmail.dajinchu.stem;

import android.provider.BaseColumns;

/**
 * Created by Da-Jin on 11/28/2015.
 */
public class HabitContract {
    public HabitContract() {
    }

    public static abstract class HabitEntry implements BaseColumns {
        public static final String TABLE_NAME = "Habits";
        public static final String COLUMN_HABIT_ID = "HabitID";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_TIME_TO_DO = "TimeToDo";
        public static final String COLUMN_DAYS_OF_WEEK = "DaysOfWeek";

        public static final String COLUMN_NEXT_INCOMPLETE = "NextIncomplete";

        public static final String SQL_CREATE_HABIT =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_TIME_TO_DO + " REAL,"
                        + COLUMN_DAYS_OF_WEEK + " TEXT,"
                        + COLUMN_NEXT_INCOMPLETE + " REAL"
                        +" )";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        //Convenience methods

    }

    public static abstract class CompletionEntry implements BaseColumns{
        public static final String TABLE_NAME = "Completions";
        public static final String COLUMN_COMPLETION_TIME = "CompletionTime";
        public static final String COLUMN_HABIT_ID = "HabitID";
        public static final String COLUMN_SUCCESS = "Success";


        public static final String SQL_CREATE_COMPLETION =
                "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_COMPLETION_TIME + " REAL,"
                + COLUMN_HABIT_ID + " INTEGER,"
                + COLUMN_SUCCESS + " INTEGER"
                + " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
