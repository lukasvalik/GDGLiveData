package com.miroslavkacera.rxapp.repository

import com.miroslavkacera.rxapp.Executors
import com.miroslavkacera.rxapp.db.HotelDao
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.remote.HotelService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HotelRepositoryImpl(private val hotelsDao: HotelDao,
                          private val remoteService: HotelService,
                          private val executors: Executors = Executors.getInstance()) : HotelRepository {

    override fun getUserPreferences() = remoteService.getUserPreferences()

    override fun getHotels(): Flowable<List<Hotel>> {
        return hotelsDao.loadHotels().concatMap { hotels ->
            if (hotels.isEmpty()) remoteService.getHotels().doAfterSuccess { remoteHotels -> hotelsDao.insertHotelList(remoteHotels) }.toFlowable() else Flowable.just(hotels)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun getHotel(name: String): Single<Hotel> = hotelsDao.getHotelByName(name).subscribeOn(Schedulers.io())

    override fun refreshData(): Completable {
        return remoteService.getHotels().doOnSuccess { hotels -> hotelsDao.insertHotelList(hotels) }.ignoreElement()
    }

    override fun markHotelSeen(hotel: Hotel) {
        executors.diskIO().execute { hotelsDao.markHotelSeen(hotel.name) }
    }

    override fun resetHotels() {
        executors.diskIO().execute { hotelsDao.resetAllHotels() }
    }
}