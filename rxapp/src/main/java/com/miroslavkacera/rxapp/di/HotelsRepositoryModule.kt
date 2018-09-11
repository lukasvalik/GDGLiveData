package com.miroslavkacera.rxapp.di

import android.content.Context
import com.miroslavkacera.rxapp.db.HotelsDatabase
import com.miroslavkacera.rxapp.remote.HotelService
import com.miroslavkacera.rxapp.repository.HotelRepository
import com.miroslavkacera.rxapp.repository.HotelRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HotelsRepositoryModule {

    @Provides
    @Singleton
    fun provideHotelApi() = HotelService.API

    @Provides
    @Singleton
    fun provideHotelDatabase(context: Context) = HotelsDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideRepository(hotelsDatabase: HotelsDatabase, hotelService: HotelService): HotelRepository = HotelRepositoryImpl(hotelsDatabase.hotelDao(), hotelService)
}