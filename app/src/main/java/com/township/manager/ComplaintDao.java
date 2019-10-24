package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public abstract class ComplaintDao {

    @Query("SELECT * FROM Complaint WHERE resolved=0 ORDER BY timestamp DESC")
    public abstract List<Complaint> getPendingComplaints();

    @Query("SELECT * FROM Complaint WHERE resolved=1 ORDER BY timestamp DESC")
    public abstract List<Complaint> getResolvedComplaints();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Complaint... complaints);

    @Query("DELETE FROM Complaint")
    public abstract void deleteAll();
}
