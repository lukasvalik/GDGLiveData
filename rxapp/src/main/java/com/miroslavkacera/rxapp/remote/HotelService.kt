package com.miroslavkacera.rxapp.remote

import android.util.Log
import com.miroslavkacera.rxapp.model.Hotel
import com.miroslavkacera.rxapp.model.UserPreferences
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://private-1b8cc-lukasvalik.apiary-mock.com/"

interface HotelService {

    companion object {
        val API = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor { message -> Log.d("OkHttp", message) }.setLevel(HttpLoggingInterceptor.Level.BASIC)).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(HotelService::class.java)!!
    }

    @GET("hotels")
    fun getHotels(): Single<List<Hotel>>

    @GET("userPreferences")
    fun getUserPreferences(): Single<UserPreferences>
}