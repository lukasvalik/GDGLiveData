package com.lukasvalik.gdglivedata.Repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.lukasvalik.gdglivedata.model.Hotel

class HotelRepository {

    private val hotels = MutableLiveData<List<Hotel>>()

    init {
        val hotelList = arrayListOf(
                Hotel("Hilton", "http://www3.hilton.com/resources/media/hi/WATHNHN/en_US/img/shared/full_page_image_gallery/main/HL_hotelexteriorview01_2_675x359_FitToBoxSmallDimension_Center.jpg", false),
                Hotel("Tapolca Fogado", "https://edge.media.datahc.com/HI154550957.jpg", true),
                Hotel("Sanja Vodice", "https://edge.media.datahc.com/HI500053572.jpg", true),
                Hotel("Rixos Libertas", "https://edge.media.datahc.com/HI409050723.jpg", false),
                Hotel("Lapad", "https://edge.media.datahc.com/HI401664845.jpg", false),
                Hotel("Valamar Lacroma", "https://edge.media.datahc.com/HI380766419.jpg", false))
        hotels.value = hotelList
    }

    fun getHotels() : LiveData<List<Hotel>> = hotels

    fun getHotel(name: String) : LiveData<Hotel> {
        val data = MutableLiveData<Hotel>()
        data.value = hotels.value?.first { it.name.equals(name) }
        return data
    }

    fun markHotelAsSeen(hotelName: String?) {
        hotels.value?.firstOrNull { it.name.equals(hotelName) }?.seen = true
    }

    fun getNextUnseenHotelName() : String {
        val nextHotel = hotels.value?.firstOrNull { !it.seen }
        return when (nextHotel) {
            null -> {
                resetHotels()
                getNextUnseenHotelName()
            }
            else -> nextHotel.name
        }
    }

    fun resetHotels() {
        //TODO make dao do it in one query ideally
        hotels.value?.filter { it.seen }?.forEach({it.seen = false})
    }
}