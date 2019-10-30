package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import retrofit2.http.DELETE;

@Dao
public interface SecurityPersonnelDao {

    @Query("SELECT * FROM SecurityPersonnel")
    List<SecurityPersonnel> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SecurityPersonnel... securityPersonnels);

    @Delete
    void delete(SecurityPersonnel securityPersonnel);

    @Query("DELETE FROM SecurityPersonnel")
    void deleteAll();

    @Query("DELETE FROM SecurityPersonnel WHERE personnel_id=:id")
    void deleteById(String id);
}
