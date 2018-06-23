package com.lukasvalik.gdglivedata.core

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Handler

class SessionMock {

    companion object {
        const val REFRESH_SESSION = 1000L
        const val SESSION_TIMEOUT = 5000L
    }

    private val sessionHandler = Handler()
    private lateinit var sessionRefreshRunnable: Runnable
    private var timestamp = System.currentTimeMillis()

    private var sessionRefreshed = MutableLiveData<Boolean>()
    val active: LiveData<Boolean> = Transformations.switchMap(sessionRefreshed, {
        val active = MutableLiveData<Boolean>()
        val now = System.currentTimeMillis()
        active.value = timestamp + SESSION_TIMEOUT >= now
        if (active.value != false) {
            timestamp = now
        }
        active
    })

    init {
        sessionRefreshRunnable = Runnable {
            // you cannot refresh session which has been closed
            sessionRefreshed.value = active.value
            if (sessionRefreshed.value != false) {
                sessionHandler.postDelayed(sessionRefreshRunnable, REFRESH_SESSION)
            }
        }
        sessionHandler.post(sessionRefreshRunnable)
    }

    fun reset() {
        timestamp = System.currentTimeMillis()
        sessionRefreshed.value = true
        sessionHandler.postDelayed(sessionRefreshRunnable, REFRESH_SESSION)
    }
}