package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Observable


interface NetworkManagerI {
    fun observeNetworkConnected(): Observable<Boolean>
}