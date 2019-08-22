package com.example.firebasetest1.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DailyInfo.class}, version = 3, exportSchema = false)

public abstract class DailyInfoDatabase extends RoomDatabase {
    public abstract DailyInfoDAO InfoDao();
    private static volatile DailyInfoDatabase INSTANCE;
    static DailyInfoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DailyInfoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    DailyInfoDatabase.class, "dailyInfo_database")
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

}
