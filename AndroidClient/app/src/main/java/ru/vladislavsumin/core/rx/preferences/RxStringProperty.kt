package ru.vladislavsumin.core.rx.preferences

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.core.utils.observeOnIo
import kotlin.reflect.KProperty

class RxStringProperty(private val preferences: SharedPreferences,
                       val name: String,
                       defaultValue: String) {

    private val disposable: Disposable
    private val value: BehaviorSubject<String> =
            BehaviorSubject.createDefault(preferences.getString(name, defaultValue))

    init {
        disposable = value
                .observeOnIo()
                .subscribe {
                    preferences.edit()
                            .putString(name, it)
                            .apply()
                }
    }

    fun get(): String = value.value!!

    fun set(value: String) {
        this.value.onNext(value)
    }

    fun observe(): Observable<String> = value

    fun dispose() {
        disposable.dispose()
    }

    operator fun getValue(any: Any, property: KProperty<*>): String = get()
    operator fun setValue(any: Any, property: KProperty<*>, s: String) = set(s)
}