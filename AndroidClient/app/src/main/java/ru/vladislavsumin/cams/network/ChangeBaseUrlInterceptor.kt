package ru.vladislavsumin.cams.network

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import ru.vladislavsumin.cams.app.Injector
import ru.vladislavsumin.cams.storage.CredentialStorage
import javax.inject.Inject

class ChangeBaseUrlInterceptor(private val baseHost: String) : Interceptor {

    @Inject
    lateinit var credentialStorage: CredentialStorage

    private lateinit var newUrl: HttpUrl

    init {
        Injector.inject(this)

        @Suppress("UNUSED_VARIABLE")
        val disposable = credentialStorage.observeServerAddress()
            .map { HttpUrl.parse(it) ?: HttpUrl.parse("http://localhost")!! }
            .subscribe { newUrl = it!! }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        var url = request.url()
        if (url.host() == baseHost) {
            url = url.newBuilder()
                .scheme(newUrl.scheme())
                .host(newUrl.host())
                .port(newUrl.port())
                .build()
            request = request.newBuilder()
                .url(url)
                .build()
        }
        return chain.proceed(request)
    }

}