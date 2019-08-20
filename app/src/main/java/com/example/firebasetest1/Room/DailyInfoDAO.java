package com.example.firebasetest1.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DailyInfoDAO {
    @Query("SELECT * FROM DailyInfo")
    List<DailyInfo> getAll();

    @Query("SELECT * FROM DailyInfo WHERE uuid = :uuid")
    List<DailyInfo> findByUID(String uuid);
    @Insert
    void insertAll(DailyInfo... infos);
    @Insert
    long insert(DailyInfo info);
    @Delete
    void delete(DailyInfo info);
    @Update(onConflict = REPLACE)
    public void updateInfos(DailyInfo... infos);
    @Query("DELETE FROM DailyInfo")
    void deleteAll();
    @Query("SELECT * FROM DailyInfo WHERE id = :id LIMIT 1")
    DailyInfo findByID(int id);
    @Query("DELETE FROM DailyInfo WHERE id = :id")
    void deleteOne(int id);
    @Query("SELECT sum(duration) FROM DailyInfo WHERE uuid = :uuid and month = :month and year = :year and date = :date")
    int DailyTotalDuration(String uuid, String date,int month,int year);
    @Query("SELECT sum(usage) FROM DailyInfo WHERE uuid = :uuid and month = :month and year = :year and date = :date")
    int DailyTotalUsage(String uuid, String date,int month,int year);
    @Query("SELECT * from DailyInfo WHERE uuid = :uuid and month = :month and year = :year and date = :date")
    List<DailyInfo> monthyTotalUsage(String uuid, int date,int month, int year);


}
