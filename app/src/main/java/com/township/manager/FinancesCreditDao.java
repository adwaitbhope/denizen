package com.township.manager;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FinancesCreditDao {

    @Query("SELECT * FROM FinancesCredit ORDER BY credit_timestamp DESC")
    List<FinancesCredit> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FinancesCredit... financesCredits);

    @Delete
    void delete(FinancesCredit financesCredit);

    @Query("DELETE FROM FinancesCredit")
    void deleteAll();
}
