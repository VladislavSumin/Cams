@file:Suppress("unused")

package ru.vladislavsumin.core.utils

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.KProperty

fun Completable.subscribeOnIo() = this.subscribeOn(Schedulers.io())
fun Completable.subscribeOnComputation() = this.subscribeOn(Schedulers.computation())
fun Completable.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
fun Completable.observeOnIo() = this.observeOn(Schedulers.io())
fun Completable.observeOnComputation() = this.observeOn(Schedulers.computation())

fun <T> Single<T>.subscribeOnIo() = this.subscribeOn(Schedulers.io())
fun <T> Single<T>.subscribeOnComputation() = this.subscribeOn(Schedulers.computation())
fun <T> Single<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
fun <T> Single<T>.observeOnIo() = this.observeOn(Schedulers.io())
fun <T> Single<T>.observeOnComputation() = this.observeOn(Schedulers.computation())

fun <T> Observable<T>.subscribeOnIo(): Observable<T> = this.subscribeOn(Schedulers.io())
fun <T> Observable<T>.subscribeOnComputation(): Observable<T> = this.subscribeOn(Schedulers.computation())
fun <T> Observable<T>.observeOnMainThread(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.observeOnIo(): Observable<T> = this.observeOn(Schedulers.io())
fun <T> Observable<T>.observeOnComputation(): Observable<T> = this.observeOn(Schedulers.computation())

fun <T> Flowable<T>.subscribeOnIo(): Flowable<T> = this.subscribeOn(Schedulers.io())
fun <T> Flowable<T>.subscribeOnComputation(): Flowable<T> = this.subscribeOn(Schedulers.computation())
fun <T> Flowable<T>.observeOnMainThread(): Flowable<T> = this.observeOn(AndroidSchedulers.mainThread())
fun <T> Flowable<T>.observeOnIo(): Flowable<T> = this.observeOn(Schedulers.io())
fun <T> Flowable<T>.observeOnComputation(): Flowable<T> = this.observeOn(Schedulers.computation())


operator fun <T> BehaviorSubject<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this.value!!
}

operator fun <T> BehaviorSubject<T>.setValue(thisRef: Any?, property: KProperty<*>, t: T) {
    this.onNext(t)
}

/**
 * Workaround
 *
 * Intellij idea does not see extension functions automatically
 * it need import manually
 */
fun <T> BehaviorSubject<T>.delegate() = BehaviorSubjectDelegate(this)

class BehaviorSubjectDelegate<T>(private val mBehaviorSubject: BehaviorSubject<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            mBehaviorSubject.getValue(thisRef, property)

    operator fun setValue(thisRef: Any?, property: KProperty<*>, t: T) =
            mBehaviorSubject.setValue(thisRef, property, t)
}