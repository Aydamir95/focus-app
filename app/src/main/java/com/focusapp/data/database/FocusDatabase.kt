package com.focusapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.focusapp.data.dao.AppUsageDao
import com.focusapp.data.dao.DailyStatsDao
import com.focusapp.data.dao.FocusSessionDao
import com.focusapp.data.model.AppUsage
import com.focusapp.data.model.DailyStats
import com.focusapp.data.model.FocusSession

@Database(
    entities = [FocusSession::class, AppUsage::class, DailyStats::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun dailyStatsDao(): DailyStatsDao

    companion object {
        @Volatile
        private var INSTANCE: FocusDatabase? = null

        fun getDatabase(context: Context): FocusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusDatabase::class.java,
                    "focus_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



