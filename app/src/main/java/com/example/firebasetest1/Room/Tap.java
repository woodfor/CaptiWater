package com.example.firebasetest1.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = House.class,
        parentColumns = "id",
        childColumns = "hid",
        onDelete = CASCADE ),indices = {@Index(value = {"hid"}, unique = true)})
public class Tap {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int tid;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "hid")
    public int hid;
    @ColumnInfo(name = "usage")
    public int usage;
    @ColumnInfo(name = "duration")
    public int duration;
    @ColumnInfo(name = "time")
    public String time;
    @ColumnInfo(name = "hour")
    public int hour;
    @ColumnInfo(name = "date")
    public int date;
    @ColumnInfo(name = "month")
    public int month;
    @ColumnInfo(name = "year")
    public int year;
    public Tap(int usage, int duration, String wholeTime, String name) {
        this.usage=usage;
        this.duration = duration;
        this.name = name;
        Date today ;
        try {
            today = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmX").parse(wholeTime);
            this.time =   new SimpleDateFormat("mm:ss").format(today);
            this.date =  Integer.parseInt(new SimpleDateFormat("dd").format(today));
            this.month =  Integer.parseInt(new SimpleDateFormat("MM").format(today));
            this.year = Integer.parseInt(new SimpleDateFormat("yyyy").format(today));
            this.hour = Integer.parseInt(new SimpleDateFormat("HH").format(today));
        } catch (ParseException e) {
            today = Calendar.getInstance().getTime();
            this.time =   new SimpleDateFormat("mm:ss").format(today);
            this.date =  Integer.parseInt(new SimpleDateFormat("dd").format(today));
            this.month =  Integer.parseInt(new SimpleDateFormat("MM").format(today));
            this.year = Integer.parseInt(new SimpleDateFormat("yyyy").format(today));
            this.hour = Integer.parseInt(new SimpleDateFormat("HH").format(today));
            e.printStackTrace();
        }

    }
    public Tap() {

    }
    public int getId() {
        return tid;
    }

    public int getUsage() {
        return usage;
    }

    public String getTime() {
        return time;
    }
    public int getHour() {
        return hour;
    }
    public String getName(){return name;}
}
