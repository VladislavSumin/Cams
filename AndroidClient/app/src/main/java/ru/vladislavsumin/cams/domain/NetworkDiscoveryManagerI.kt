package ru.vladislavsumin.cams.domain

import io.reactivex.Observable
import ru.vladislavsumin.cams.dto.ServerInfoDTO

interface NetworkDiscoveryManagerI {
    fun scan(): Observable<List<ServerInfoDTO>>
}