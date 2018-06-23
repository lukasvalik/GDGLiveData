package com.lukasvalik.gdglivedata

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status

fun Lifecycle.reObserve(observer: LifecycleObserver) {
    this.removeObserver(observer)
    this.addObserver(observer)
}

fun <T> MutableLiveData<T>.from(newValue: T) : MutableLiveData<T> {
    value = newValue
    return this
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

// Zip 2 sources - Based on Transformations.SwitchMap
fun <T1, T2, R> MediatorLiveData<Resource<R>>.zipResource(t1Data: LiveData<T1>, t2Data: LiveData<T2>,
                                                  func: Function2<T1, T2, LiveData<Resource<R>>>) : MediatorLiveData<Resource<R>> {

    addSource<T1>(t1Data, object : Observer<T1> {
        internal var mSource: LiveData<Resource<R>>? = null

        override fun onChanged(x: T1?) {

            val newLiveData = func.apply(t1Data.value, t2Data.value)

            if (mSource == newLiveData) {
                return
            }
            mSource?.let { removeSource<Resource<R>>(mSource!!) }

            mSource = newLiveData
            if (mSource != null) {
                addSource<Resource<R>>(mSource!!) { y -> value = y }
            }
        }
    })

    addSource<T2>(t2Data, object : Observer<T2> {
        internal var mSource: LiveData<Resource<R>>? = null

        override fun onChanged(x: T2?) {

            val newLiveData = func.apply(t1Data.value, t2Data.value)

            if (mSource == newLiveData) {
                return
            }
            mSource?.let { removeSource<Resource<R>>(mSource!!) }

            mSource = newLiveData
            if (mSource != null) {
                addSource<Resource<R>>(mSource!!) { y -> value = y }
            }
        }
    })

    return this
}

interface Function2<T1, T2, R> {
    fun apply(t1: T1?, t2: T2?): R
}

