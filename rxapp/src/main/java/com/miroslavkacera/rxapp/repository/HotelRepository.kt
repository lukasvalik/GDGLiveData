package com.miroslavkacera.rxapp.repository

import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface HotelRepository {

    fun refreshData(): Completable
    fun getHotels(): Flowable<List<Hotel>>
    fun getUserPreferences(): Single<UserPreferences>

    //TODO fetch hotels and store them to db
    //TODO fetch userPreferencies - I do not store them in db currently (do not know why)
    //TODO getHotel(name) could help

    // ja pouzivam room ako databazu, neviem vsak, ci mas rozbehane riesenie na nu, tak kludne
    // pouzi co ti je najlahsie, alebo si to len lokalne ukladaj.
}