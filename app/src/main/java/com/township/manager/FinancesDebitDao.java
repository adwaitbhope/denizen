package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FinancesDebitDao {


    @Query("SELECT * FROM FinancesDebit ORDER BY debit_timestamp DESC")
    List<FinancesDebit> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FinancesDebit... financesDebits);

    @Delete
    void delete(FinancesDebit financesDebit);

    @Query("DELETE FROM FinancesDebit")
    void deleteAll();
}
