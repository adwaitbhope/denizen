package com.township.manager;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface SecurityDesksDao {
    @Query("SELECT * FROM SecurityDesks")
    List<SecurityDesks> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SecurityDesks... securityDesks);

    @Delete
    void delete(SecurityDesks securityDesks);

    @Query("DELETE FROM SecurityDesks")
    void deleteAll();
}
