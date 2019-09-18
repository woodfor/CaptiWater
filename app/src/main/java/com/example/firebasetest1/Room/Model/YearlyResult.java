package com.example.firebasetest1.Room.Model;

import androidx.room.ColumnInfo;

public class YearlyResult {
    String name;
    long usage;
    int month;
    String area;

    public YearlyResult(String name, long usage, int month, String area) {
        this.name = name;
        this.usage = usage;
        this.month = month;
        this.area = area;
    }

    public YearlyResult(String name, long usage, int month){
        this.name = name;
        this.usage = usage;
        this.month = month;
    }

    public String getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

    public int getMonth() {
        return month;
    }

    public long getUsage() {
        return usage;
    }
}
