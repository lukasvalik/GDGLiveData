package com.lukasvalik.gdglivedata

import android.app.Application
import com.lukasvalik.gdglivedata.Repository.HotelRepository

class App : Application() {

    companion object {
        lateinit var instance : App
            private set
    }

    lateinit var hotelRepository: HotelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        hotelRepository = HotelRepository()
    }
}