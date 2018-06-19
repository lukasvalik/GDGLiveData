package com.lukasvalik.gdglivedata.Repository

import android.arch.lifecycle.*
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import com.lukasvalik.gdglivedata.AppExecutors
import com.lukasvalik.gdglivedata.api.*
import com.lukasvalik.gdglivedata.Function2
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status
import com.lukasvalik.gdglivedata.zipResource

abstract class RoomBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors,
                        private val task: ApiTask<ResultType>) {

    private val status = MutableLiveData<Status>()
    private val result = MediatorLiveData<Resource<ResultType>>()

    // TODO store data needs to set loading to LiveData, we need to check difference of data as well to not become stuck at loading and
    // TODo not updating data from

    init {
        result.zipResource(status, loadFromDb(), object : Function2<Status, ResultType, LiveData<Resource<ResultType>>> {
            override fun apply(t1: Status?, t2: ResultType?): LiveData<Resource<ResultType>> {
                t1?.let {
                    if (task.isExecuting() && result.value?.status == Status.LOADING && result.value?.data?.equals(t2) == true) {
                        return result
                    }

                    val resource = when (status) {
                        Status.LOADING -> Resource.loading(t2)
                        Status.SUCCESS -> Resource.success(t2)
                        else -> Resource.error("Error", t2) // TODO implement error message delivering
                    }
                    result.value = resource
                }
                return result
            }
        })

        //task.execute(null)

        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                refreshFromNetwork()
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun refreshFromNetwork() {
        task.execute(null)
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = loadFromDb()

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}