package com.example.firebasetest1.RestClient.Model;

import java.util.Date;
import java.sql.Time;

public class Report {
    long rid;
    int waterUsage;
    int waterDuration;
    String areaName;
    String tapName;
    Time time;
    Date date;

    public Report() {
    }

    public long getRid() {
        return rid;
    }

    public int getWaterUsage() {
        return waterUsage;
    }

    public int getWaterDuration() {
        return waterDuration;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getTapName() {
        return tapName;
    }

    public Time getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }
}
