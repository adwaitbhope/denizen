package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MaintenanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Maintenance... maintenances);

    @Delete
    void delete(Maintenance maintenance);

    @Query("DELETE FROM Maintenance")
    void deleteAll();

    @Query("SELECT * FROM Maintenance " +
            "ORDER BY timestamp DESC")
    List<Maintenance> getAll();


}
