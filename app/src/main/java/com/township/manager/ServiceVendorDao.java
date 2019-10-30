package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ServiceVendorDao {

    @Query("SELECT * FROM ServiceVendors")
    List<ServiceVendors> getAll();

    @Delete
    void delete(ServiceVendors serviceVendors);

    @Query("DELETE FROM ServiceVendors")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ServiceVendors... serviceVendors);

    @Query("DELETE FROM SERVICEVENDORS WHERE vendor_id=:id")
    void delete(String id);
}
