package com.miroslavkacera.rxapp.utils

import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.ObservableEmitter
import io.reactivex.Observer
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


class SignalingObservable<T> : Observable<T>(), ObservableSignal<T> {

    private var lastValue: AtomicReference<T> = AtomicReference()
    private var lastError: AtomicReference<Throwable> = AtomicReference()

    private var emitter: ObservableEmitter<T>? = null
    private var sharedObservable: Observable<T>? = null

    private val observersCount: AtomicInteger = AtomicInteger(0)

    override fun subscribeActual(observer: Observer<in T>) {
        if (observersCount.getAndIncrement() == 0) {
            if (lastError.get() != null) {
                sharedObservable = error<T>(lastError.getAndSet(null))
            } else {
                val createObservable = create<T> { e ->
                    emitter = e
                }

                sharedObservable = if (lastValue.get() != null) {
                    just(lastValue.getAndSet(null)).concatWith(createObservable)
                } else {
                    createObservable
                }
            }

            sharedObservable = sharedObservable?.doFinally {  observersCount.set(0) }
                    ?.share()
                    ?.doOnDispose { observersCount.decrementAndGet() }
        }

        sharedObservable?.subscribe(observer)
    }

    fun hasObservers() = observersCount.get() > 0

    fun getObserversCount(): Int = observersCount.get()

    override fun onComplete() = signalSafe { onComplete() }

    override fun onNext(value: T) =
            if (!signalSafe { onNext(value) }) {
                lastValue.set(value)
                false
            } else {
                true
            }

    override fun onError(error: Throwable) =
            if (!signalSafe { onError(error) }) {
                lastError.set(error)
                false
            } else {
                true
            }

    private inline fun signalSafe(block: ObservableEmitter<T>.() -> Unit): Boolean {
        if (hasObservers()) {
            emitter?.takeUnless { it.isDisposed }?.run {
                block()
                return true
            }
        }

        return false
    }
}