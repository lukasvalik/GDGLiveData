package com.lukasvalik.gdglivedata

import android.arch.lifecycle.*
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status

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

// Zip 2 sources - Based on Transformations.SwitchMap
/*fun <T1, T2, R> MediatorLiveData<Resource<R>>.zipResource(t1Data: LiveData<Resource<T1>>, t2Data: LiveData<Resource<T2>>,
                                            func: Function2<T1, T2, LiveData<Resource<R>>>) : MediatorLiveData<Resource<R>> {

    addSource<Resource<T1>>(t1Data, object : Observer<Resource<T1>> {
        internal var mSource: LiveData<Resource<R>>? = null

        override fun onChanged(x: Resource<T1>?) {
            if (!t2Data.isLoadedWithData()) return

            val newLiveData = func.apply(t1Data.value?.data, t2Data.value?.data)

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

    addSource<Resource<T2>>(t2Data, object : Observer<Resource<T2>> {
        internal var mSource: LiveData<Resource<R>>? = null

        override fun onChanged(x: Resource<T2>?) {
            if (!t1Data.isLoadedWithData()) return

            val newLiveData = func.apply(t1Data.value?.data, t2Data.value?.data)

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
}*/

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

fun <T> LiveData<Resource<T>>.isLoadedWithData() =
        !isLoading() && (value?.status == Status.SUCCESS || value?.data != null)

fun <T> LiveData<Resource<T>>.isLoading() = value?.status != Status.LOADING

// Zip and stop observing
fun <T1, T2, R> MutableLiveData<R>.zip(t1Data: LiveData<T1>, t2Data: LiveData<T2>, owner: LifecycleOwner?, function: Function2<T1, T2, R>): MutableLiveData<R> {
    var dataReadyCount = 0
    val zipper = MediatorLiveData<R>()
    zipper.addSource(t1Data, {
        zipper.removeSource(t1Data)
        if (++dataReadyCount == 2) {
            value = function.apply(t1Data.value, t2Data.value)
        }
    })
    zipper.addSource(t2Data, {
        zipper.removeSource(t2Data)
        if (++dataReadyCount == 2) {
            value = function.apply(t1Data.value, t2Data.value)
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

