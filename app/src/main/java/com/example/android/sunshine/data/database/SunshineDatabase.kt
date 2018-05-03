package com.example.android.sunshine.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context


@Database(entities = [WeatherEntry::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class SunshineDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao


    // For Singleton instantiation
    companion object {
        private var INSTANCE: SunshineDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): SunshineDatabase? {
            if (INSTANCE == null) {
                synchronized(LOCK::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            SunshineDatabase::class.java, "weather")
                            .build()
                }
            }
            return INSTANCE
        }
    }
}