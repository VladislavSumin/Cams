package ru.vladislavsumin.cams.domain

import io.reactivex.Completable
import ru.vladislavsumin.cams.network.api.AboutApi
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionManager @Inject constructor(
        private val aboutApi: AboutApi
) {
    fun checkConnection(address: String): Completable {
        return aboutApi.about("$address/api/v1/about/")
                .flatMapCompletable {
                    if (it["connection_state"] == "ok") Completable.complete()
                    else Completable.error(Exception("Server send wrong answer: $it"))
                }
    }
}