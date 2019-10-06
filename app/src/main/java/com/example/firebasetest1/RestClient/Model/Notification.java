package com.example.firebasetest1.RestClient.Model;

import java.util.Date;

public class Notification {
    private long nid;
    private int duration;
    private Date dateTime;
    private String tapName;
    private String areaName;

    public Notification(int duration, Date dateTime, String tapName, String areaName) {
        this.duration = duration;
        this.dateTime = dateTime;
        this.tapName = tapName;
        this.areaName = areaName;
    }

    public Notification() {
    }

    public long getNid() {
        return nid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTapName() {
        return tapName;
    }

    public void setTapName(String tapName) {
        this.tapName = tapName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
