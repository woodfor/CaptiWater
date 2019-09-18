package com.example.firebasetest1.Room.Model;

import androidx.room.ColumnInfo;

public class DailyResult {
    String name;
    long usage;
    int hour;
    String area;
    public DailyResult(String name, int usage, int hour){
        this.name = name;
        this.usage = usage;
        this.hour = hour;
    }

    public DailyResult(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }


    public String getName() {
        return name;
    }

    public int getHour() {
        return hour;
    }

    public long getSumUsage() {
        return usage;
    }
}
