package com.miroslavkacera.rxapp.repository

import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface HotelRepository {

    fun refreshData(): Completable
    fun getHotels(): Flowable<List<Hotel>>
    fun getHotel(name: String): Single<Hotel>
    fun getUserPreferences(): Single<UserPreferences>
    fun markHotelSeen(hotel: Hotel)
    fun resetHotels()
}