package com.lukasvalik.gdglivedata.api

import android.arch.lifecycle.LiveData
import com.lukasvalik.gdglivedata.vo.Resource

abstract class CacheableTask<T> : ApiTask<T>() {

    override fun execute(data: T?): LiveData<Resource<T>> {
        return if (shouldFetch()) {
            super.execute(data)
        } else {
            loadFromDb()
        }
    }

    override fun onFetchFinished(response: ApiResponse<T>?) {

        when (response) {
            is ApiSuccessResponse -> {
                appExecutors.diskIO().execute {
                    saveCallResult(processResponse(response))
                    appExecutors.mainThread().execute {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        result.addSource(loadFromDb()) { newData ->
                            setValue(Resource.success(newData))
                        }
                    }
                }
            }
            is ApiEmptyResponse -> {
                appExecutors.mainThread().execute {
                    // reload from disk whatever we had
                    result.addSource(loadFromDb()) { newData ->
                        setValue(Resource.success(newData))
                    }
                }
            }
            is ApiErrorResponse -> {
                onFetchFailed()
                result.addSource(dbSource) { newData ->
                    setValue(Resource.error(response.errorMessage, newData))
                }
            }
        }

        saveCallResult()
        result.addSource(loadFromDb(), {
            result.removeSource(loadFromDb())
            result.value = it
        })
    }

    abstract fun shouldFetch() : Boolean

    abstract fun saveCallResult()

    abstract fun loadFromDb() : LiveData<Resource<T>>
}