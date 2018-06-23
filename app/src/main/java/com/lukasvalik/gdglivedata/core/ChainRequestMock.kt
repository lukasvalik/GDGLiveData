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
        BEFORE_LOADING(false),
        CALL_VERIFYING_SMT(true),
        ERROR_VERIFICATION(false),
        CALL_RESERVATION(true),
        ERROR_RESERVATION(false),
        RESERVED_SUCCESSFULLY(false)
    }

    private val requestHandler = Handler()

    // status of request chain showed in fragment
    val chainRequestStatus = MutableLiveData<ChainRequestStatus>()

    private val request1 = MutableLiveData<Boolean>()
    private val request2 = Transformations.switchMap(request1, {
        val req = MutableLiveData<Boolean>()
        it?.let {
            if (it) {
                // request1 was successful - update view & schedule next request in chain
                chainRequestStatus.value = ChainRequestStatus.CALL_RESERVATION
                scheduleResponse2(req)
            } else {
                // request1 was unsuccessful - show error
                chainRequestStatus.value = ChainRequestStatus.ERROR_VERIFICATION
            }
        }
        req
    })

    init {
        chainRequestStatus.value = ChainRequestStatus.BEFORE_LOADING
    }

    fun getResult() : LiveData<Boolean> = request2

    fun startLoading() {
        requestHandler.post({
            chainRequestStatus.value = ChainRequestStatus.CALL_VERIFYING_SMT
            scheduleFinishLoadingRequest1()
        })
    }

    fun resetStatus() {
        chainRequestStatus.value = ChainRequestStatus.BEFORE_LOADING
    }

    private fun scheduleFinishLoadingRequest1() {
        val active = session.active.value ?: true
        requestHandler.postDelayed({ request1.value = active }, DELAY) // this triggers request2
    }

    private fun scheduleResponse2(req: MutableLiveData<Boolean>) {
        requestHandler.postDelayed({
            session.active.value?.let {
                chainRequestStatus.value = if (it) ChainRequestStatus.RESERVED_SUCCESSFULLY else ChainRequestStatus.ERROR_RESERVATION
                req.value = it
            }
        }, DELAY)
    }
}