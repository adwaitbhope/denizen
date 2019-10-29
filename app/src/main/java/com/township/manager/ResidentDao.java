package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ResidentDao {

    @Query("SELECT * FROM Resident")
    List<Resident> getAll();

    @Query("SELECT * FROM Resident WHERE wing_id = :wing_id ORDER BY apartment ASC")
    List<Resident> getAllFromWing(String wing_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Resident... residents);

    @Delete
    void delete(Resident resident);

    @Query("DELETE FROM Resident")
    void deleteAll();

}
