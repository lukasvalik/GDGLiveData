package com.lukasvalik.gdglivedata.core

import android.arch.lifecycle.*
import android.os.Handler

class OnScreenTimer(private val countDownTime: Int, onCountDown:() -> Unit) : LifecycleObserver {

    val seconds = MutableLiveData<Int>()
    private val handler = Handler()
    private lateinit var runnable: Runnable

    init {
        seconds.value = countDownTime
        runnable = Runnable {
            if (seconds.value == 1) {
                onCountDown()
                reset()
            } else {
                seconds.value = seconds.value?.minus(1)
                handler.postDelayed(runnable, 1000)
            }
        }
    }

    private fun reset() {
        seconds.value = countDownTime
        startCounting()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startCounting() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 1000)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun stopCounting() {
        handler.removeCallbacks(runnable)
    }
}