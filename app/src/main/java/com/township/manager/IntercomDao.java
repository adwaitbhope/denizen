package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface IntercomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Intercom... intercoms);

    @Delete
    void delete(Intercom intercom);

    @Query("DELETE FROM Intercom")
    void deleteAll();

    @Query("SELECT * FROM Intercom WHERE type=:type ORDER BY designation")
    List<Intercom> getAll(String type);

    @Query("SELECT * FROM Intercom WHERE type=:type AND wing_id=:wingid ORDER BY apartment")
    List<Intercom> getAll(String type, String wingid);
}
