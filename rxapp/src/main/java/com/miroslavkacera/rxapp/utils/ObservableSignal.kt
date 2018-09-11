package com.miroslavkacera.rxapp.utils


interface ObservableSignal<in T> {
    fun onNext(value: T): Boolean
    fun onError(error: Throwable): Boolean
    fun onComplete(): Boolean
}