package com.miroslavkacera.rxapp.repository

import com.miroslavkacera.rxapp.db.HotelDao
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.remote.HotelService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HotelRepositoryImpl(private val hotelsDao: HotelDao, private val remoteService: HotelService) : HotelRepository {

    override fun getUserPreferences() = remoteService.getUserPreferences()

    override fun getHotels(): Flowable<List<Hotel>> {
        return hotelsDao.loadHotels().concatMap { hotels ->
            if (hotels.isEmpty()) remoteService.getHotels().doAfterSuccess { remoteHotels -> hotelsDao.insertHotelList(remoteHotels)}.toFlowable() else Flowable.just(hotels)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun refreshData(): Completable {
        return remoteService.getHotels().doOnSuccess { hotels -> hotelsDao.insertHotelList(hotels) }.ignoreElement()
    }
}