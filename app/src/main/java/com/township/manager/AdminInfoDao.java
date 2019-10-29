package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AdminInfoDao {

    @Query("SELECT * FROM AdminInfo")
    List<AdminInfo> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AdminInfo... adminInfos);

    @Delete
    void delete(AdminInfo adminInfo);

    @Query("DELETE FROM AdminInfo")
    void deleteAll();
}
