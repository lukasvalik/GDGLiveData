package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.core.OnScreenTimer
import com.lukasvalik.gdglivedata.db.Hotel

class PresentationVM(private val repository: HotelRepository) : ViewModel() {

    private val hotels = repository.getHotels()
    private val hotelName = MediatorLiveData<String>()

    val hotel: LiveData<Hotel> = Transformations.switchMap(hotelName, { repository.getHotel(it) })
    val timer = OnScreenTimer(5, { markHotelAsSeen() })

    init {
        // Adding source without ever removing it is safe only in init method
        // hotelName will be evaluated each time there will be any change in database.
        // Changes are following - setting default hotelList, marking hotel as seen, reset seen flag
        hotelName.addSource(hotels, {
            if (it == null || it.isEmpty()) {
                // database is empty put defaults values to database
                repository.setDefaultHotelList()
            } else {
                val nextHotel = it.firstOrNull { !it.seen }
                if (nextHotel == null) {
                    // all hotels have been seen -> reset the seen flag
                    repository.resetSeenHotels()
                } else {
                    // there are unseen hotels or database has been reset and all hotels are unseen now
                    hotelName.value = nextHotel.name
                }
            }
        })
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