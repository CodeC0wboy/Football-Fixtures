package com.app.matches.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MatchEntity::class], version = 1)
abstract class MatchDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
}