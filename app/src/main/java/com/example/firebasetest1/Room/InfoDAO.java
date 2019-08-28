package com.example.firebasetest1.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.firebasetest1.Room.Model.DailyResult;
import com.example.firebasetest1.Room.Model.MonthlyResult;
import com.example.firebasetest1.Room.Model.YearlyResult;

import java.util.List;

@Dao
public interface InfoDAO {


    @Insert
    long insertUser(User user);
    @Insert
    long insertTap(Tap tap);
    @Insert
    long insertHouse(House house);
    @Insert
    long insertRecord(Records records);
    @Query("SELECT id FROM User WHERE uuid = :uuid")
    int userExists(String uuid);
    @Query("SELECT * FROM House WHERE uid = :uid")
    List<House> getHouses(int uid);
    @Query("SELECT * FROM tap WHERE hid = :hid")
    List<Tap> getTaps(int hid);
    @Query("SELECT sum(usage) FROM records WHERE hid = :hid and month = :month and year=:year and date = :date")
    int getDailySumUsage(int hid,int year,int month,int date);
    @Query("SELECT sum(usage) FROM records WHERE hid = :hid and month = :month and year=:year ")
    int getMonthlySumUsage(int hid,int year,int month);
    @Query("SELECT sum(usage) FROM records WHERE hid = :hid and year=:year")
    int getYearlySumUsage(int hid,int year);
    @Query("SELECT sum(usage) FROM records WHERE hid = :hid")
    int getTotalSumUsage(int hid);
    @Query("SELECT count(distinct year) FROM records WHERE hid = :hid")
    int getDistinctYear(int hid);
    @Query("DELETE FROM House WHERE id = :id")
    void deleteHouse(int id);
    @Query("DELETE FROM Tap WHERE id = :id")
    void deleteTap(int id);
    @Query("UPDATE Tap SET name = :name WHERE id = :id")
    void updateTap(int id,String name);
    @Query("SELECT Tap.name as name,sum(usage),hour FROM Records,Tap WHERE year =:year and month = :month and date = :date and Records.hid = :hid and tid = Tap.id group by Tap.name,hour")
    List<DailyResult> getRecordsByDay(int year, int month, int date, int hid);
    @Query("SELECT * FROM Records WHERE year =:year and month = :month and date = :date and Records.hid = :hid ")
    List<Records> getOnlyRecordsByDay(int year, int month, int date, int hid);
    @Query("SELECT Tap.name,sum(usage),date FROM Records,Tap WHERE year =:year and month = :month and Records.hid = :hid and tid = Tap.id group by Tap.name,date")
    List<MonthlyResult> getRecordsByMonth(int year, int month, int hid);
    @Query("SELECT Tap.name,sum(usage),month FROM Records,Tap WHERE year =:year and Records.hid = :hid and tid = Tap.id group by Tap.name,month")
    List<YearlyResult> getRecordsByYear(int year, int hid);


}
