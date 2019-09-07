package com.example.firebasetest1.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = UserDAO.class,
        parentColumns = "id",
        childColumns = "uid",
        onDelete = CASCADE ),indices = {@Index(value = {"uid"})})
public class HouseDAO {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int hid;
    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "nop")
    public int nop;
    @ColumnInfo(name = "uid")
    public int uid;
    public HouseDAO(String name, int nop, int uid){
        this.name = name;

        this.nop = nop;
        this.uid = uid;
    }




    public int getNop() {
        return nop;
    }

    public int getUid() {
        return uid;
    }


    public int getId() {
        return hid;
    }

    public String getName() {
        return name;
    }
}
