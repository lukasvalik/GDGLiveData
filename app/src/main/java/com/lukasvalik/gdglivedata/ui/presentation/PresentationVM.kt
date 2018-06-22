package com.lukasvalik.gdglivedata.ui.presentation

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.Function2
import com.lukasvalik.gdglivedata.Repository.HotelRepository
import com.lukasvalik.gdglivedata.core.OnScreenTimer
import com.lukasvalik.gdglivedata.db.Hotel
import com.lukasvalik.gdglivedata.db.UserPreferences
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status
import com.lukasvalik.gdglivedata.zipResource

class PresentationVM(private val repository: HotelRepository) : ViewModel() {

    // interface from Repository
    private val userPreferences: LiveData<Resource<UserPreferences>> = repository.getUserPreferences()
    private val allHotels: LiveData<Resource<List<Hotel>>> = repository.getHotels()

    //triggers
    private val hotels = MediatorLiveData<Resource<List<Hotel>>>()
    private val hotelName = MediatorLiveData<String>()

    // interface for UI
    val timer = OnScreenTimer(5, { markHotelAsSeen() })
    val hotel: LiveData<Hotel> = Transformations.switchMap(hotelName, { getHotel(it) })
    // TODO create
    val errorMessage = Transformations.map(hotels) {
        if (it.status != Status.ERROR) null else it.message
    }

    init {

        // business logic
        hotels.zipResource(allHotels, userPreferences,
                object : Function2<Resource<List<Hotel>>, Resource<UserPreferences>, LiveData<Resource<List<Hotel>>>> {

                    override fun apply(t1: Resource<List<Hotel>>?, t2: Resource<UserPreferences>?): LiveData<Resource<List<Hotel>>> {
                        val result = MutableLiveData<Resource<List<Hotel>>>()
                        val value: Resource<List<Hotel>> = if (t1?.data != null) {
                            // hotels are loaded successfully
                            if (t2?.data != null) {
                                // userPreferences are loaded successfully
                                Resource.success(t1.data.filter { it.price in t2.data.costMin .. t2.data.costMax})
                            } else {
                                Resource.error("Set Your Preferences", t1.data)
                            }
                        } else {
                            Resource.error("Hotels Fetching Error", null)
                        }
                        result.value = value
                        return result
                    }

                })

        // Adding source without ever removing it - make sure you do not add the same source again
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
    }

    private fun getHotel(name: String): LiveData<Hotel> {
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