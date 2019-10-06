package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Completable

interface ConnectionManagerI {
    fun checkConnection(address: String): Completable
}