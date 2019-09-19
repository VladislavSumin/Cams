package ru.vladislavsumin.cams.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface AboutApi {
    @GET
    fun about(@Url url: String): Single<Map<String, String>>
}