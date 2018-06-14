package com.lukasvalik.gdglivedata.core

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Handler

class ChainRequestMock(private val session: SessionMock) {

    companion object {
        const val DELAY = 2000L
    }

    // provides info about state of chained requests
    enum class ChainRequestStatus(val loading: Boolean) {
        BEFORE_LOADING(true),
        LOADING_REQUEST_1(true),
        ERROR_1(false),
        LOADING_REQUEST_2(true),
        ERROR_2(false),
        DONE(false)
    }

    // status of request chain showed in fragment
    val chainRequestStatus = MutableLiveData<ChainRequestStatus>()

    private val requestHandler = Handler()

    /**
     * Request chain mock
     */

    private val request1 = MutableLiveData<Boolean>()
    private val request2 = Transformations.switchMap(request1, {
        val req = MutableLiveData<Boolean>()
        it?.let {
            if (it) {
                // request1 was successful - update view & schedule next request in chain
                chainRequestStatus.value = ChainRequestStatus.LOADING_REQUEST_2
                scheduleResponse2(req)
            } else {
                // request1 was unsuccessful - show error
                chainRequestStatus.value = ChainRequestStatus.ERROR_1
            }
        }
        req
    })

    fun getResult() : LiveData<Boolean> = request2

    fun scheduleLoading() {
        chainRequestStatus.value = ChainRequestStatus.BEFORE_LOADING
        requestHandler.postDelayed({
            chainRequestStatus.value = ChainRequestStatus.LOADING_REQUEST_1
            scheduleFinishLoadingRequest1()
        }, DELAY)
    }

    private fun scheduleFinishLoadingRequest1() {
        val active = session.active.value ?: true
        requestHandler.postDelayed({ request1.value = active }, DELAY) // this triggers request2
    }

    private fun scheduleResponse2(req: MutableLiveData<Boolean>) {
        requestHandler.postDelayed({
            session.active.value?.let {
                chainRequestStatus.value = if (it) ChainRequestStatus.DONE else ChainRequestStatus.ERROR_2
                req.value = it
            }
        }, DELAY)
    }
}