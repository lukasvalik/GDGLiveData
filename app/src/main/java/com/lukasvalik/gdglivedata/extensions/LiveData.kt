package com.lukasvalik.gdglivedata.extensions

import android.arch.lifecycle.*

fun Lifecycle.reObserve(observer: LifecycleObserver) {
    this.removeObserver(observer)
    this.addObserver(observer)
}

fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<T>) {
    this.removeObserver(observer)
    this.observe(owner, observer)
}

// observeForever and unsubscribe once result is emitted
fun <T> LiveData<T>.observeInBackground(observer: Observer<T>) {
    val lastValueWillBeEmitted = this.value != null
    this.observeForever(UnRegisterObserver(this, observer, lastValueWillBeEmitted))
}

class UnRegisterObserver<T>(private val liveData: LiveData<T>,
                            private val observer: Observer<T>,
                            lastValueWillBeEmitted: Boolean) : Observer<T> {
    var lastValueHasBeenEmitted = !lastValueWillBeEmitted

    override fun onChanged(t: T?) {
        observer.onChanged(t)
        if (lastValueHasBeenEmitted) {
            liveData.removeObserver(this)
        } else {
            lastValueHasBeenEmitted = true
        }
    }
}

// Zip 2 sources
/*fun <T1, T2, R> LiveData<R>.zip(t1Data: LiveData<T1>, t2Data: LiveData<T2>,
                                owner: LifecycleOwner? = null, function: Function2<T1, T2, R>) {
    var dataReadyCount = 0

    if (owner == null) {
        t1Data.observeInBackground(Observer { if (++dataReadyCount == 2) function.apply(t1Data.value, t2Data.value) })
        t2Data.observeInBackground(Observer { if (++dataReadyCount == 2) function.apply(t1Data.value, t2Data.value) })
    } else {
        t1Data.reObserve(owner, Observer { if (++dataReadyCount == 2) function.apply(t1Data.value, t2Data.value) })
        t2Data.reObserve(owner, Observer { if (++dataReadyCount == 2) function.apply(t1Data.value, t2Data.value) })
    }
}*/

fun <T1, T2, R> MutableLiveData<R>.zip(t1Data: LiveData<T1>, t2Data: LiveData<T2>, owner: LifecycleOwner?, function: Function2<T1, T2, R>): MutableLiveData<R> {
    var dataReadyCount = 0
    val zipper = MediatorLiveData<R>()
    zipper.addSource(t1Data, {
        zipper.removeSource(t1Data)
        if (++dataReadyCount == 2) {
            this.value = function.apply(t1Data.value, t2Data.value)
        }
    })
    zipper.addSource(t2Data, {
        zipper.removeSource(t2Data)
        if (++dataReadyCount == 2) {
            this.value = function.apply(t1Data.value, t2Data.value)
        }
    })
    // add observer to start execution
    if (owner == null) {
        zipper.observeInBackground(Observer {  })
    } else {
        zipper.observe(owner, Observer {  })
    }
    return this
}

interface Function2<T1, T2, R> {
    fun apply(t1: T1?, t2: T2?): R
}

