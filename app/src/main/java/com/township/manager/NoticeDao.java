package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface NoticeDao {

    @Query("SELECT * FROM notice")
    List<Notice> getAll();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(Notice... notices);

    @Delete
    void delete(Notice notice);

}
