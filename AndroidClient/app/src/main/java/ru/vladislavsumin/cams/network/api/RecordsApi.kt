package ru.vladislavsumin.cams.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.vladislavsumin.cams.entity.Record

interface RecordsApi {
    @GET("api/v1/records")
    fun getAll(): Single<List<Record>>//TODO

    @POST("api/v1/records/save")
    fun save(@Query("id") id: Long, @Query("name") name: String?): Single<Record>

    @POST("api/v1/records/delete")
    fun delete(@Query("id") id: Long): Single<Record>
}