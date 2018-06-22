package com.lukasvalik.gdglivedata.api

import android.arch.lifecycle.LiveData
import com.lukasvalik.gdglivedata.api.response.HotelListResponse
import com.lukasvalik.gdglivedata.api.response.UserPreferencesResponse
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.UserPreferences
import retrofit2.http.GET

interface HotelService {

    companion object {
        const val BASE_URL = "http://private-1b8cc-lukasvalik.apiary-mock.com/"
    }

    @GET("hotels")
    fun getHotels(): LiveData<ApiResponse<List<Hotel>>>

    @GET("userPreferences")
    fun getUserPreferences(): LiveData<ApiResponse<UserPreferences>>
}