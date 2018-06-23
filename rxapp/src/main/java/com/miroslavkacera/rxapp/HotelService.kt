package com.miroslavkacera.rxapp

import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import retrofit2.http.GET

interface HotelService {

    companion object {
        const val BASE_URL = "http://private-1b8cc-lukasvalik.apiary-mock.com/"
    }

    @GET("hotels")
    fun getHotels(): List<Hotel>

    @GET("userPreferences")
    fun getUserPreferences(): UserPreferences
}