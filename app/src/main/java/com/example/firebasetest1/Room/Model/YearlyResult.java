package com.example.firebasetest1.Room.Model;

import androidx.room.ColumnInfo;

public class YearlyResult {
    String name;
    @ColumnInfo(name = "sum(usage)")
    int usage;
    int month;
    public YearlyResult(String name, int usage, int month){
        this.name = name;
        this.usage = usage;
        this.month = month;
    }

    public String getName() {
        return name;
    }

    public int getMonth() {
        return month;
    }

    public int getUsage() {
        return usage;
    }
}
