package com.lukasvalik.gdglivedata.api.task

import android.arch.lifecycle.LiveData
import com.lukasvalik.gdglivedata.api.ApiResponse
import com.lukasvalik.gdglivedata.api.HotelService
import com.lukasvalik.gdglivedata.api.ApiTask
import com.lukasvalik.gdglivedata.db.Hotel

class HotelListTask(service: HotelService) : ApiTask<List<Hotel>>(service) {

    override fun service(): LiveData<ApiResponse<List<Hotel>>> = service.getHotels()
}