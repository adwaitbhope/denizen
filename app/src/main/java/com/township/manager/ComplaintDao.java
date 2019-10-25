package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ComplaintDao {

    @Query("SELECT * FROM Complaint WHERE resolved=0 ORDER BY timestamp DESC")
    List<Complaint> getPendingComplaints();

    @Query("SELECT * FROM Complaint WHERE resolved=1 ORDER BY timestamp DESC")
    List<Complaint> getResolvedComplaints();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Complaint... complaints);

    @Query("DELETE FROM Complaint")
    void deleteAll();
}
