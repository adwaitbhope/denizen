package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MembershipPaymentDao {
    @Query("SELECT * FROM MembershipPayment")
    List<MembershipPayment> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MembershipPayment... payments);

    @Delete
    void delete(MembershipPayment payment);

    @Query("DELETE FROM MembershipPayment")
    void deleteAll();
}
