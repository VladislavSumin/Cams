package ru.vladislavsumin.cams.domain.impl

import io.reactivex.Completable
import ru.vladislavsumin.cams.domain.interfaces.ConnectionManagerI
import ru.vladislavsumin.cams.network.api.AboutApi
import java.lang.Exception

class ConnectionManager(private val aboutApi: AboutApi) : ConnectionManagerI {
    override fun checkConnection(address: String): Completable {
        return aboutApi.about("$address/api/v1/about/")
                .flatMapCompletable {
                    if (it["connection_state"] == "ok") Completable.complete()
                    else Completable.error(Exception("Server send wrong answer: $it"))
                }
    }
}