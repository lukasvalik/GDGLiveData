package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.component.OnScreenTimer
import com.lukasvalik.gdglivedata.model.Hotel

class PresentationVM(private val repository: HotelRepository) : ViewModel() {

    private val hotelName = MutableLiveData<String>()
    val hotel : LiveData<Hotel> = Transformations.switchMap(hotelName, {repository.getHotel(it)})
    val timer = OnScreenTimer(10, {
        repository.markHotelAsSeen(hotelName.value)
        hotelName.value = repository.getNextUnseenHotelName()
    })

    init {
        hotelName.value = repository.getNextUnseenHotelName()
    }

    class Factory constructor(private val repository: HotelRepository) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PresentationVM(repository) as T
    }
}