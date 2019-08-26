package com.example.firebasetest1.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface InfoDAO {


    @Insert
    long insertUser(User user);
    @Insert
    long insertTap(Tap tap);
    @Insert
    long insertHouse(House house);
    @Query("SELECT id FROM User WHERE uuid = :uuid")
    int userExists(String uuid);


}
