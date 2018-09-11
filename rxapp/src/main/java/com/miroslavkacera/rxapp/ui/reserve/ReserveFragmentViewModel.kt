package com.miroslavkacera.rxapp.ui.reserve

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Bindable
import android.view.View
import com.kacera.utils.bindableviewmodel.BindableViewModel
import com.miroslavkacera.rxapp.BR
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.repository.HotelRepository
import com.miroslavkacera.rxapp.ui.presentation.HotelViewModel
import com.miroslavkacera.rxapp.utils.mock.ChainRequestMock
import com.miroslavkacera.rxapp.utils.mock.SessionMock
import com.miroslavkacera.rxapp.utils.plusAssign
import io.reactivex.disposables.CompositeDisposable

class ReserveFragmentViewModel(hotelRepository: HotelRepository, hotelName: String) : BindableViewModel(), HotelViewModel {

    @get:Bindable
    private var hotel: Hotel? = null
        set(value) {
            field = value
            notifyChange()
        }

    @get:Bindable
    var loading: Boolean = true
        private set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }

    @get:Bindable
    var status: String = ""
        private set(value) {
            field = value
            notifyPropertyChanged(BR.status)
        }

    private val session = SessionMock()
    private val chainRequest = ChainRequestMock(session)
    private val disposables = CompositeDisposable()

    init {
        disposables += hotelRepository.getHotel(hotelName).subscribe({ hotel ->
            run {
                this.hotel = hotel
                loading = false
            }
        }, { error -> this.status = error.localizedMessage })

        disposables += chainRequest.chainRequestStatus.subscribe({ status ->
            this.status = status.name
            loading = status.loading
        }, { _ ->
            this.status = ChainRequestMock.ChainRequestStatus.ERROR_RESERVATION.name
            loading = false
        })
    }

    @Bindable
    override fun getUrl(): String? = hotel?.url

    @Bindable
    override fun getName(): String? = hotel?.name

    @Bindable
    override fun getDescription(): String? = hotel?.description

    fun onButtonClicked(view: View) {
        session.reset()
        chainRequest.startLoading()
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
    }

    class Factory constructor(private val repository: HotelRepository, private val hotelName: String) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                ReserveFragmentViewModel(repository, hotelName) as T
    }
}