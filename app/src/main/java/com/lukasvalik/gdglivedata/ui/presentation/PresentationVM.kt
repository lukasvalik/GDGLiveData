package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.*
import com.lukasvalik.gdglivedata.R
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.core.OnScreenTimer
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.UserPreferences
import com.lukasvalik.gdglivedata.ui.common.StatusMessage
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status

class PresentationVM(private val repository: HotelRepository, app: App) : AndroidViewModel(app) {

    // interface from Repository
    private val userPrefs: LiveData<Resource<UserPreferences>> = repository.getUserPreferences()
    private val allHotels: LiveData<Resource<List<Hotel>>> = repository.allHotels

    //triggers
    private val hotels = MediatorLiveData<Resource<List<Hotel>>>()
    private val hotelName = MediatorLiveData<String>()

    // interface for UI & business logic (Data transformations)
    val loading: LiveData<Boolean> = Transformations.switchMap(hotels) {
        MutableLiveData<Boolean>().from(hotels.value?.status == Status.LOADING)
    }
    val timer = OnScreenTimer(5, { markHotelAsSeen() })
    val hotel: LiveData<Hotel> = Transformations.switchMap(hotelName, { repository.getHotel(it) })
    val statusMessage: LiveData<StatusMessage?> = Transformations.map(hotels) {
        StatusMessage(it.status, it.message, userPrefs.value?.data?.costMin, userPrefs.value?.data?.costMax)
    }

    init {

        // Define additional business logic
        hotels.zip(allHotels, userPrefs,
                object : Function2<Resource<List<Hotel>>, Resource<UserPreferences>, LiveData<Resource<List<Hotel>>>> {

                    override fun apply(t1: Resource<List<Hotel>>?, t2: Resource<UserPreferences>?): LiveData<Resource<List<Hotel>>> {
                        val result = MutableLiveData<Resource<List<Hotel>>>()
                        val value: Resource<List<Hotel>> =
                                if (t1?.data != null && t1.data.isNotEmpty()) {
                                    // allHotels are loaded successfully
                                    when {
                                        t2?.status == Status.LOADING -> Resource.loading(null) // wait for prefs
                                        t2?.data != null -> // allHotels & userPreferences are loaded successfully
                                            Resource.success(t1.data.filter { it.price in t2.data.costMin..t2.data.costMax })
                                        else -> // allHotels loaded successfully, but userPreferences failed
                                            Resource.error(app.getString(R.string.message_error_no_prefs), t1.data)
                                    }
                                } else {
                                    // Hotels failed or are empty
                                    Resource.error(app.getString(R.string.message_error_no_hotels), null)
                                }
                        result.value = value
                        return result
                    }

                })

        // Adding source without ever removing it - careful! do not add the same source again
        // hotelName will be evaluated each time there will be any change in database.
        // Changes are following - setting default hotelList, marking hotel as seen, reset seen flag
        hotelName.addSource(hotels, {
            it?.data?.let {
                if (!it.isEmpty()) {
                    val nextHotel = it.firstOrNull { !it.seen }
                    if (nextHotel == null) {
                        // all allHotels have been seen -> reset the seen flag
                        repository.resetSeenHotels()
                    } else {
                        // there are unseen allHotels or database has been reset and all allHotels are unseen now
                        hotelName.value = nextHotel.name
                    }
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

    class Factory constructor(private val app: App) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
                PresentationVM(app.hotelRepository, app) as T
    }
}