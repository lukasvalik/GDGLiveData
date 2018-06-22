package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.*
import android.util.Log
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.core.OnScreenTimer
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.UserPreferences
import com.lukasvalik.gdglivedata.vo.Resource

class PresentationVM(private val repository: HotelRepository) : ViewModel() {

    private val hotels : LiveData<Resource<List<Hotel>>> = repository.getHotels()
    private val userPreferences : LiveData<Resource<UserPreferences>> = repository.getUserPreferences()
    private val hotelName = MediatorLiveData<String>()

    val hotel: LiveData<Hotel> = Transformations.switchMap(hotelName, { getHotel(it) })
    val timer = OnScreenTimer(5, { markHotelAsSeen() })

    init {
        // Adding source without ever removing it is safe only in init method
        // hotelName will be evaluated each time there will be any change in database.
        // Changes are following - setting default hotelList, marking hotel as seen, reset seen flag
        hotelName.addSource(hotels, {
            it?.data?.let {
                if (!it.isEmpty()) {
                    val nextHotel = it.firstOrNull { !it.seen }
                    if (nextHotel == null) {
                        // all hotels have been seen -> reset the seen flag
                        repository.resetSeenHotels()
                    } else {
                        // there are unseen hotels or database has been reset and all hotels are unseen now
                        hotelName.value = nextHotel.name
                    }
                }
            }
        })
        hotelName.addSource(userPreferences) {
            Log.d("userPrefs", "userPreference")
        }
    }

    private fun getHotel(name: String) : LiveData<Hotel> {
        val data = MutableLiveData<Hotel>()
        data.value = hotels.value?.data?.first { it.name == name }
        return data
    }

    private fun markHotelAsSeen() {
        hotel.value?.let {
            it.seen = true
            repository.updateHotel(it)
        }
    }

    class Factory constructor(private val repository: HotelRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PresentationVM(repository) as T
    }
}