package com.example.firebasetest1.Room.Model;

import androidx.room.ColumnInfo;

public class MonthlyResult {
    String name;
    @ColumnInfo(name = "sum(usage)")
    int usage;
    int date;
    public MonthlyResult(String name, int usage, int date){
        this.name = name;
        this.usage = usage;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public int getUsage() {
        return usage;
    }
}
