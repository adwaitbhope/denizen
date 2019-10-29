package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AmenityBookingDao {

    @Query("SELECT * FROM AmenityBooking ORDER BY booking_from DESC")
    List<AmenityBooking> getAll();

    @Query("SELECT * FROM AmenityBooking WHERE payment = 1 ORDER BY booking_from DESC")
    List<AmenityBooking> getWithPaymentsOnly();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AmenityBooking... bookings);

    @Delete
    void delete(AmenityBooking amenityBooking);

    @Query("DELETE FROM AmenityBooking")
    void deleteAll();

}
