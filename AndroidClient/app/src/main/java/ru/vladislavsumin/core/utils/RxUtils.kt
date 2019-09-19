@file:Suppress("unused")

package ru.vladislavsumin.core.utils

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.KProperty

fun Completable.subscribeOnIo() = this.subscribeOn(Schedulers.io())
fun Completable.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeOnIo() = this.subscribeOn(Schedulers.io())
fun <T> Single<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.observeOnMainThread(): Observable<T> = this.observeOn(AndroidSchedulers.mainThread())
fun <T> Observable<T>.subscribeOnIo(): Observable<T> = this.subscribeOn(Schedulers.io())


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