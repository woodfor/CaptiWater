package com.example.firebasetest1.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.firebasetest1.General.Converters;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "uid",
        onDelete = CASCADE ),indices = {@Index(value = {"uid"})})
public class House {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int hid;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "bday")
    public int bday;
    @TypeConverters(Converters.class)
    @ColumnInfo(name = "cpl")
    public String cpl;
    @ColumnInfo(name = "nop")
    public int nop;
    @ColumnInfo(name = "uid")
    public int uid;
    public House(String name, int bday, String cpl, int nop, int uid){
        this.name = name;
        this.bday = bday;
        this.cpl = cpl;
        this.nop = nop;
        this.uid = uid;
    }


    public String getCpl() {
        return cpl;
    }

    public int getNop() {
        return nop;
    }

    public int getUid() {
        return uid;
    }

    public int getBday() {
        return bday;
    }

    public int getId() {
        return hid;
    }

    public String getName() {
        return name;
    }
}
