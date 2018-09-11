package com.miroslavkacera.rxapp.utils.mock

import android.os.Handler
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

private const val REFRESH_SESSION = 1000L
private const val SESSION_TIMEOUT = 5000L

class SessionMock {

    private val sessionHandler = Handler()
    private lateinit var sessionRefreshRunnable: Runnable
    private var timestamp = System.currentTimeMillis()

    private val sessionRefreshed: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    val active: Observable<Boolean> = sessionRefreshed.flatMap { _ ->
        val now = System.currentTimeMillis()
        val isActive = (timestamp + SESSION_TIMEOUT >= now).also { if (it) timestamp = now }
        Observable.just<Boolean>(isActive)
    }.share().distinctUntilChanged()

    init {
        active.subscribe(sessionRefreshed)
        sessionRefreshRunnable = Runnable {
            sessionRefreshed.onNext(true)
            if (sessionRefreshed.value != false) {
                sessionHandler.postDelayed(sessionRefreshRunnable, REFRESH_SESSION)
            }
        }
        sessionHandler.post(sessionRefreshRunnable)
    }

    fun reset() {
        sessionHandler.removeCallbacks(sessionRefreshRunnable)
        timestamp = System.currentTimeMillis()
        sessionRefreshed.onNext(true)
        sessionHandler.postDelayed(sessionRefreshRunnable, REFRESH_SESSION)
    }
}