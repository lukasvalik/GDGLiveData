package com.lukasvalik.gdglivedata.ui.reserve

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.core.ChainRequestMock
import com.lukasvalik.gdglivedata.core.SessionMock

class ReserveVM(repository: HotelRepository,
                hotelName: String) : ViewModel() {

    val hotel = repository.getHotel(hotelName)

    private val session = SessionMock()
    private val chainRequest = ChainRequestMock(session)

    val checkBoxChecked = MutableLiveData<Boolean>()

    init {
        checkBoxChecked.value = false // start loading with lifecycle aware observer
    }

    fun startLoading() {
        session.reset()
        chainRequest.scheduleLoading()
    }

    fun isSessionActive() : LiveData<Boolean> = session.active

    fun getChainRequestResult() : LiveData<Boolean> = chainRequest.getResult()

    fun getChainRequestStatus() : LiveData<ChainRequestMock.ChainRequestStatus> = chainRequest.chainRequestStatus

    class Factory constructor(private val hotelRepository: HotelRepository,
                              private val hotelName: String) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                ReserveVM(hotelRepository, hotelName) as T
    }
}