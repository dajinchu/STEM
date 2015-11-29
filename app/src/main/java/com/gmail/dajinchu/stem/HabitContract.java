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
        public static final String COLUMN_FREQUENCY = "Frequency";

        public static final String SQL_CREATE_HABIT =
                "CREATE TABLE " + TABLE_NAME + " ("
                        + COLUMN_HABIT_ID + " STRING PRIMARY KEY,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_FREQUENCY + " TEXT"
                        +" )";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS" + TABLE_NAME;

    }
}