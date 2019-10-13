package ru.vladislavsumin.cams.domain.interfaces

import io.reactivex.Observable
import ru.vladislavsumin.cams.dto.ServerInfoDto

interface NetworkDiscoveryManagerI {
    fun scan(): Observable<List<ServerInfoDto>>
}