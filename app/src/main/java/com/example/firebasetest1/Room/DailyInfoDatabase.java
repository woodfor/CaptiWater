package com.example.firebasetest1.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.firebasetest1.General.Converters;

@Database(entities = {House.class, Tap.class,User.class,Records.class}, version = 9, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DailyInfoDatabase extends RoomDatabase {
    public abstract InfoDAO InfoDao();
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
