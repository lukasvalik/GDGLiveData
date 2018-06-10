package com.lukasvalik.gdglivedata

import android.app.Application
import android.arch.persistence.room.Room
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.model.AppDatabase
import com.lukasvalik.gdglivedata.model.HotelDao

class App : Application() {

    companion object {
        lateinit var instance : App
            private set
    }

    private lateinit var database: AppDatabase
    private lateinit var hotelDao: HotelDao

    lateinit var hotelRepository: HotelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(instance, AppDatabase::class.java, "GDGLiveData.db").build()
        hotelDao = database.hotelDao()
        hotelRepository = HotelRepository(hotelDao)
    }
}