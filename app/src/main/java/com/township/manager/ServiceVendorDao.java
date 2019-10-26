package com.township.manager;

import java.util.List;

import androidx.room.Delete;
import androidx.room.Query;

public interface ServiceVendorDao {

    @Query("SELECT * FROM ServiceVendors")
    List<Resident> getAll();

    @Delete
    void delete(ServiceVendors serviceVendors);

    @Query("DELETE FROM ServiceVendors")
    void deleteAll();
}
