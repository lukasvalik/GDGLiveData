package com.miroslavkacera.rxapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.miroslavkacera.rxapp.model.Hotel

private lateinit var instance: HotelsDatabase

@Database(entities = [(Hotel::class)], version = 1, exportSchema = false)
abstract class HotelsDatabase : RoomDatabase() {
    companion object {
        fun getInstance(context: Context): HotelsDatabase {
            if (!::instance.isInitialized) {
                instance = Room.databaseBuilder(context.applicationContext, HotelsDatabase::class.java, "GDGLiveData.db").build()
            }
            return instance
        }
    }


    abstract fun hotelDao(): HotelDao
}