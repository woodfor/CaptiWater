package com.example.firebasetest1.Room.Model;

import androidx.room.ColumnInfo;

public class DailyResult {
    String name;
    @ColumnInfo(name = "sum(usage)")
    int usage;
    int hour;
    public DailyResult(String name, int usage, int hour){
        this.name = name;
        this.usage = usage;
        this.hour = hour;
    }

    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public int getSumUsage() {
        return usage;
    }
}
