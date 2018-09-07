package com.miroslavkacera.rxapp.ui.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Bindable
import android.support.annotation.ColorRes
import com.kacera.utils.bindableviewmodel.BR
import com.kacera.utils.bindableviewmodel.BindableViewModel
import com.miroslavkacera.rxapp.R
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import com.miroslavkacera.rxapp.repository.HotelRepository
import com.miroslavkacera.rxapp.utils.FormattedString
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

class HotelsFragmentViewModel(private val hotelRepository: HotelRepository) : BindableViewModel(), HotelViewModel {

    @get:Bindable
    var statusMessage: FormattedString? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.statusMessage)
        }

    @ColorRes
    @get:Bindable
    var statusColor: Int = R.color.error
        private set(value) {
            field = value
            notifyPropertyChanged(BR.statusColor)
        }

    @get:Bindable
    var loading: Boolean = true
        private set(value) {
            field = value
            notifyPropertyChanged(BR.loading)
        }

    private var hotels: List<Hotel>? = null
        set(value) {
            field = value
            notifyChange()
        }

    private val disposables = CompositeDisposable()

    init {
        fetchHotelsFromRemote()
    }

    private fun fetchHotelsFromRemote() {
        disposables.add(Flowable.combineLatest(hotelRepository.getHotels(), hotelRepository.getUserPreferences().doOnSuccess { preference ->
            statusMessage = FormattedString.from(R.string.message_success, preference.costMin, preference.costMax)
            statusColor = R.color.success
        }.toFlowable(),
                BiFunction<List<Hotel>, UserPreferences, Pair<List<Hotel>, UserPreferences>> { hotels, preference -> hotels to preference })
                .map { pair ->
                    pair.first.filter { it.price in pair.second.costMin..pair.second.costMax }
                }
                .subscribe({ hotels ->
                    this.hotels = hotels
                    loading = false
                }, { error ->
                    statusMessage = FormattedString.from(error.localizedMessage)
                    statusColor = R.color.error
                }))

    }

    @Bindable
    fun isRefreshing() = loading

    fun setRefreshing(refreshing: Boolean) {
        loading = refreshing
        if (refreshing) {
            disposables.add(hotelRepository.refreshData().subscribe({}, { error ->
                statusMessage = FormattedString.from(error.localizedMessage)
                statusColor = R.color.error
            }))
        }
    }

    @Bindable
    override fun getUrl(): String? {
        return hotels?.get(0)?.url
    }

    @Bindable
    override fun getName(): String? {
        return hotels?.get(0)?.name
    }

    @Bindable
    override fun getDescription(): String? {
        return hotels?.get(0)?.description
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
    }

    class Factory constructor(private val repository: HotelRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                HotelsFragmentViewModel(repository) as T
    }

//TODO getHotels from api
    //TODO getUserPreferences from api
    //TODO zip hotels and prefs to filter hotels with price in range from prefs.costMin to prefs.costMax && show hotel wo description
    // if prefs are not loaded, show all
    //TODO take first unseen hotel (All hotels are unseen at first) and show it in fragment
    //TODO show button more with seconds counting down to 0 then mark hotel as seen and take another hotel
    //TODO after all hotels are seen, reset whole list to unseen

    //TODO pass clicked hotel to ReserveFragment

    //Optional
    //TODO shows status message
    // success - Hotels within price range from %1$d to %2$d
    // getHotels success && getUserPreferences failed - Set your preferences
    // both error - Hotels fetch error
}