package com.app.matches.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches")
    suspend fun getAll(): List<MatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matches: List<MatchEntity>)

    @Query("DELETE FROM matches")
    suspend fun clear()
}
