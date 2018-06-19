package com.lukasvalik.gdglivedata.api

import android.arch.lifecycle.LiveData
import com.lukasvalik.gdglivedata.db.Hotel
import retrofit2.http.GET

interface HotelService {

    companion object {
        const val BASE_URL = "http://private-1b8cc-lukasvalik.apiary-mock.com/"
    }

    @GET("hotels")
    fun getHotels(): LiveData<ApiResponse<List<Hotel>>>
}