package ru.vladislavsumin.core.utils

import android.util.Log
import io.reactivex.Observable

inline fun <reified T> tag(): String {
    return T::class.java.simpleName
}

fun <T> Observable<T>.log(tag: String, name: String): Observable<T> {
    return this
            .doOnSubscribe { Log.d(tag, "[rx:$name] subscribe") }
            .doOnDispose { Log.d(tag, "[rx:$name] dispose") }
            .doOnNext { Log.d(tag, "[rx:$name] next: $it") }
            .doOnError { Log.e(tag, "[rx:$name] error: $it") }
}