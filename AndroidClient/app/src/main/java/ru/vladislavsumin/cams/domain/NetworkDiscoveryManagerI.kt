package ru.vladislavsumin.cams.domain

import io.reactivex.Completable

interface NetworkDiscoveryManagerI {
    fun scan(): Completable
}