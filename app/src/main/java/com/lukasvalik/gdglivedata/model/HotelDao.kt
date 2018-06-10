package com.lukasvalik.gdglivedata.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface HotelDao {
    @Query("SELECT * FROM Hotel")
    fun loadHotels(): LiveData<List<Hotel>>

    @Query("SELECT * FROM Hotel WHERE name = :name")
    fun getHotelByName(name: String): LiveData<Hotel>

    @Query("UPDATE Hotel SET seen = 0")
    fun resetSeenHotels()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHotelList(hotels: List<Hotel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHotel(hotel: Hotel)
}