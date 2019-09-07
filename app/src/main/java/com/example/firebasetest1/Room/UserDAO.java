package com.example.firebasetest1.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"id"}, unique = true)})
public class UserDAO {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int uid;
    @ColumnInfo(name = "uuid")
    public String uuid;

    public UserDAO(String uuid){
        this.uuid = uuid;
    }


    public int getId() {
        return uid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
