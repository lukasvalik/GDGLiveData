package com.lukasvalik.gdglivedata

import android.app.Application
import android.arch.persistence.room.Room
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.api.HotelService
import com.lukasvalik.gdglivedata.db.AppDatabase
import com.lukasvalik.gdglivedata.db.HotelDao
import com.lukasvalik.gdglivedata.util.LiveDataCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



class App : Application() {

    companion object {
        lateinit var instance : App
            private set
    }

    private lateinit var database: AppDatabase
    private lateinit var hotelDao: HotelDao
    private lateinit var hotelService: HotelService

    lateinit var appExecutors: AppExecutors
        private set
    lateinit var hotelRepository: HotelRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appExecutors = AppExecutors()
        database = Room.databaseBuilder(instance, AppDatabase::class.java, "GDGLiveData.db").build()
        hotelDao = database.hotelDao()
        hotelService = Retrofit.Builder()
                .baseUrl(HotelService.BASE_URL)
                .client(okHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(HotelService::class.java)
        hotelRepository = HotelRepository(hotelDao, hotelService, appExecutors)
    }

    private fun okHttpClient() : OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }
}