package com.miroslavkacera.rxapp.ui.presentation

import android.annotation.SuppressLint
import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.Bindable
import android.os.Bundle
import android.support.annotation.ColorRes
import android.view.View
import com.kacera.utils.bindableviewmodel.BR
import com.kacera.utils.bindableviewmodel.BindableViewModel
import com.miroslavkacera.rxapp.R
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import com.miroslavkacera.rxapp.repository.HotelRepository
import com.miroslavkacera.rxapp.utils.FormattedString
import com.miroslavkacera.rxapp.utils.SignalingObservable
import com.miroslavkacera.rxapp.utils.Timer
import com.miroslavkacera.rxapp.utils.plusAssign
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class HotelsFragmentViewModel(private val hotelRepository: HotelRepository) : BindableViewModel(), DefaultLifecycleObserver, HotelViewModel {

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

    private val loadingSubject = BehaviorSubject.createDefault(true)
    var loading: Boolean
        @Bindable get() = loadingSubject.value!!
        private set(value) {
            loadingSubject.onNext(value)
            notifyPropertyChanged(BR.loading)
        }

    @get:Bindable
    var timerValue: Int = 5
        private set(value) {
            field = value
            notifyPropertyChanged(BR.timerValue)
        }

    private var hotels: List<Hotel>? = null
        set(value) {
            field = value
            notifyChange()
        }

    private val disposables = CompositeDisposable()
    private var timerDisposable: Disposable? = null
    private val timer = Timer(timerValue, ::markHotelAsSeen)

    val buttonSignal: SignalingObservable<Bundle> = SignalingObservable()

    init {
        fetchHotelsFromRemote()
    }

    @SuppressLint("RxSubscribeOnError")
    override fun onResume(owner: LifecycleOwner) {
        disposables += loadingSubject.distinctUntilChanged().subscribe { loading ->
            if (loading) {
                timer.reset()
                timerDisposable?.dispose()
            } else {
                timerDisposable = timer.countDown().subscribe({ tick -> timerValue = tick }, { error -> error.printStackTrace() })
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        timerDisposable?.dispose()
    }

    private fun fetchHotelsFromRemote() {
        disposables += Flowable.combineLatest(hotelRepository.getHotels(), hotelRepository.getUserPreferences().doOnSuccess { preference ->
            statusMessage = FormattedString.from(R.string.message_success, preference.costMin, preference.costMax)
            statusColor = R.color.success
        }.toFlowable(),
                BiFunction<List<Hotel>, UserPreferences, Pair<List<Hotel>, UserPreferences>> { hotels, preference -> hotels to preference })
                .map { pair ->
                    pair.first.filter { it.price in pair.second.costMin..pair.second.costMax && !it.seen }
                }
                .subscribe({ hotels ->
                    if (hotels.isEmpty()) {
                        hotelRepository.resetHotels()
                    } else {
                        this.hotels = hotels
                        loading = false
                    }
                }, { error ->
                    statusMessage = FormattedString.from(error.localizedMessage)
                    statusColor = R.color.error
                })
    }

    private fun markHotelAsSeen() {
        hotels?.get(0)?.let {
            it.seen = true
            hotelRepository.markHotelSeen(it)
        }
    }

    @Bindable
    fun isRefreshing() = loading

    fun setRefreshing(refreshing: Boolean) {
        loading = refreshing
        if (refreshing) {
            disposables += hotelRepository.refreshData().subscribe({}, { error ->
                statusMessage = FormattedString.from(error.localizedMessage)
                statusColor = R.color.error
            })
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

    fun onButtonClicked(view: View) {
        val bundle = Bundle()
        bundle.putString("hotelName", getName())
        buttonSignal.onNext(bundle)
    }

    override fun onCleared() {
        super.onCleared()

        disposables.clear()
    }

    class Factory constructor(private val repository: HotelRepository) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                HotelsFragmentViewModel(repository) as T
    }
}