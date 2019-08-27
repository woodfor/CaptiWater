package com.example.firebasetest1.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = House.class,
        parentColumns = "id",
        childColumns = "hid",
        onDelete = CASCADE ),indices = {@Index(value = {"hid"})})
public class Tap {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int tid;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "hid")
    public int hid;

    public Tap(String name,int hid) {
        this.name = name;
        this.hid = hid;
    }
    public int getId() {
        return tid;
    }


    public String getName(){return name;}
}
