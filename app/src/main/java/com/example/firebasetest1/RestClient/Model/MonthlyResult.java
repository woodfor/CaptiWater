package com.example.firebasetest1.RestClient.Model;

import androidx.room.ColumnInfo;

public class MonthlyResult {
    String name;
    long usage;
    int date;
    String area;
    public MonthlyResult(String name, long usage, int date){
        this.name = name;
        this.usage = usage;
        this.date = date;
    }

    public String getArea() {
        return area;
    }

    public MonthlyResult(String name, long usage, int date, String area) {
        this.name = name;
        this.usage = usage;
        this.date = date;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public long getUsage() {
        return usage;
    }
}
