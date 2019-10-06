package ru.vladislavsumin.cams.network.api

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*
import ru.vladislavsumin.cams.dto.CameraDTO

interface CamsApi {

    @GET("api/v1/cams")
    fun getAll(@Query("includeDeleted") includeDeleted: Boolean = false): Single<List<CameraDTO>>

    @DELETE("api/v1/cams")
    fun delete(@Query("id") id: Long): Completable

    @PUT("api/v1/cams")
    fun add(@Body cam: CameraDTO): Completable
}