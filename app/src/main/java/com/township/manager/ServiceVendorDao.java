package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

@Dao
public interface ServiceVendorDao {

    @Query("SELECT * FROM ServiceVendors")
    List<ServiceVendors> getAll();

    @Delete
    void delete(ServiceVendors serviceVendors);

    @Query("DELETE FROM ServiceVendors")
    void deleteAll();
}
