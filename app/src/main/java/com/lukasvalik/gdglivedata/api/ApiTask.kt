package com.lukasvalik.gdglivedata.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.lukasvalik.gdglivedata.vo.Resource
import com.lukasvalik.gdglivedata.vo.Status

/**
 * Creates WebService call and delivers results as LiveData<Resource<T>>
 */

abstract class ApiTask<T> {
    companion object {
        const val ILLEGAL_STATE_RESPONSE_ERROR = "Exception could be thrown as well"
    }

    protected val result = MediatorLiveData<Resource<T>>()
    protected val status = Transformations.switchMap(result, {
        it?.let {
            val status = MutableLiveData<Status>()
            status.value = it.status
            status
        }
    })

    abstract fun service() : LiveData<ApiResponse<T>>

    protected open fun onFetchFailed() {

    }

    // execute service call and return data
    public open fun execute(data: T?) : LiveData<Resource<T>> {
        result.value = Resource.loading(data)
        result.addSource(service(), {
            result.removeSource(service())
            onFetchFinished(it)
        })
        return result
    }

    fun isExecuting() = status == Status.LOADING

    protected open fun onFetchFinished(response: ApiResponse<T>?) {
        result.value = when (response) {
            is ApiSuccessResponse -> Resource.success(response.body)
            is ApiEmptyResponse -> Resource.success(null)
            is ApiErrorResponse -> deliverError(response.errorMessage)
            else -> deliverError(ILLEGAL_STATE_RESPONSE_ERROR)
        }
    }

    fun asLiveData() = result

    private fun deliverError(message: String) : Resource<T>{
        onFetchFailed()
        return Resource.error(message, null)
    }
}