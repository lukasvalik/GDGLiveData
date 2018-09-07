package com.miroslavkacera.rxapp.ui.presentation

import android.databinding.Bindable
import android.databinding.Observable


interface HotelViewModel : Observable {
    @Bindable fun getUrl(): String?
    @Bindable fun getName(): String?
    @Bindable fun getDescription(): String?
}