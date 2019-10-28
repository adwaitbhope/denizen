package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AmenityBookingDao {

    @Query("SELECT * FROM AmenityBooking")
    List<AmenityBooking> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AmenityBooking... bookings);

    @Delete
    void delete(AmenityBooking amenityBooking);

    @Query("DELETE FROM AmenityBooking")
    void deleteAll();

}
