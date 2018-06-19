package com.lukasvalik.gdglivedata.Repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.lukasvalik.gdglivedata.AppExecutors
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.HotelDao

class HotelRepository(private val hotelDao: HotelDao,
                      private val appExecutors: AppExecutors) {

    private val hotels : LiveData<List<Hotel>> = hotelDao.loadHotels()

    fun getHotels() : LiveData<List<Hotel>> = hotels

    fun getHotel(name: String) : LiveData<Hotel> {
        val data = MutableLiveData<Hotel>()
        data.value = hotels.value?.first { it.name.equals(name) }
        return data
    }

    fun updateHotel(hotel: Hotel) = appExecutors.diskIO().execute {hotelDao.insertHotel(hotel) }

    fun resetSeenHotels() = appExecutors.diskIO().execute { hotelDao.resetSeenHotels() }

    fun setDefaultHotelList() = appExecutors.diskIO().execute { hotelDao.insertHotelList(DEFAULT_HOTEL_LIST) }

    companion object {
        val DEFAULT_HOTEL_LIST = arrayListOf(
                Hotel("Hilton", "http://www3.hilton.com/resources/media/hi/WATHNHN/en_US/img/shared/full_page_image_gallery/main/HL_hotelexteriorview01_2_675x359_FitToBoxSmallDimension_Center.jpg", false),
                Hotel("Tapolca Fogado", "https://edge.media.datahc.com/HI154550957.jpg", true),
                Hotel("Sanja Vodice", "https://edge.media.datahc.com/HI500053572.jpg", true),
                Hotel("Rixos Libertas", "https://edge.media.datahc.com/HI409050723.jpg", false),
                Hotel("Lapad", "https://edge.media.datahc.com/HI401664845.jpg", false),
                Hotel("Valamar Lacroma", "https://edge.media.datahc.com/HI380766419.jpg", false))
    }
}