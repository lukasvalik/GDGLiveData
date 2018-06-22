package com.lukasvalik.gdglivedata.Repository

import android.arch.lifecycle.LiveData
import com.lukasvalik.gdglivedata.AppExecutors
import com.lukasvalik.gdglivedata.api.ApiResponse
import com.lukasvalik.gdglivedata.api.ApiTask
import com.lukasvalik.gdglivedata.api.HotelService
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.HotelDao
import com.lukasvalik.gdglivedata.db.UserPreferences
import com.lukasvalik.gdglivedata.vo.Resource

class HotelRepository(private val hotelDao: HotelDao,
                      private val hotelService: HotelService,
                      private val appExecutors: AppExecutors) {

    fun getHotels(): LiveData<Resource<List<Hotel>>> {
        return object : NetworkBoundResource<List<Hotel>, List<Hotel>>(appExecutors) {
            override fun saveCallResult(item: List<Hotel>) {
                hotelDao.insertHotelList(item)
            }

            override fun shouldFetch(data: List<Hotel>?): Boolean = data == null || data.isEmpty()

            override fun loadFromDb(): LiveData<List<Hotel>> = hotelDao.loadHotels()

            override fun createCall(): LiveData<ApiResponse<List<Hotel>>> = hotelService.getHotels()

        }.asLiveData()
    }

    fun getUserPreferences(): LiveData<Resource<UserPreferences>> {
        return object : ApiTask<UserPreferences>() {

            override fun service(): LiveData<ApiResponse<UserPreferences>> = hotelService.getUserPreferences()

        }.execute(null)
    }

    fun updateHotel(hotel: Hotel) = appExecutors.diskIO().execute { hotelDao.insertHotel(hotel) }

    fun resetSeenHotels() = appExecutors.diskIO().execute { hotelDao.resetSeenHotels() }
}