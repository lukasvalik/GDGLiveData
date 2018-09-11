package com.miroslavkacera.rxapp.utils.mock

import android.os.Handler
import com.miroslavkacera.rxapp.utils.SignalingObservable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

private const val DELAY = 2000L

class ChainRequestMock(private val session: SessionMock) {

    // provides info about state of chained requests
    enum class ChainRequestStatus(val loading: Boolean) {
        NO_RESERVATION(false),
        CALL_VERIFYING_SMT(true),
        ERROR_VERIFICATION(false),
        CALL_RESERVATION(true),
        ERROR_RESERVATION(false),
        RESERVED_SUCCESSFULLY(false)
    }

    private val requestHandler = Handler()
    private var sessionActive = false

    private var disposable: Disposable? = null

    // status of request chain showed in fragment
    val chainRequestStatus: BehaviorSubject<ChainRequestStatus> = BehaviorSubject.createDefault(ChainRequestStatus.NO_RESERVATION)

    private val request1 = SignalingObservable<Boolean>()
    private val request2 = request1.flatMap { success ->
        if (success) {
            scheduleResponse2()
            Observable.just(ChainRequestStatus.CALL_RESERVATION)
        } else {
            Observable.just(ChainRequestStatus.ERROR_VERIFICATION)
        }
    }

    init {
        request2.subscribe(chainRequestStatus)
    }

    fun startLoading() {
        disposable = session.active.subscribe({ active -> sessionActive = active },
                { _ -> sessionActive = false })

        requestHandler.post {
            chainRequestStatus.onNext(ChainRequestStatus.CALL_VERIFYING_SMT)
            scheduleResponse1()
        }
    }

    fun resetStatus() {
        requestHandler.removeCallbacksAndMessages(null)
        chainRequestStatus.onNext(ChainRequestStatus.NO_RESERVATION)
    }

    private fun scheduleResponse1() {
        requestHandler.postDelayed({ request1.onNext(sessionActive) }, DELAY) // this triggers request2
    }

    private fun scheduleResponse2() {
        requestHandler.postDelayed({
            sessionActive.let {
                chainRequestStatus.onNext(if (it) ChainRequestStatus.RESERVED_SUCCESSFULLY else ChainRequestStatus.ERROR_RESERVATION)
            }
            disposable?.dispose()
        }, DELAY)
    }
}