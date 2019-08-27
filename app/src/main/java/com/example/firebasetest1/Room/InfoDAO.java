package com.example.firebasetest1.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

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
    @Query("SELECT * FROM House WHERE uid = :uid")
    List<House> getHouses(int uid);
    @Query("SELECT * FROM tap WHERE hid = :hid")
    List<Tap> getTaps(int hid);
    @Query("SELECT sum(usage) FROM records WHERE hid = :hid and date = :date")
    int getSumUsage(int hid,int date);
    @Query("DELETE FROM House WHERE id = :id")
    void deleteHouse(int id);
    @Query("DELETE FROM Tap WHERE id = :id")
    void deleteTap(int id);
    @Query("UPDATE Tap SET name = :name WHERE id = :id")
    void updateTap(int id,String name);



}
