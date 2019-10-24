package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface VisitorDao {

    @Query("SELECT * FROM Visitor")
    List<Visitor> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Visitor... visitors);

    @Delete
    void delete(Visitor visitor);

    @Query("DELETE FROM Visitor")
    void deleteAll();
}
