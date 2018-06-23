package com.miroslavkacera.rxapp.ui.presentation

import android.arch.lifecycle.ViewModel

class PresentationVM : ViewModel() {

    //TODO getHotels from api
    //TODO getUserPreferences from api
    //TODO zip hotels and prefs to filter hotels with price in range from prefs.costMin to prefs.costMax && show hotel wo description
    // if prefs are not loaded, show all
    //TODO take first unseen hotel (All hotels are unseen at first) and show it in fragment
    //TODO show button more with seconds counting down to 0 then mark hotel as seen and take another hotel
    //TODO after all hotels are seen, reset whole list to unseen

    //TODO pass clicked hotel to ReserveFragment

    //Optional
    //TODO shows status message
    // success - Hotels within price range from %1$d to %2$d
    // getHotels success && getUserPreferences failed - Set your preferences
    // both error - Hotels fetch error
}