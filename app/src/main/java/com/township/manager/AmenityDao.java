package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AmenityDao {

    @Query("SELECT * FROM Amenity")
    List<Amenity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Amenity... amenities);

    @Delete
    void delete(Amenity amenity);

    @Query("DELETE FROM Amenity")
    void deleteAll();
}
