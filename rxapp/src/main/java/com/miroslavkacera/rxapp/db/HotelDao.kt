package com.miroslavkacera.rxapp.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.miroslavkacera.rxapp.model.Hotel
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface HotelDao {
    @Query("SELECT * FROM Hotel")
    fun loadHotels(): Flowable<List<Hotel>>

    @Query("SELECT * FROM Hotel WHERE name = :name")
    fun getHotelByName(name: String): Single<Hotel>

    @Query("UPDATE Hotel SET seen = 0 WHERE name = :name")
    fun resetHotel(name: String)

    @Query("UPDATE Hotel SET seen = 0")
    fun resetAllHotels()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHotelList(hotels: List<Hotel>)

    @Query("UPDATE Hotel SET seen = 1 WHERE name = :name")
    fun markHotelSeen(name: String)
}