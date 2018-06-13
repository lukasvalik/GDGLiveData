package com.lukasvalik.gdglivedata.ui.reserve

import android.arch.lifecycle.*
import android.os.Handler
import com.lukasvalik.gdglivedata.core.SessionMock

class ReserveVM : ViewModel() {

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

    private val session = SessionMock()
    private val requestHandler = Handler()

    /**
     * UI inputs
     */
    val checkBoxChecked = MutableLiveData<Boolean>()
    // status of request chain showed in fragment
    val chainRequestStatus = MutableLiveData<ChainRequestStatus>()

    /**
     * Request chain mock
     */

    val request1 = MutableLiveData<Boolean>()
    val request2 = Transformations.switchMap(request1, {
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

    init {
        // start loading with observerInBackground
        checkBoxChecked.value = true
    }

    fun startLoading() {
        session.reset()
        chainRequestStatus.value = ChainRequestStatus.BEFORE_LOADING
        scheduleLoadingRequest1()
    }

    fun activeSession() : LiveData<Boolean> = session.active

    private fun scheduleLoadingRequest1() {
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