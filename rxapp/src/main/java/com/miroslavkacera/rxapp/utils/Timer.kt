package com.miroslavkacera.rxapp.utils

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Timer(private val targetTime: Int = 5, private val onCountDown:() -> Unit) {

    private var currentTime = targetTime

    fun countDown(): Observable<Int> {
        return Observable.timer(1L, TimeUnit.SECONDS, Schedulers.computation())
                .flatMap { _ -> Observable.just(getTime()) }
                .repeat()
                .startWith(currentTime)
                .share()
    }

    @Synchronized
    fun reset() {
        currentTime = targetTime
    }

    @Synchronized
    private fun getTime(): Int {
        if (--currentTime <= 0) {
            currentTime = targetTime
            onCountDown()
        }

        return currentTime
    }
}