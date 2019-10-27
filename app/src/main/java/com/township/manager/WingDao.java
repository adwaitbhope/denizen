package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface WingDao {

    @Query("SELECT * FROM wing")
    List<Wing> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Wing... wings);

    @Delete
    void delete(Wing wing);

    @Query("DELETE FROM Wing")
    void deleteAll();

    @Query("Select name FROM Wing WHERE wing_id=:id")
    String getWingName(String id);

}
